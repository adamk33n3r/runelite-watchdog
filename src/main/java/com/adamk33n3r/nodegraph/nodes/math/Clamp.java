package com.adamk33n3r.nodegraph.nodes.math;

import com.adamk33n3r.nodegraph.VarInput;
import lombok.Getter;

@Getter
public class Clamp extends MathNode {
    private final VarInput<Number> value = new VarInput<>(this, "Value", Number.class, 0d);
    private final VarInput<Number> min = new VarInput<>(this, "Min", Number.class, 0d);
    private final VarInput<Number> max = new VarInput<>(this, "Max", Number.class, 1d);

    public Clamp() {
        this.value.onChange(v -> this.process());
        this.min.onChange(v -> this.process());
        this.max.onChange(v -> this.process());
        reg(this.value);
        reg(this.min);
        reg(this.max);
    }

    @Override
    public void process() {
        double v = this.value.getValue().doubleValue();
        double lo = this.min.getValue().doubleValue();
        double hi = this.max.getValue().doubleValue();
        if (lo > hi) {
            double tmp = lo; lo = hi; hi = tmp;
        }
        this.getResult().setValue(Math.max(lo, Math.min(hi, v)));
    }
}
