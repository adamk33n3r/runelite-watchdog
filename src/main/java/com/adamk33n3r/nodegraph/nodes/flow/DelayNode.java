package com.adamk33n3r.nodegraph.nodes.flow;

import com.adamk33n3r.nodegraph.ExecSignal;
import com.adamk33n3r.nodegraph.Node;
import com.adamk33n3r.nodegraph.VarInput;
import com.adamk33n3r.nodegraph.VarOutput;
import lombok.Getter;

@Getter
public class DelayNode extends Node {
    private final VarInput<ExecSignal> exec = new VarInput<>(this, "Exec", ExecSignal.class, new ExecSignal(new String[0]));
    private final VarInput<Number> delayMs = new VarInput<>(this, "Delay (ms)", Number.class, 0);
    private final VarOutput<ExecSignal> execOut = new VarOutput<>(this, "Exec", ExecSignal.class, new ExecSignal(new String[0]));

    public DelayNode() {
        this.exec.setAllowMultipleConnections(true);
        reg(this.exec);
        reg(this.delayMs);
        reg(this.execOut);
    }
}
