package com.adamk33n3r.runelite.watchdog.ui.nodegraph.variables;

import com.adamk33n3r.nodegraph.nodes.constants.VariableNode;
import com.adamk33n3r.runelite.watchdog.ui.nodegraph.GraphPanel;
import com.adamk33n3r.runelite.watchdog.ui.nodegraph.NodePanel;

import java.awt.*;

public abstract class VariableNodePanel extends NodePanel {
    public VariableNodePanel(GraphPanel graphPanel, VariableNode node, int x, int y, String name, Color color) {
        super(graphPanel, node, x, y, name, color);
    }
}
