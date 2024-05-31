package com.adamk33n3r.runelite.watchdog.nodegraph;

import com.adamk33n3r.runelite.watchdog.nodegraph.nodes.TriggerNode;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class Graph {
    private final List<Node> nodes = new ArrayList<>();
    private final List<Connection<?>> connections = new ArrayList<>();

    public void add(Node node) {
        this.nodes.add(node);
    }

    public void remove(Node node) {
        this.nodes.remove(node);
    }

    public <T> void connect(VarOutput<T> output, VarInput<T> input) {
        if (output.getConnections().stream().anyMatch(c -> c.getInput().equals(input))) {
            log.warn("Connection already exists");
            return;
        }

        Connection<?> connection = new Connection<>(output, input);
        this.connections.add(connection);
    }

    @Override
    public String toString() {
        // Print out directed graph from nodes through connections
        StringBuilder sb = new StringBuilder();
        for (Node node : this.nodes) {
            if (!(node instanceof TriggerNode)) {
                continue;
            }
            sb.append(node.getClass().getSimpleName()).append(":\n");
            for (Connection<?> connection : this.connections) {
                if (connection.getOutput().getNode() == node) {
                    sb.append("  ")
                        .append(connection.getOutput().getName())
                        .append(" -> ")
                        .append(connection.getInput().getName())
                        .append(" -> ")
                        .append(connection.getInput().getNode().getClass().getSimpleName())
                        .append("\n");
                }
            }
        }
        return sb.toString();
    }
}
