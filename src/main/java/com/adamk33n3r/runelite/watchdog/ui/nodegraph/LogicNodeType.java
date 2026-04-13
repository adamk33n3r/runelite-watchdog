package com.adamk33n3r.runelite.watchdog.ui.nodegraph;

import com.adamk33n3r.nodegraph.Node;
import com.adamk33n3r.nodegraph.nodes.logic.BooleanGate;
import com.adamk33n3r.nodegraph.nodes.logic.Equality;
import com.adamk33n3r.runelite.watchdog.Displayable;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum LogicNodeType implements Displayable {
    // dropdown to select which gate, will have 2 inputs
    BOOLEAN("Boolean Gate", "Boolean logic e.g. and/or", BooleanGate.class, BooleanGateNodePanel.class),
    // dropdown to select which equality type, will have 2 inputs and must be same data type
    EQUALITY("Equality", "Equality logic e.g. ==/!=/>/<", Equality.class, EqualityNodePanel.class),
    // takes in 2 WorldPoint inputs to compare, or uses same controls as LocationAlertPanel for x,y,plane if that input is not connected
    // additionally, will have other controls of LocationAlertPanel like Cardinal Only, Distance, and a button to grab the current
    // location
//    LOCATION_COMPARE("Location", "Location", LocationCompare.class, LocationCompareNodePanel.class),
    ;

    private final String name;
    private final String tooltip;
    private final Class<? extends Node> implClass;
    private final Class<? extends NodePanel> nodePanelClass;
}
