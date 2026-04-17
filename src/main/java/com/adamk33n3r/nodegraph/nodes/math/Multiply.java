package com.adamk33n3r.nodegraph.nodes.math;

import com.adamk33n3r.nodegraph.VarInput;
import lombok.Getter;

@Getter
public class Multiply extends MathNode {
    private final VarInput<Number> a = new VarInput<>(this, "A", Number.class, 0);
    private final VarInput<Number> b = new VarInput<>(this, "B", Number.class, 0);

    public Multiply() {
        this.a.onChange(a -> this.process());
        this.b.onChange(b -> this.process());
        reg(this.a);
        reg(this.b);
    }

    @Override
    public void process() {
        this.getResult().setValue(this.a.getValue().doubleValue() * this.b.getValue().doubleValue());
    }
}
