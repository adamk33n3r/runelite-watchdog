package com.adamk33n3r.nodegraph.nodes.math;

import com.adamk33n3r.nodegraph.VarInput;
import lombok.Getter;

@Getter
public class Round extends MathNode {
    private final VarInput<Number> value = new VarInput<>(this, "Value", Number.class, 0);

    public Round() {
        this.value.onChange(v -> this.process());
        reg(this.value);
    }

    @Override
    public void process() {
        this.getResult().setValue((double) Math.round(this.value.getValue().doubleValue()));
    }
}
