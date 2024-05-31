package com.adamk33n3r.runelite.watchdog.ui.nodegraph;

import com.adamk33n3r.runelite.watchdog.nodegraph.Node;

import java.awt.*;

public abstract class AcceptsConnectionNodePanel extends NodePanel implements IAcceptDropNode {
    public AcceptsConnectionNodePanel(GraphPanel graphPanel, Node node, int x, int y, String name, Color color) {
        super(graphPanel, node, x, y, name, color);
    }
}
