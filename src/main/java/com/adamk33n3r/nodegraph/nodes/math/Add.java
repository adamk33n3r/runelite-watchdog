package com.adamk33n3r.nodegraph.nodes.math;

import com.adamk33n3r.nodegraph.Node;
import com.adamk33n3r.nodegraph.VarInput;
import com.adamk33n3r.nodegraph.VarOutput;
import lombok.Getter;

@Getter
public class Add extends Node {
    private final VarInput<Number> a = new VarInput<>(this, "A", Number.class, 0);
    private final VarInput<Number> b = new VarInput<>(this, "B",Number.class, 0);
    private final VarOutput<Number> result = new VarOutput<>(this, "Result", Number.class, 0);

    @Override
    public void process() {
        this.result.setValue(this.a.getValue().doubleValue() + this.b.getValue().doubleValue());
    }
}
