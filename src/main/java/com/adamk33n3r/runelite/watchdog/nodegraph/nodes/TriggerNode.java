package com.adamk33n3r.runelite.watchdog.nodegraph.nodes;

import com.adamk33n3r.runelite.watchdog.alerts.Alert;
import com.adamk33n3r.runelite.watchdog.nodegraph.Node;
import com.adamk33n3r.runelite.watchdog.nodegraph.VarInput;
import com.adamk33n3r.runelite.watchdog.nodegraph.VarOutput;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@AllArgsConstructor
public class TriggerNode extends Node {
    @Setter
    private Alert alert;

    private final VarInput<Boolean> enabled = new VarInput<>(this, "Enabled", Boolean.class, true);
    private final VarInput<String> name = new VarInput<>(this, "Name", String.class, "Alert");
    private final VarInput<Number> debounce = new VarInput<>(this, "Debounce", Number.class, 0);

//    private final VarOutput<Alert> outAlert = new VarOutput<>("Alert", this.alert);
    // Perhaps only need to send this one
    private final VarOutput<String[]> captureGroups = new VarOutput<>(this, "Capture Groups Out", String[].class, new String[0]);
    private final VarOutput<String> nameOut = new VarOutput<>(this, "Name", String.class, this.name.getValue());
}
