package com.adamk33n3r.nodegraph;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class VarInput<T> extends Var<T> {
    private final List<Connection<T>> connections = new ArrayList<>();
    @Getter @Setter
    private boolean allowMultipleConnections = false;
    private final List<Consumer<T>> onChange = new ArrayList<>();
    private final List<Consumer<Boolean>> onConnectChange = new ArrayList<>();

    public VarInput(Node node, String name, Class<T> type, T initialValue) {
        super(node, name, type, initialValue);
    }

    public Connection<T> getConnection() {
        return this.connections.isEmpty() ? null : this.connections.get(this.connections.size() - 1);
    }

    public void addConnection(Connection<T> connection) {
        assert connection != null;
        this.connections.add(connection);
        this.fireConnectChange(true);
    }

    public void removeConnection(Connection<T> connection) {
        this.connections.remove(connection);
        if (this.connections.isEmpty()) {
            this.fireConnectChange(false);
        }
    }

    public void fireConnectChange(boolean connect) {
        this.onConnectChange.forEach((consumer) -> consumer.accept(connect));
    }

    @Override
    public T getValue() {
        this.receive();
        return this.value;
    }

    @Override
    public void setValue(T value) {
        this.value = value;
        this.onChange.forEach((consumer) -> consumer.accept(this.value));
    }

    public void receive() {
        if (this.connections.isEmpty()) {
            return;
        }

        // For multi-connection inputs, value is set via push (send()) — don't overwrite with stale pull data
        if (this.allowMultipleConnections) {
            return;
        }

        this.value = this.connections.get(0).get();
        this.onChange.forEach((consumer) -> consumer.accept(this.value));
    }

    public Runnable onChange(Consumer<T> onChange) {
        this.onChange.add(onChange);
        return () -> this.onChange.remove(onChange);
    }

    public Runnable onConnectChange(Consumer<Boolean> onConnectChange) {
        this.onConnectChange.add(onConnectChange);
        return () -> this.onConnectChange.remove(onConnectChange);
    }

    public VarOutput<T> toOutput() {
        return new VarOutput<>(this.node, this.name, this.type, this.value);
    }
}
