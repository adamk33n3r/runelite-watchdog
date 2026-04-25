package com.adamk33n3r.nodegraph.nodes.math;

import com.adamk33n3r.nodegraph.VarInput;
import lombok.Getter;

@Getter
public class Floor extends MathNode {
    private final VarInput<Number> value = new VarInput<>(this, "Value", Number.class, 0);

    public Floor() {
        this.value.onChange(v -> this.process());
        reg(this.value);
    }

    @Override
    public void process() {
        this.getResult().setValue(Math.floor(this.value.getValue().doubleValue()));
    }
}
