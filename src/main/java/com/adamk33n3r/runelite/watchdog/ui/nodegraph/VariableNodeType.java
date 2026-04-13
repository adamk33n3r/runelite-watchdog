package com.adamk33n3r.runelite.watchdog.ui.nodegraph;

import com.adamk33n3r.nodegraph.Node;
import com.adamk33n3r.nodegraph.nodes.constants.Bool;
import com.adamk33n3r.nodegraph.nodes.constants.Num;
import com.adamk33n3r.runelite.watchdog.Displayable;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum VariableNodeType implements Displayable {
    BOOLEAN("Boolean", "Boolean", Bool.class, BoolNodePanel.class),
    NUMBER("Number", "Number", Num.class, NumberNodePanel.class),
//    // can use same controls as LocationAlertPanel, but will constantly output true/false rather than triggering an exec
//    LOCATION("Location", "Location condition", IfNodePanel.class),
//    // outputs true/false if plugin by name is on/off
//    PLUGIN("Plugin", "Plugin", IfNodePanel.class),
//    // outputs true/false based on inventory contents
//    INVENTORY("Inventory", "Inventory", InventoryNodePanel.class),
    ;

    private final String name;
    private final String tooltip;
    private final Class<? extends Node> implClass;
    private final Class<? extends NodePanel> nodePanelClass;
}
