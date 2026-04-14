package com.adamk33n3r.runelite.watchdog.ui.nodegraph;

import com.adamk33n3r.nodegraph.Node;
import com.adamk33n3r.nodegraph.nodes.logic.BooleanGate;
import com.adamk33n3r.nodegraph.nodes.logic.Equality;
import com.adamk33n3r.nodegraph.nodes.logic.LocationCompare;
import com.adamk33n3r.runelite.watchdog.Displayable;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum LogicNodeType implements Displayable {
    BOOLEAN("Boolean Gate", "Boolean logic e.g. and/or", BooleanGate.class, BooleanGateNodePanel.class),
    EQUALITY("Equality", "Equality logic e.g. ==/!=/>/<", Equality.class, EqualityNodePanel.class),
    LOCATION_COMPARE("Location Compare", "Location comparison", LocationCompare.class, LocationCompareNodePanel.class),
    ;

    private final String name;
    private final String tooltip;
    private final Class<? extends Node> implClass;
    private final Class<? extends NodePanel> nodePanelClass;
}
