package com.adamk33n3r.runelite.watchdog.ui.nodegraph;

import com.adamk33n3r.nodegraph.nodes.math.Round;
import com.adamk33n3r.runelite.watchdog.ui.nodegraph.connections.ConnectionLine;
import com.adamk33n3r.runelite.watchdog.ui.nodegraph.connections.ConnectionPointIn;
import com.adamk33n3r.runelite.watchdog.ui.nodegraph.connections.ConnectionPointOut;
import com.adamk33n3r.runelite.watchdog.ui.nodegraph.inputs.NumberInput;
import com.adamk33n3r.runelite.watchdog.ui.nodegraph.inputs.ViewInput;
import lombok.Getter;

import java.awt.Color;

@Getter
public class RoundNodePanel extends NodePanel {
    private final ConnectionPointOut<Number> resultOut;

    public RoundNodePanel(GraphPanel graphPanel, Round node, int x, int y, String name, Color color) {
        super(graphPanel, node, x, y, name, color);

        ConnectionPointIn<Number> valueIn = new ConnectionPointIn<>(this, node.getValue());
        this.items.add(new ConnectionLine<>(valueIn, new NumberInput("Value", node.getValue()), null));

        this.resultOut = new ConnectionPointOut<>(this, node.getResult());
        ViewInput<Number> resultView = new ViewInput<>("Result", node.getResult().getValue());
        addDisposer(node.getValue().onChange(v -> resultView.setValue(node.getResult().getValue())));
        this.items.add(new ConnectionLine<>(null, resultView, this.resultOut));

        this.watchDirty(node.getValue());
        this.pack();
    }
}
