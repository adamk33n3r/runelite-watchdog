package com.adamk33n3r.runelite.watchdog.ui.nodegraph;

import com.adamk33n3r.nodegraph.nodes.constants.Bool;
import com.adamk33n3r.runelite.watchdog.ui.nodegraph.connections.ConnectionLine;
import com.adamk33n3r.runelite.watchdog.ui.nodegraph.connections.ConnectionPointOut;
import com.adamk33n3r.runelite.watchdog.ui.nodegraph.inputs.BoolInput;
import lombok.Getter;

import java.awt.*;

@Getter
public class BoolNodePanel extends NodePanel {
    private final ConnectionPointOut<Boolean> boolValue;

    public BoolNodePanel(GraphPanel graphPanel, Bool boolNode, int x, int y, String name, Color color) {
        super(graphPanel, boolNode, x, y, name, color);

        this.boolValue = new ConnectionPointOut<>(this, boolNode.getValue());

        BoolInput boolInput = new BoolInput("Boolean Value", boolNode.getValue().getValue());
        this.items.add(new ConnectionLine<>(null, boolInput, this.boolValue));

        this.pack();
    }
}
