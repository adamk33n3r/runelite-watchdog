package com.adamk33n3r.runelite.watchdog.ui.nodegraph;

import com.adamk33n3r.runelite.watchdog.Displayable;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum LogicNodeType implements Displayable {
//    IF("If", "If condition", IfNode.class),
    AND("And", "And", IfNode.class),
    OR("Or", "Or", IfNode.class),

    // Equality
    EQUALS("Equals", "Equals", IfNode.class),
    NOT_EQUALS("Not Equals", "Not Equals", IfNode.class),
    GREATER_THAN("Greater Than", "Greater Than", IfNode.class),
    LESS_THAN("Less Than", "Less Than", IfNode.class),
    ;

    private final String name;
    private final String tooltip;
    private final Class<? extends Node> implClass;
}
