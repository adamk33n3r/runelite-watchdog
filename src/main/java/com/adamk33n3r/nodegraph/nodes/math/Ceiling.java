package com.adamk33n3r.nodegraph.nodes.math;

import com.adamk33n3r.nodegraph.VarInput;
import lombok.Getter;

@Getter
public class Ceiling extends MathNode {
    private final VarInput<Number> value = new VarInput<>(this, "Value", Number.class, 0d);

    public Ceiling() {
        this.value.onChange(v -> this.process());
        reg(this.value);
    }

    @Override
    public void process() {
        this.getResult().setValue(Math.ceil(this.value.getValue().doubleValue()));
    }
}
