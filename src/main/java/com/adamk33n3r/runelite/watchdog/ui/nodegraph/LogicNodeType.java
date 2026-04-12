package com.adamk33n3r.runelite.watchdog.ui.nodegraph;

import com.adamk33n3r.runelite.watchdog.Displayable;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum LogicNodeType implements Displayable {
    // dropdown to select which gate, will have 2 inputs
    BOOLEAN("Boolean", "Boolean logic e.g. and/or", BooleanGateNodePanel.class),
    // dropdown to select which equality type, will have 2 inputs and must be same data type
    EQUALITY("Equality", "Equality logic e.g. ==/!=/>/<", EqualityNodePanel.class),
    ;

    private final String name;
    private final String tooltip;
    private final Class<? extends NodePanel> implClass;
}
