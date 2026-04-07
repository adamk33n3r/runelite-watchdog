package com.adamk33n3r.nodegraph.nodes.constants;

import com.adamk33n3r.nodegraph.VarOutput;
import com.adamk33n3r.nodegraph.nodes.VariableNode;
import lombok.Getter;

@Getter
public class Num extends VariableNode {
    private final VarOutput<Number> value = new VarOutput<>(this, "Value", Number.class, 0);

    public Num() {
        reg(this.value);
    }

    public void setValue(int value) {
        this.value.setValue(value);
    }
}
