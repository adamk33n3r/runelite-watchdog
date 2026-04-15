package com.adamk33n3r.runelite.watchdog.ui.nodegraph;

import com.adamk33n3r.nodegraph.Node;
import com.adamk33n3r.nodegraph.nodes.constants.*;
import com.adamk33n3r.runelite.watchdog.Displayable;
import com.adamk33n3r.runelite.watchdog.ui.nodegraph.variables.*;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum VariableNodeType implements Displayable {
    BOOLEAN("Boolean", "Constant boolean", Bool.class, BoolNodePanel.class),
    NUMBER("Number", "Constant number", Num.class, NumberNodePanel.class),
    LOCATION("Current Location", "Outputs your current location", Location.class, LocationNodePanel.class),
    PLUGIN("Plugin State", "Outputs true/false if a plugin is on/off", PluginVar.class, PluginNodePanel.class),
    INVENTORY("Inventory", "Inventory", Inventory.class, InventoryVariableNodePanel.class),
    ;

    private final String name;
    private final String tooltip;
    private final Class<? extends Node> implClass;
    private final Class<? extends NodePanel> nodePanelClass;
}
