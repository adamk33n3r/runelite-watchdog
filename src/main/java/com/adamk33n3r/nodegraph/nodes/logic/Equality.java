package com.adamk33n3r.nodegraph.nodes.logic;

import com.adamk33n3r.nodegraph.Node;
import com.adamk33n3r.nodegraph.VarInput;
import com.adamk33n3r.nodegraph.VarOutput;
import com.adamk33n3r.runelite.watchdog.Displayable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
public class Equality extends Node {
    @Getter
    @AllArgsConstructor
    public enum Op implements Displayable {
        EQUAL("==", "Equal to"),
        NOT_EQUAL("!=", "Not equal to"),
        GREATER(">", "Greater than"),
        LESS("<", "Less than"),
        GREATER_EQUAL(">=", "Greater than or equal to"),
        LESS_EQUAL("<=", "Less than or equal to");

        private final String name;
        private final String tooltip;

        @Override
        public String toString() {
            return this.name;
        }
    }

    @Setter
    private Op op = Op.EQUAL;

    private final VarInput<Number> a = new VarInput<>(this, "A", Number.class, 0);
    private final VarInput<Number> b = new VarInput<>(this, "B", Number.class, 0);
    private final VarOutput<Boolean> result = new VarOutput<>(this, "Result", Boolean.class, false);

    public Equality() {
        reg(this.a);
        reg(this.b);
        reg(this.result);
    }

    @Override
    public void process() {
        double aVal = this.a.getValue().doubleValue();
        double bVal = this.b.getValue().doubleValue();
        boolean res;
        switch (this.op) {
            case EQUAL:         res = aVal == bVal; break;
            case NOT_EQUAL:     res = aVal != bVal; break;
            case GREATER:       res = aVal > bVal; break;
            case LESS:          res = aVal < bVal; break;
            case GREATER_EQUAL: res = aVal >= bVal; break;
            case LESS_EQUAL:    res = aVal <= bVal; break;
            default:            res = false; break;
        }
        this.result.setValue(res);
    }
}
