package com.adamk33n3r.nodegraph.nodes.constants;

import com.adamk33n3r.nodegraph.Node;
import com.adamk33n3r.nodegraph.VarInput;
import com.adamk33n3r.nodegraph.VarOutput;
import lombok.Getter;

@Getter
public class Bool extends Node {
    private final VarInput<Boolean> valueIn = new VarInput<>(this, "Value", Boolean.class, true);
    private final VarOutput<Boolean> valueOut = new VarOutput<>(this, "Value", Boolean.class, true);

    public void setValue(boolean value) {
        this.valueOut.setValue(value);
    }

    @Override
    public void process() {
        this.valueOut.setValue(this.valueIn.getConnection() != null ? this.valueIn.getValue() : this.valueOut.getValue());
    }
}
