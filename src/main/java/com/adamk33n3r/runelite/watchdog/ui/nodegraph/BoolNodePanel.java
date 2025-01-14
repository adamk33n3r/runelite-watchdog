package com.adamk33n3r.runelite.watchdog.ui.nodegraph;

import com.adamk33n3r.nodegraph.nodes.constants.Bool;
import com.adamk33n3r.runelite.watchdog.ui.nodegraph.connections.ConnectionLine;
import com.adamk33n3r.runelite.watchdog.ui.nodegraph.connections.ConnectionPointIn;
import com.adamk33n3r.runelite.watchdog.ui.nodegraph.connections.ConnectionPointOut;
import com.adamk33n3r.runelite.watchdog.ui.nodegraph.inputs.BoolInput;
import lombok.Getter;

import java.awt.*;

@Getter
public class BoolNodePanel extends NodePanel {
    private final ConnectionPointIn<Boolean> boolValueIn;
    private final ConnectionPointOut<Boolean> boolValueOut;

    public BoolNodePanel(GraphPanel graphPanel, Bool boolNode, int x, int y, String name, Color color) {
        super(graphPanel, boolNode, x, y, name, color);

        this.boolValueIn = new ConnectionPointIn<>(this, boolNode.getValueIn());
        this.boolValueOut = new ConnectionPointOut<>(this, boolNode.getValueOut());

        BoolInput boolInput = new BoolInput("Boolean Value", boolNode.getValueOut().getValue());
        this.items.add(new ConnectionLine<>(this.boolValueIn, boolInput, this.boolValueOut));

        this.pack();
    }
}
