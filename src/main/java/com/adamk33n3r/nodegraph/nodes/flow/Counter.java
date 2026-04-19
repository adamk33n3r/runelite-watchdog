package com.adamk33n3r.nodegraph.nodes.flow;

import com.adamk33n3r.nodegraph.ExecSignal;
import com.adamk33n3r.nodegraph.Node;
import com.adamk33n3r.nodegraph.VarInput;
import com.adamk33n3r.nodegraph.VarOutput;
import lombok.Getter;

@Getter
public class Counter extends Node {
    private final VarInput<ExecSignal> exec = new VarInput<>(this, "Exec", ExecSignal.class, new ExecSignal(new String[0]));
    private final VarInput<ExecSignal> reset = new VarInput<>(this, "Reset", ExecSignal.class, new ExecSignal(new String[0]));
    private final VarInput<Number> count = new VarInput<>(this, "Count In (hidden)", Number.class, 0);
    private final VarOutput<ExecSignal> execOut = new VarOutput<>(this, "Exec", ExecSignal.class, new ExecSignal(new String[0]));
    private final VarOutput<Number> countOut = new VarOutput<>(this, "Count", Number.class, 0);

    private int value = 0;

    public Counter() {
        this.exec.setAllowMultipleConnections(true);
        this.reset.setAllowMultipleConnections(true);

        this.exec.onChange(val -> this.process());
        this.reset.onChange(val -> this.process());
        this.count.onChange(val -> this.process());

        reg(this.exec);
        reg(this.reset);
        reg(this.count);
        reg(this.execOut);
        reg(this.countOut);
    }

    // Called by Graph.executeExecChainBFS when the exec input fires
    public void increment() {
        this.value++;
        this.count.setValue(this.value);
    }

    // Called by Graph.executeExecChainBFS when the reset input fires
    public void reset() {
        this.value = 0;
        this.count.setValue(this.value);
    }

    public void initValue(int v) {
        this.value = v;
        this.count.setValue(v);
    }

    @Override
    public void process() {
        this.countOut.setValue(this.count.getValue());
        this.execOut.setValue(this.exec.getValue());
    }
}
