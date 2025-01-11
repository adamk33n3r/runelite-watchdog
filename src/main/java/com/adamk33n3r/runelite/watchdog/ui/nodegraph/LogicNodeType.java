package com.adamk33n3r.runelite.watchdog.ui.nodegraph;

import com.adamk33n3r.runelite.watchdog.Displayable;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum LogicNodeType implements Displayable {
//    IF("If", "If condition", IfNode.class),
    AND("And", "And", IfNodePanel.class),
    OR("Or", "Or", IfNodePanel.class),

    // Equality
    EQUALS("Equals", "Equals", IfNodePanel.class),
    NOT_EQUALS("Not Equals", "Not Equals", IfNodePanel.class),
    GREATER_THAN("Greater Than", "Greater Than", IfNodePanel.class),
    LESS_THAN("Less Than", "Less Than", IfNodePanel.class),
    ;

    private final String name;
    private final String tooltip;
    private final Class<? extends NodePanel> implClass;
}
