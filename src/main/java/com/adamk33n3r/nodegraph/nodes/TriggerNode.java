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
//    private final VarInput<String> name = new VarInput<>(this, "Name", String.class, "");
//    private final VarInput<Number> debounce = new VarInput<>(this, "Debounce", Number.class, 0);
    private final VarInput<String[]> captureGroupsIn = new VarInput<>(this, "Capture Groups In", String[].class, new String[0]);

    private final VarOutput<ExecSignal> exec = new VarOutput<>(this, "Exec", ExecSignal.class, new ExecSignal(new String[0]));
//    private final VarOutput<String> nameOut = new VarOutput<>(this, "Name", String.class, "");
//    private final VarOutput<Number> debounceOut = new VarOutput<>(this, "Debounce Out", Number.class, 0);
    private final VarOutput<Boolean> enabledOut = new VarOutput<>(this, "Enabled Out", Boolean.class, this.enabled.getValue());

    public TriggerNode(Alert alert) {
        this.alert = alert;

        this.enabled.setValue(this.alert.isEnabled());
//        this.name.setValue(this.alert.getName());
//        this.debounce.setValue(this.alert.getDebounceTime());

//        this.nameOut.setValue(this.name.getValue());
//        this.debounceOut.setValue(this.debounce.getValue());
        this.enabledOut.setValue(this.enabled.getValue());
        this.exec.setValue(new ExecSignal(this.captureGroupsIn.getValue()));

        reg(this.enabled);
//        reg(this.name);
//        reg(this.debounce);
        reg(this.captureGroupsIn);
        reg(this.exec);
//        reg(this.nameOut);
//        reg(this.debounceOut);
        reg(this.enabledOut);
    }

    @Override
    public void process() {
//        this.nameOut.setValue(this.name.getValue());
        this.exec.setValue(new ExecSignal(this.captureGroupsIn.getValue()));
//        this.debounceOut.setValue(this.debounce.getConnection() != null ? this.debounce.getValue() : this.alert.getDebounceTime());
        this.enabledOut.setValue(this.enabled.getValue());
    }
}
