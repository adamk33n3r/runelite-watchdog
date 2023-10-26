package com.adamk33n3r.runelite.watchdog.ui.nodegraph;

import java.awt.*;

public class IfNode extends AcceptsConnectionNode {
    public IfNode(Graph graph, int x, int y, String name, Color color) {
        super(graph, x, y, name, color);

        ConnectionPoint connectionPoint = new ConnectionPoint(this);
        this.add(connectionPoint, BorderLayout.EAST);
    }
}
