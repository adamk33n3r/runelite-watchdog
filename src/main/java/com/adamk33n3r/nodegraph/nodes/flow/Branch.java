package com.adamk33n3r.nodegraph.nodes.flow;

import com.adamk33n3r.nodegraph.ExecSignal;
import com.adamk33n3r.nodegraph.Node;
import com.adamk33n3r.nodegraph.VarInput;
import com.adamk33n3r.nodegraph.VarOutput;
import lombok.Getter;

@Getter
public class Branch extends Node {
    private final VarInput<ExecSignal> exec = new VarInput<>(this, "Exec", ExecSignal.class, new ExecSignal(new String[0]));
    private final VarInput<Boolean> condition = new VarInput<>(this, "Condition", Boolean.class, false);
    private final VarOutput<ExecSignal> execTrue = new VarOutput<>(this, "True", ExecSignal.class, new ExecSignal(new String[0]));
    private final VarOutput<ExecSignal> execFalse = new VarOutput<>(this, "False", ExecSignal.class, new ExecSignal(new String[0]));

    public Branch() {
        this.exec.setAllowMultipleConnections(true);

        this.exec.onChange(exec -> {
            boolean cond = this.getCondition().getValue();
            VarOutput<ExecSignal> activeOut = cond ? this.execTrue : this.execFalse;
            activeOut.setValue(exec);
        });

        reg(this.exec);
        reg(this.condition);
        reg(this.execTrue);
        reg(this.execFalse);
    }

    @Override
    public void process() {
    }
}
