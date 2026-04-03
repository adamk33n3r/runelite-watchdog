package com.adamk33n3r.runelite.watchdog.ui.nodegraph;

import com.adamk33n3r.runelite.watchdog.Displayable;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum VariableNodeType implements Displayable {
    BOOLEAN("Boolean", "Boolean", BoolNodePanel.class),
    NUMBER("Number", "Number", NumberNodePanel.class),
    ;

    private final String name;
    private final String tooltip;
    private final Class<? extends NodePanel> implClass;
}
