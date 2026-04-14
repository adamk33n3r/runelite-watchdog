package com.adamk33n3r.runelite.watchdog.ui.nodegraph;

import com.adamk33n3r.nodegraph.Node;
import com.adamk33n3r.nodegraph.nodes.constants.Bool;
import com.adamk33n3r.nodegraph.nodes.constants.InventoryVar;
import com.adamk33n3r.nodegraph.nodes.constants.Location;
import com.adamk33n3r.nodegraph.nodes.constants.Num;
import com.adamk33n3r.nodegraph.nodes.constants.PluginVar;
import com.adamk33n3r.runelite.watchdog.Displayable;
import com.adamk33n3r.runelite.watchdog.ui.nodegraph.variables.BoolNodePanel;
import com.adamk33n3r.runelite.watchdog.ui.nodegraph.variables.InventoryNodePanel;
import com.adamk33n3r.runelite.watchdog.ui.nodegraph.variables.LocationNodePanel;
import com.adamk33n3r.runelite.watchdog.ui.nodegraph.variables.NumberNodePanel;
import com.adamk33n3r.runelite.watchdog.ui.nodegraph.variables.PluginNodePanel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum VariableNodeType implements Displayable {
    BOOLEAN("Boolean", "Constant boolean", Bool.class, BoolNodePanel.class),
    NUMBER("Number", "Constant number", Num.class, NumberNodePanel.class),
    LOCATION("Current Location", "Outputs your current location", Location.class, LocationNodePanel.class),
    PLUGIN("Plugin State", "Outputs true/false if a plugin is on/off", PluginVar.class, PluginNodePanel.class),
    INVENTORY("Inventory", "Inventory", InventoryVar.class, InventoryNodePanel.class),
    ;

    private final String name;
    private final String tooltip;
    private final Class<? extends Node> implClass;
    private final Class<? extends NodePanel> nodePanelClass;
}
