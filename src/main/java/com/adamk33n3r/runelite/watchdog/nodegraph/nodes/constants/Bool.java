package com.adamk33n3r.runelite.watchdog.nodegraph.nodes.constants;

import com.adamk33n3r.runelite.watchdog.nodegraph.Node;
import com.adamk33n3r.runelite.watchdog.nodegraph.VarOutput;
import lombok.Getter;

@Getter
public class Bool extends Node {
    private final VarOutput<Boolean> value = new VarOutput<>(this, "Value", Boolean.class, true);

    public void setValue(boolean value) {
        this.value.setValue(value);
    }
}
