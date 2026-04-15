package com.adamk33n3r.runelite.watchdog.ui.nodegraph.variables;

import com.adamk33n3r.nodegraph.nodes.VariableNode;
import com.adamk33n3r.runelite.watchdog.ui.nodegraph.GraphPanel;
import com.adamk33n3r.runelite.watchdog.ui.nodegraph.NodePanel;
import com.adamk33n3r.runelite.watchdog.ui.nodegraph.inputs.TextInput;

import java.awt.*;

public abstract class VariableNodePanel extends NodePanel {
    public VariableNodePanel(GraphPanel graphPanel, VariableNode node, int x, int y, String name, Color color) {
        this(graphPanel, node, x, y, name, color, true);
    }

    public VariableNodePanel(GraphPanel graphPanel, VariableNode node, int x, int y, String name, Color color, boolean showName) {
        super(graphPanel, node, x, y, name, color);

        if (showName) {
            this.updateHeaderLabel(node.getNameOut().getValue());

            TextInput nameInput = new TextInput("Name", "Name", node.getNameOut());
            nameInput.registerOnType(this::updateHeaderLabel);
            this.items.add(nameInput);
        }
    }
}
