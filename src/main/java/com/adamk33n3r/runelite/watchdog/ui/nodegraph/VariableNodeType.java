package com.adamk33n3r.runelite.watchdog.ui.nodegraph;

import com.adamk33n3r.nodegraph.Node;
import com.adamk33n3r.nodegraph.nodes.constants.Bool;
import com.adamk33n3r.nodegraph.nodes.constants.Num;
import com.adamk33n3r.runelite.watchdog.Displayable;
import com.adamk33n3r.runelite.watchdog.ui.nodegraph.variables.BoolNodePanel;
import com.adamk33n3r.runelite.watchdog.ui.nodegraph.variables.NumberNodePanel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum VariableNodeType implements Displayable {
    BOOLEAN("Boolean", "Boolean", Bool.class, BoolNodePanel.class),
    NUMBER("Number", "Number", Num.class, NumberNodePanel.class),
    // will constantly output the player's WorldPoint
//    LOCATION("Location", "Location", LocationNodePanel.class),
//    // outputs true/false if plugin, specified by name in a dropdown (look at PluginToggleNotificationPanel), is on/off
//    PLUGIN("Plugin", "Plugin", PluginNodePanel.class),
//    // outputs true/false based on inventory contents, use same controls as InventoryAlertPanel
//    INVENTORY("Inventory", "Inventory", InventoryNodePanel.class),
    ;

    private final String name;
    private final String tooltip;
    private final Class<? extends Node> implClass;
    private final Class<? extends NodePanel> nodePanelClass;
}
