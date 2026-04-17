package com.adamk33n3r.nodegraph.nodes;

import com.adamk33n3r.nodegraph.Node;
import com.adamk33n3r.nodegraph.ExecSignal;
import com.adamk33n3r.nodegraph.VarInput;
import com.adamk33n3r.nodegraph.VarOutput;
import com.adamk33n3r.runelite.watchdog.alerts.Alert;
import lombok.Getter;

@Getter
public class TriggerNode extends Node {
    private final Alert alert;

    private final VarInput<Boolean> enabled = new VarInput<>(this, "Enabled", Boolean.class, false);
    private final VarInput<String[]> captureGroupsIn = new VarInput<>(this, "Capture Groups In", String[].class, new String[0]);

    private final VarOutput<ExecSignal> exec = new VarOutput<>(this, "Exec", ExecSignal.class, new ExecSignal(new String[0]));
    private final VarOutput<Boolean> enabledOut = new VarOutput<>(this, "Enabled Out", Boolean.class, this.enabled.getValue());

    public TriggerNode(Alert alert) {
        this.alert = alert;

        this.enabled.setValue(this.alert.isEnabled());
        this.enabledOut.setValue(this.enabled.getValue());
        this.exec.setValue(new ExecSignal(this.captureGroupsIn.getValue()));

        reg(this.enabled);
        reg(this.captureGroupsIn);
        reg(this.exec);
        reg(this.enabledOut);
    }

    @Override
    public void process() {
        this.exec.setValue(new ExecSignal(this.captureGroupsIn.getValue()));
        this.enabledOut.setValue(this.enabled.getValue());
    }
}
