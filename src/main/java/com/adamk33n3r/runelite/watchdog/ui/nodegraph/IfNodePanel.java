package com.adamk33n3r.runelite.watchdog.ui.nodegraph;

import com.adamk33n3r.runelite.watchdog.nodegraph.Node;

import java.awt.*;

public class IfNodePanel extends AcceptsConnectionNodePanel {
    public IfNodePanel(GraphPanel graphPanel, Node node, int x, int y, String name, Color color) {
        super(graphPanel, node, x, y, name, color);

        ConnectionPoint inputConnectionPoint = new ConnectionPointIn<>(this, name, String.class, "TEST IF INPUT");
        this.add(inputConnectionPoint, BorderLayout.WEST);
        ConnectionPoint outputConnectionPoint = new ConnectionPointOut<>(this, name, String.class, "IF OUTPUT");
        this.add(outputConnectionPoint, BorderLayout.EAST);
    }
}
