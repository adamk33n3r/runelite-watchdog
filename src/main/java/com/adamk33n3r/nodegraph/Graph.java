package com.adamk33n3r.nodegraph;

import com.adamk33n3r.nodegraph.nodes.NotificationNode;
import com.adamk33n3r.nodegraph.nodes.TriggerNode;
import com.adamk33n3r.runelite.watchdog.alerts.Alert;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public class Graph {
    @Getter
    private final List<Node> nodes = new ArrayList<>();
    @Getter
    private final List<Connection<?>> connections = new ArrayList<>();

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

    public <T> boolean connect(VarOutput<T> output, VarInput<T> input) {
        if (output.getConnections().stream().anyMatch(c -> c.getInput().equals(input))) {
            log.warn("Connection already exists");
            return false;
        }

        // There is an existing connection to this input, disconnect it
        if (input.getConnection() != null) {
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
     * via connections to the enabled input.
     */
    public List<NotificationNode> getReachableNotificationsFromTrigger(TriggerNode triggerNode) {
        List<NotificationNode> reachableNotifications = new ArrayList<>();
        List<Node> nodesToProcess = new ArrayList<>();
        nodesToProcess.add(triggerNode);
        while (!nodesToProcess.isEmpty()) {
            Node node = nodesToProcess.remove(0);
            if (node instanceof NotificationNode) {
                NotificationNode notification = ((NotificationNode) node);
                if (notification.getEnabled().getValue()) {
                    reachableNotifications.add(notification);
                }
                continue;
            }
            if (node instanceof TriggerNode && !((TriggerNode) node).getEnabled().getValue()) {
                continue;
            }
            this.connections.stream()
                .filter(c -> c.getOutput().getNode() == node)
                .map(c -> c.getInput().getNode())
                .distinct()
                .forEach(nodesToProcess::add);
        }
        return reachableNotifications;
    }
}
