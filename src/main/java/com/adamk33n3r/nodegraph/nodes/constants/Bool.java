package com.adamk33n3r.nodegraph.nodes.constants;

import com.adamk33n3r.nodegraph.Node;
import com.adamk33n3r.nodegraph.VarOutput;
import lombok.Getter;

@Getter
public class Bool extends Node {
    private final VarOutput<Boolean> value = new VarOutput<>(this, "Value", Boolean.class, true);

    public void setValue(boolean value) {
        this.value.setValue(value);
    }

    @Override
    public void process() {
        this.value.setValue(this.value.getValue());
    }
}
