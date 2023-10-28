package com.adamk33n3r.runelite.watchdog.ui.nodegraph;

import java.awt.BorderLayout;
import java.awt.Color;

public class NotificationNode extends AcceptsConnectionNode {
    public NotificationNode(Graph graph, int x, int y, String name, Color color) {
        super(graph, x, y, name, color);

        ConnectionPoint inputConnectionPoint = new ConnectionPoint(this);
        this.add(inputConnectionPoint, BorderLayout.WEST);
    }
}
