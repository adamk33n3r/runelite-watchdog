package com.adamk33n3r.runelite.watchdog.ui.nodegraph.variables;

import com.adamk33n3r.nodegraph.nodes.constants.ConstantNode;
import com.adamk33n3r.runelite.watchdog.ui.nodegraph.GraphPanel;
import com.adamk33n3r.runelite.watchdog.ui.nodegraph.NodePanel;
import com.adamk33n3r.runelite.watchdog.ui.nodegraph.inputs.TextInput;

import java.awt.*;

public class ConstantNodePanel extends NodePanel {
    public ConstantNodePanel(GraphPanel graphPanel, ConstantNode node, int x, int y, String name, Color color) {
        super(graphPanel, node, x, y, name, color);

        this.updateHeaderLabel(node.getNameOut().getValue());

        TextInput nameInput = new TextInput("Name", "Name", node.getNameOut());
        nameInput.registerOnType(this::updateHeaderLabel);
        this.items.add(nameInput);
    }
}
