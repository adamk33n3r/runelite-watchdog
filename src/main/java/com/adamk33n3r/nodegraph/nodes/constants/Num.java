package com.adamk33n3r.nodegraph.nodes.constants;

import com.adamk33n3r.nodegraph.Node;
import com.adamk33n3r.nodegraph.VarOutput;
import lombok.Getter;

@Getter
public class Num extends Node {
    private final VarOutput<Number> value = new VarOutput<>(this, "Value", Number.class, 0);

    public void setValue(int value) {
        this.value.setValue(value);
    }
}
