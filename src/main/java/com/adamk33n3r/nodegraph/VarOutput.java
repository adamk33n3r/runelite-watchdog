package com.adamk33n3r.nodegraph;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class VarOutput<T> extends Var<T> {
    // TODO: look into removing this and getting from graph. Have an issue that if an input is overwritten, this is still referring to that VarInput even though it's removed from Graph
    @Getter
    private final List<Connection<T>> connections = new ArrayList<>();
    private final List<Consumer<Boolean>> onConnectChange = new ArrayList<>();

    public VarOutput(Node node, String name, Class<T> type, T initialValue) {
        super(node, name, type, initialValue);
    }

    public void addConnection(Connection<T> connection) {
        this.connections.add(connection);
        this.onConnectChange.forEach((consumer) -> consumer.accept(true));
    }

    public void removeConnection(Connection<T> connection) {
        this.connections.remove(connection);
        if (this.connections.isEmpty()) {
            this.onConnectChange.forEach((consumer) -> consumer.accept(false));
        }
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

    public void onConnectChange(Consumer<Boolean> onConnectChange) {
        this.onConnectChange.add(onConnectChange);
    }
}
