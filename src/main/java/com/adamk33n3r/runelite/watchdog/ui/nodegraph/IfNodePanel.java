package com.adamk33n3r.runelite.watchdog.ui.nodegraph;

import com.adamk33n3r.runelite.watchdog.nodegraph.Node;

import java.awt.*;

public class IfNodePanel extends AcceptsConnectionNodePanel {
    public IfNodePanel(GraphPanel graphPanel, Node node, int x, int y, String name, Color color) {
        super(graphPanel, node, x, y, name, color);

//        ConnectionPoint inputConnectionPoint = new ConnectionPointIn<>(this);
//        this.inConnectionPoints.add(inputConnectionPoint);
//        ConnectionPoint outputConnectionPoint = new ConnectionPointOut<>(this, name, String.class, "IF OUTPUT");
//        this.outConnectionPoints.add(outputConnectionPoint);
    }
}
