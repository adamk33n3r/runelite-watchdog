package com.adamk33n3r.nodegraph.nodes.utility;

import com.adamk33n3r.nodegraph.Node;
import com.adamk33n3r.nodegraph.VarInput;
import com.adamk33n3r.nodegraph.VarOutput;
import lombok.Getter;

@Getter
public class ToStringNode extends Node {
    private final VarInput<Object> value = new VarInput<>(this, "Value", Object.class, "");
    private final VarOutput<String> result = new VarOutput<>(this, "Result", String.class, "");

    public ToStringNode() {
        this.value.onChange(v -> this.process());
        reg(this.value);
        reg(this.result);
    }

    @Override
    public void process() {
        Object v = this.value.getValue();
        this.result.setValue(v != null ? v.toString() : "null");
    }
}
