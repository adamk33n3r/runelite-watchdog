package com.adamk33n3r.runelite.watchdog.ui.nodegraph;

import java.awt.*;

public class IfNode extends AcceptsConnectionNode {
    public IfNode(Graph graph, int x, int y, String name, Color color) {
        super(graph, x, y, name, color);

        ConnectionPoint inputConnectionPoint = new ConnectionPoint(this);
        this.add(inputConnectionPoint, BorderLayout.WEST);
        ConnectionPoint outputConnectionPoint = new ConnectionPoint(this);
        this.add(outputConnectionPoint, BorderLayout.EAST);
    }
}
