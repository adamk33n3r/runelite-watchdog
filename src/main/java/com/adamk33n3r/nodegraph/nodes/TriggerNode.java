package com.adamk33n3r.nodegraph.nodes;

import com.adamk33n3r.nodegraph.Node;
import com.adamk33n3r.nodegraph.VarInput;
import com.adamk33n3r.nodegraph.VarOutput;
import com.adamk33n3r.runelite.watchdog.alerts.Alert;
import lombok.Getter;

@Getter
public class TriggerNode extends Node {
    private final Alert alert;

    private final VarInput<Boolean> enabled = new VarInput<>(this, "Enabled", Boolean.class, false);
    private final VarInput<String> name = new VarInput<>(this, "Name", String.class, "");
    private final VarInput<Number> debounce = new VarInput<>(this, "Debounce", Number.class, 0);
    private final VarInput<String[]> captureGroupsIn = new VarInput<>(this, "Capture Groups In", String[].class, new String[0]);

//    private final VarOutput<Alert> outAlert = new VarOutput<>("Alert", this.alert);
    // Perhaps only need to send this one
    private final VarOutput<String[]> captureGroups = new VarOutput<>(this, "Capture Groups Out", String[].class, new String[0]);
    private final VarOutput<String> nameOut = new VarOutput<>(this, "Name", String.class, this.name.getValue());
    private final VarOutput<Number> debounceOut = new VarOutput<>(this, "Debounce Out", Number.class, this.debounce.getValue());

    public TriggerNode(Alert alert) {
        this.alert = alert;

        this.enabled.setValue(this.alert.isEnabled());
        this.name.setValue(this.alert.getName());
        this.debounce.setValue(this.alert.getDebounceTime());

        this.nameOut.setValue(this.name.getValue());
        this.debounceOut.setValue(this.debounce.getValue());
    }

    @Override
    public void process() {
        this.nameOut.setValue(this.name.getValue());
        this.captureGroups.setValue(this.captureGroupsIn.getValue());
        this.debounceOut.setValue(this.debounce.getValue());
    }
}
