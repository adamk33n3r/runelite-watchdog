package com.adamk33n3r.nodegraph;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class VarInput<T> extends Var<T> {
    @Getter
    private Connection<T> connection;
    private final List<Consumer<T>> onChange = new ArrayList<>();
    private final List<Consumer<Boolean>> onConnectChange = new ArrayList<>();

    public VarInput(Node node, String name, Class<T> type, T initialValue) {
        super(node, name, type, initialValue);
    }

    public void setConnection(Connection<T> connection) {
        assert connection != null;
        this.connection = connection;
        this.onConnectChange.forEach((consumer) -> consumer.accept(true));
    }

    public void removeConnection() {
        this.connection = null;
        this.onConnectChange.forEach((consumer) -> consumer.accept(false));
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
        if (this.connection == null) {
            return;
        }

        this.value = this.connection.get();
        this.onChange.forEach((consumer) -> consumer.accept(this.value));
    }

    public void onChange(Consumer<T> onChange) {
        this.onChange.add(onChange);
    }

    public void onConnectChange(Consumer<Boolean> onConnectChange) {
        this.onConnectChange.add(onConnectChange);
    }

    public VarOutput<T> toOutput() {
        return new VarOutput<>(this.node, this.name, this.type, this.value);
    }
}
