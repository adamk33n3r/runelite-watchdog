package com.adamk33n3r.runelite.watchdog.nodegraph.nodes.logic;

import com.adamk33n3r.runelite.watchdog.nodegraph.Node;
import com.adamk33n3r.runelite.watchdog.nodegraph.VarInput;
import com.adamk33n3r.runelite.watchdog.nodegraph.VarOutput;
import lombok.Getter;

@Getter
public class And extends Node {
    private final VarInput<Boolean> a = new VarInput<>(this, "A", Boolean.class, false);
    private final VarInput<Boolean> b = new VarInput<>(this, "B", Boolean.class, false);
    private final VarOutput<Boolean> result = new VarOutput<>(this, "Result", Boolean.class, false);

    public And() {
        this.inputs.add(this.a);
        this.inputs.add(this.b);
        this.outputs.add(this.result);
    }

    @Override
    public void process() {
        this.result.setValue(this.a.getValue() && this.b.getValue());
    }
}