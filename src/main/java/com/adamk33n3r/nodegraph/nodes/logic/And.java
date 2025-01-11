package com.adamk33n3r.nodegraph.nodes.logic;

import com.adamk33n3r.nodegraph.Node;
import com.adamk33n3r.nodegraph.VarInput;
import com.adamk33n3r.nodegraph.VarOutput;
import lombok.Getter;

@Getter
public class And extends Node {
    private final VarInput<Boolean> a = new VarInput<>(this, "A", Boolean.class, false);
    private final VarInput<Boolean> b = new VarInput<>(this, "B", Boolean.class, false);
    private final VarOutput<Boolean> result = new VarOutput<>(this, "Result", Boolean.class, false);

    @Override
    public void process() {
        this.result.setValue(this.a.getValue() && this.b.getValue());
    }
}
