package com.adamk33n3r.nodegraph;

import com.adamk33n3r.nodegraph.nodes.ActionNode;
import com.adamk33n3r.nodegraph.nodes.flow.Counter;
import com.adamk33n3r.nodegraph.nodes.flow.DelayNode;
import com.adamk33n3r.nodegraph.nodes.flow.TimerNode;
import com.adamk33n3r.nodegraph.nodes.TriggerNode;
import com.adamk33n3r.nodegraph.nodes.flow.Branch;
import com.adamk33n3r.runelite.watchdog.alerts.Alert;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.AbstractMap;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public class Graph {
    @Getter
    private final List<Node> nodes = new ArrayList<>();
    @Getter
    private final List<Connection<?>> connections = new ArrayList<>();

    private Consumer<Throwable> onError;

    // Lazy-initialised, daemon-threaded scheduler — shared across all async nodes in this graph.
    private ScheduledExecutorService scheduler;

    private ScheduledExecutorService getScheduler() {
        if (this.scheduler == null) {
            this.scheduler = Executors.newScheduledThreadPool(1, r -> {
                Thread t = new Thread(r, "nodegraph-timer");
                t.setDaemon(true);
                return t;
            });
        }
        return this.scheduler;
    }

    private Runnable wrap(Runnable r) {
        return () -> {
            try {
                r.run();
            } catch (Throwable e) {
                log.error("Exception in timer exec chain", e);
                if (this.onError != null) this.onError.accept(e);
            }
        };
    }

    public void setOnError(Consumer<Throwable> onError) {
        this.onError = onError;
    }

    public void add(Node node) {
        this.nodes.add(node);
    }

    public void remove(Node node) {
        this.nodes.remove(node);
        List<Connection<?>> toRemove = this.connections.stream()
            .filter(c -> c.getOutput().getNode().equals(node) || c.getInput().getNode().equals(node))
            .collect(Collectors.toList());
        toRemove.forEach(Connection::remove);
        this.connections.removeAll(toRemove);
    }

    private boolean wouldCreateCycle(Node source, Node dest) {
        Set<Node> visited = new HashSet<>();
        Deque<Node> stack = new ArrayDeque<>();
        stack.push(source);
        while (!stack.isEmpty()) {
            Node node = stack.pop();
            if (node == dest) return true;
            if (!visited.add(node)) continue;
            this.connections.stream()
                .filter(c -> c.getInput().getNode() == node)
                .map(c -> c.getOutput().getNode())
                .forEach(stack::push);
        }
        return false;
    }

    public <T> boolean connect(VarOutput<T> output, VarInput<T> input) {
        if (output.getConnections().stream().anyMatch(c -> c.getInput().equals(input))) {
            log.warn("Connection already exists");
            return false;
        }

        if (wouldCreateCycle(output.getNode(), input.getNode())) {
            log.warn("Connection would create a cycle");
            return false;
        }

        // There is an existing connection to this input, disconnect it (unless multi-connection is allowed)
        if (!input.isAllowMultipleConnections() && input.getConnection() != null) {
            this.disconnect(input.getConnection().getOutput(), input);
        }

        Connection<?> connection = new Connection<>(output, input);
        this.connections.add(connection);
        return true;
    }

    public void disconnect(VarOutput<?> output, VarInput<?> input) {
        this.connections.stream()
            .filter(c -> c.getOutput().equals(output) && c.getInput().equals(input))
            .findFirst()
            .ifPresent(c -> {
                c.remove();
                this.connections.remove(c);
            });
    }

    public <T extends Node> Stream<T> getNodesOfType(Class<T> nodeClass) {
        return nodes.stream().filter(nodeClass::isInstance).map(nodeClass::cast);
    }

    public Stream<TriggerNode> getTriggerNodes() {
        return this.nodes.stream()
            .filter(n -> n instanceof TriggerNode)
            .map(n -> (TriggerNode) n);
    }

    /**
     * Returns all TriggerNodes whose embedded alert is an instance of the given alert class.
     */
    public <T extends Alert> Stream<TriggerNode> getTriggerNodesOfType(Class<T> alertClass) {
        return nodes.stream()
            .filter(n -> n instanceof TriggerNode)
            .map(n -> (TriggerNode) n)
            .filter(tn -> alertClass.isInstance(tn.getAlert()));
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Node node : this.nodes) {
            if (!(node instanceof TriggerNode)) {
                continue;
            }
            sb.append(node.getClass().getSimpleName()).append(":\n");
            for (Connection<?> connection : this.connections) {
                if (connection.getOutput().getNode() == node) {
                    sb.append("  ")
                        .append(connection.getOutput().getNode().getClass().getSimpleName())
                        .append(":")
                        .append(connection.getOutput().getName())
                        .append(" -> ")
                        .append(connection.getInput().getName())
                        .append(":")
                        .append(connection.getInput().getNode().getClass().getSimpleName())
                        .append("\n");
                }
            }
            if (this.connections.isEmpty()) {
                sb.append("  No connections\n");
            }
        }
        return sb.toString();
    }

    public void process(Node node) {
        node.process();
        this.connections.stream()
            .filter(c -> c.getOutput().getNode() == node)
            .map(c -> c.getInput().getNode())
            .distinct()
            .forEach(this::process);
    }

    /**
     * Finds and returns all notifications recursively reachable from triggerNode
     * via exec connections, including those behind ActionNodes and DelayNodes.
     */
    public List<ActionNode> getReachableActionsFromTrigger(TriggerNode triggerNode) {
        List<ActionNode> reachableNotifications = new ArrayList<>();
        List<Node> nodesToProcess = new ArrayList<>();
        nodesToProcess.add(triggerNode);
        while (!nodesToProcess.isEmpty()) {
            Node node = nodesToProcess.remove(0);
            if (node instanceof TriggerNode && !((TriggerNode) node).getEnabled().getValue()) {
                continue;
            }
            if (node instanceof ActionNode) {
                ActionNode action = (ActionNode) node;
                if (action.getEnabled().getValue()) {
                    reachableNotifications.add(action);
                }
                // Fall through to also follow this node's exec output
            }
            nodesToProcess.addAll(this.getExecDownstream(node));
        }
        return reachableNotifications;
    }

    /**
     * Traverses the exec chain starting from startNode, firing enabled ActionNodes
     * immediately and spawning daemon threads to handle DelayNodes.
     *
     * The BFS queue carries (node, arrivingInput) pairs so that stateful nodes like
     * Counter can distinguish which of their exec inputs was triggered.
     * arrivingInput is null only for the startNode entry.
     */
    public void executeExecChain(Node startNode, String[] captureGroups) {
        List<Map.Entry<Node, VarInput<ExecSignal>>> nodesToProcess = new ArrayList<>();
        nodesToProcess.add(new AbstractMap.SimpleEntry<>(startNode, null));
        this.executeExecChainBFS(nodesToProcess, captureGroups);
    }

    @SuppressWarnings("unchecked")
    private void executeExecChainBFS(List<Map.Entry<Node, VarInput<ExecSignal>>> nodesToProcess, String[] captureGroups) {
        while (!nodesToProcess.isEmpty()) {
            Map.Entry<Node, VarInput<ExecSignal>> entry = nodesToProcess.remove(0);
            Node node = entry.getKey();
            VarInput<ExecSignal> arrivingInput = entry.getValue();

            if (node instanceof TriggerNode && !((TriggerNode) node).getEnabled().getValue()) {
                continue;
            }

            if (node instanceof Counter) {
                Counter counter = (Counter) node;
                if (arrivingInput == counter.getReset()) {
                    counter.reset();
                    continue;  // reset is terminal — no exec output
                } else {
                    counter.increment();
                    // fall through to add exec-downstream
                }
            }

            if (node instanceof TimerNode) {
                TimerNode timer = (TimerNode) node;
                if (arrivingInput == timer.getReset()) {
                    timer.cancelCurrentFuture();
                    continue; // reset is terminal — no exec output
                }
                // Exec arrival — cancel any running future (restart semantics) then schedule
                timer.cancelCurrentFuture();
                timer.getCancelled().set(false);
                int durationMs = timer.getDurationMs().getValue().intValue();
                boolean pulse = Boolean.TRUE.equals(timer.getPulse().getValue());
                if (durationMs <= 0) {
                    // degenerate: fire execOut immediately as a pass-through
                    this.executeFromOutput(timer.getExecOut(), captureGroups);
                    continue;
                }
                ScheduledExecutorService sched = this.getScheduler();
                Runnable fireExec = this.wrap(() -> this.executeFromOutput(timer.getExecOut(), captureGroups));
                Runnable firePulse = this.wrap(() -> this.executeFromOutput(timer.getPulseOut(), captureGroups));
                if (!pulse) {
                    timer.setCurrentFuture(sched.schedule(fireExec, durationMs, TimeUnit.MILLISECONDS));
                } else {
                    timer.setCurrentFuture(sched.schedule(this.wrap(() -> {
                        fireExec.run();
                        // Guard against Reset arriving between fireExec and scheduleAtFixedRate.
                        if (!timer.getCancelled().get()) {
                            timer.setCurrentFuture(sched.scheduleAtFixedRate(firePulse, durationMs, durationMs, TimeUnit.MILLISECONDS));
                        }
                    }), durationMs, TimeUnit.MILLISECONDS));
                }
                continue;
            }

            if (node instanceof ActionNode) {
                ActionNode action = (ActionNode) node;
                if (action.getEnabled().getValue()) {
                    try {
                        action.fire(captureGroups);
                    } catch (Throwable e) {
                        log.error("Exception in ActionNode execution", e);
                        if (this.onError != null) this.onError.accept(e);
                    }
                }
                // Fall through to follow exec downstream from this action
            }

            if (node instanceof Branch) {
                Branch branch = (Branch) node;
                boolean cond = branch.getCondition().getValue();
                VarOutput<ExecSignal> activeOut = cond ? branch.getExecTrue() : branch.getExecFalse();
                this.connections.stream()
                    .filter(c -> c.getOutput() == activeOut)
                    .map(c -> (Map.Entry<Node, VarInput<ExecSignal>>) new AbstractMap.SimpleEntry<>(
                        c.getInput().getNode(), (VarInput<ExecSignal>) c.getInput()))
                    .forEach(nodesToProcess::add);
                continue;
            }

            if (node instanceof DelayNode) {
                int delayMs = ((DelayNode) node).getDelayMs().getValue().intValue();
                if (delayMs > 0) {
                    List<Map.Entry<Node, VarInput<ExecSignal>>> downstream = this.getExecEntriesDownstream(node);
                    Thread t = new Thread(() -> {
                        try {
                            Thread.sleep(delayMs);
                        } catch (InterruptedException e) {
                            return;
                        }
                        try {
                            this.executeExecChainBFS(downstream, captureGroups);
                        } catch (Throwable e) {
                            log.error("Exception in delayed exec chain", e);
                            if (this.onError != null) this.onError.accept(e);
                        }
                    });
                    t.setDaemon(true);
                    t.start();
                    continue; // Don't add downstream to the immediate BFS queue
                }
                // delayMs == 0: fall through and add downstream immediately
            }

            nodesToProcess.addAll(this.getExecEntriesDownstream(node));
        }
    }

    private List<Node> getExecDownstream(Node node) {
        return this.connections.stream()
            .filter(c -> c.getOutput().getNode() == node && c.getOutput().getType() == ExecSignal.class)
            .map(c -> c.getInput().getNode())
            .distinct()
            .collect(Collectors.toList());
    }

    @SuppressWarnings("unchecked")
    private List<Map.Entry<Node, VarInput<ExecSignal>>> getExecEntriesDownstream(Node node) {
        return this.connections.stream()
            .filter(c -> c.getOutput().getNode() == node && c.getOutput().getType() == ExecSignal.class)
            .map(c -> (Map.Entry<Node, VarInput<ExecSignal>>) new AbstractMap.SimpleEntry<>(
                c.getInput().getNode(), (VarInput<ExecSignal>) c.getInput()))
            .collect(Collectors.toList());
    }

    // Routes execution through a specific exec output pin — used by TimerNode which has two exec outputs.
    @SuppressWarnings("unchecked")
    private void executeFromOutput(VarOutput<ExecSignal> output, String[] captureGroups) {
        List<Map.Entry<Node, VarInput<ExecSignal>>> entries = this.connections.stream()
            .filter(c -> c.getOutput() == output)
            .map(c -> (Map.Entry<Node, VarInput<ExecSignal>>) new AbstractMap.SimpleEntry<>(
                c.getInput().getNode(), (VarInput<ExecSignal>) c.getInput()))
            .collect(Collectors.toList());
        this.executeExecChainBFS(entries, captureGroups);
    }
}
