package com.adamk33n3r.nodegraph.nodes;

import com.adamk33n3r.nodegraph.VarOutput;
import com.adamk33n3r.runelite.watchdog.alerts.ContinuousAlert;
import lombok.Getter;

@Getter
public class ContinuousTriggerNode extends TriggerNode {
    private final VarOutput<Boolean> isTriggered = new VarOutput<>(this, "Is Triggered", Boolean.class, false);

    public ContinuousTriggerNode(ContinuousAlert alert) {
        super(alert);
    }
}
