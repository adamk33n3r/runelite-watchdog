package com.adamk33n3r.nodegraph.nodes.logic;

import com.adamk33n3r.nodegraph.Node;
import com.adamk33n3r.nodegraph.VarInput;
import com.adamk33n3r.nodegraph.VarOutput;
import com.adamk33n3r.runelite.watchdog.Displayable;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
public class BooleanGate extends Node {
    @Getter
    @AllArgsConstructor
    public enum Op implements Displayable {
        AND("AND", "Both inputs must be true"),
        OR("OR", "At least one input must be true");

        private final String name;
        private final String tooltip;

        @Override
        public String toString() {
            return this.name;
        }
    }

    private final VarInput<Op> op = new VarInput<>(this, "Op", Op.class, Op.AND);
    private final VarInput<Boolean> a = new VarInput<>(this, "A", Boolean.class, false);
    private final VarInput<Boolean> b = new VarInput<>(this, "B", Boolean.class, false);
    private final VarOutput<Boolean> result = new VarOutput<>(this, "Result", Boolean.class, false);

    public BooleanGate() {
        this.op.onChange(op -> this.process());
        this.a.onChange(a -> this.process());
        this.b.onChange(b -> this.process());

        reg(this.op);
        reg(this.a);
        reg(this.b);
        reg(this.result);
    }

    @Override
    public void process() {
        switch (this.op.getValue()) {
            case AND:
                this.result.setValue(this.a.getValue() && this.b.getValue());
                break;
            case OR:
                this.result.setValue(this.a.getValue() || this.b.getValue());
                break;
        }
    }
}
