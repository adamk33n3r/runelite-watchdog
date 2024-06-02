package com.adamk33n3r.runelite.watchdog.nodegraph;

import lombok.Getter;
import lombok.Setter;

@Getter
public abstract class Var<T> {
    @Setter
    protected T value;
    protected final Class<T> type;
    protected String name;
    protected Node node;

    protected Var(Node node, String name, Class<T> type, T initialValue) {
        this.node = node;
        this.name = name;
        this.type = type;
        this.value = initialValue;
    }

}
