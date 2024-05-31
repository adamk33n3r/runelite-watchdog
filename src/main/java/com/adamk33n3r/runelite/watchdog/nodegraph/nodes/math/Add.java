package com.adamk33n3r.runelite.watchdog.nodegraph.nodes.math;

import com.adamk33n3r.runelite.watchdog.nodegraph.Node;
import com.adamk33n3r.runelite.watchdog.nodegraph.VarInput;
import com.adamk33n3r.runelite.watchdog.nodegraph.VarOutput;
import lombok.Getter;

@Getter
public class Add extends Node {
    private final VarInput<Number> a = new VarInput<>(this, "A", Number.class, 0);
    private final VarInput<Number> b = new VarInput<>(this, "B",Number.class, 0);
    private final VarOutput<Number> result = new VarOutput<>(this, "Result", Number.class, 0);

    public Add() {
        this.inputs.add(this.a);
        this.inputs.add(this.b);
        this.outputs.add(this.result);
    }

    @Override
    public void process() {
        this.result.setValue(this.a.getValue().doubleValue() + this.b.getValue().doubleValue());
    }
}
