package com.adamk33n3r.nodegraph.nodes.math;

import com.adamk33n3r.nodegraph.VarInput;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public class Divide extends MathNode {
    private final VarInput<Number> a = new VarInput<>(this, "A", Number.class, 1d);
    private final VarInput<Number> b = new VarInput<>(this, "B", Number.class, 1d);

    public Divide() {
        this.a.onChange(a -> this.process());
        this.b.onChange(b -> this.process());
        reg(this.a);
        reg(this.b);
    }

    @Override
    public void process() {
        double divisor = this.b.getValue().doubleValue();
        if (divisor == 0.0) {
            log.warn("Divide by zero — returning 0.0");
            this.getResult().setValue(0.0);
            return;
        }
        this.getResult().setValue(this.a.getValue().doubleValue() / divisor);
    }
}
