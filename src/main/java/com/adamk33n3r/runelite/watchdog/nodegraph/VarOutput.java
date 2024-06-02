package com.adamk33n3r.runelite.watchdog.nodegraph;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class VarOutput<T> extends Var<T> {
    private final List<Connection<T>> connections = new ArrayList<>();

    public VarOutput(Node node, String name, Class<T> type, T initialValue) {
        super(node, name, type, initialValue);
    }

    public void addConnection(Connection<T> connection) {
        this.connections.add(connection);
    }

    public void removeConnection(Connection<T> connection) {
        this.connections.remove(connection);
    }

    @Override
    public void setValue(T value) {
        this.value = value;
        this.send();
    }

    private void send() {
        for (Connection<T> connection : this.connections) {
            connection.send(this.value);
        }
    }
}
