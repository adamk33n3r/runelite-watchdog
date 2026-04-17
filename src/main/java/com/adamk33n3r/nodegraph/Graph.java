package com.adamk33n3r.nodegraph;

import com.adamk33n3r.nodegraph.nodes.ActionNode;
import com.adamk33n3r.nodegraph.nodes.flow.DelayNode;
import com.adamk33n3r.nodegraph.nodes.TriggerNode;
import com.adamk33n3r.nodegraph.nodes.flow.Branch;
import com.adamk33n3r.runelite.watchdog.alerts.Alert;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
     */
    public void executeExecChain(Node startNode, String[] captureGroups) {
        List<Node> nodesToProcess = new ArrayList<>();
        nodesToProcess.add(startNode);

        while (!nodesToProcess.isEmpty()) {
            Node node = nodesToProcess.remove(0);

            if (node instanceof TriggerNode && !((TriggerNode) node).getEnabled().getValue()) {
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
                    .map(c -> c.getInput().getNode())
                    .distinct()
                    .forEach(nodesToProcess::add);
                continue;
            }

            if (node instanceof DelayNode) {
                int delayMs = ((DelayNode) node).getDelayMs().getValue().intValue();
                if (delayMs > 0) {
                    List<Node> downstream = this.getExecDownstream(node);
                    Thread t = new Thread(() -> {
                        try {
                            Thread.sleep(delayMs);
                        } catch (InterruptedException e) {
                            return;
                        }
                        try {
                            downstream.forEach(n -> this.executeExecChain(n, captureGroups));
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

            nodesToProcess.addAll(this.getExecDownstream(node));
        }
    }

    private List<Node> getExecDownstream(Node node) {
        return this.connections.stream()
            .filter(c -> c.getOutput().getNode() == node && c.getOutput().getType() == ExecSignal.class)
            .map(c -> c.getInput().getNode())
            .distinct()
            .collect(Collectors.toList());
    }
}
