package com.adamk33n3r.runelite.watchdog.nodegraph;

import lombok.Setter;

@Setter
public class VarInput<T> extends Var<T> {
    private Connection<T> connection;
    public VarInput(Node node, String name, Class<T> type, T initialValue) {
        super(node, name, type, initialValue);
    }

    public void removeConnection() {
        this.connection = null;
    }

    public T getValue() {
        this.receive();
        return this.value;
    }

    public void receive() {
        if (this.connection == null) {
            return;
        }

        this.value = this.connection.get();
    }
}
