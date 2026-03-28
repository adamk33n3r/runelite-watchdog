package com.adamk33n3r.nodegraph;

import lombok.Getter;
import lombok.Setter;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

public abstract class Node {
    @Getter
    private final UUID id = UUID.randomUUID();
    @Getter @Setter
    private int x, y;

    @Getter
    protected final Map<String, VarInput<?>> inputs = new LinkedHashMap<>();
    @Getter
    protected final Map<String, VarOutput<?>> outputs = new LinkedHashMap<>();

    protected void reg(VarInput<?> v) {
        inputs.put(v.getName(), v);
    }

    protected void reg(VarOutput<?> v) {
        outputs.put(v.getName(), v);
    }

    public void process() {}
}
