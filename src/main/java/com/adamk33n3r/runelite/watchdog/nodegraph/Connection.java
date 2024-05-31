package com.adamk33n3r.runelite.watchdog.nodegraph;

import lombok.Getter;

@Getter
public class Connection<T> {
    private final VarOutput<T> output;
    private final VarInput<T> input;

    public Connection(VarOutput<T> output, VarInput<T> input) {
        this.output = output;
        this.input = input;
        this.output.addConnection(this);
        this.input.setConnection(this);
    }

    public void send(T value) {
        this.input.value = value;
    }

    public T get() {
        return this.output.value;
    }

    public void remove() {
        this.output.removeConnection(this);
        this.input.removeConnection();
    }
}
