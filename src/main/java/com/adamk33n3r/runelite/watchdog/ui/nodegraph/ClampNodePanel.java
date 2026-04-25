package com.adamk33n3r.runelite.watchdog.ui.nodegraph;

import com.adamk33n3r.nodegraph.nodes.math.Clamp;
import com.adamk33n3r.runelite.watchdog.ui.nodegraph.connections.ConnectionLine;
import com.adamk33n3r.runelite.watchdog.ui.nodegraph.connections.ConnectionPointIn;
import com.adamk33n3r.runelite.watchdog.ui.nodegraph.connections.ConnectionPointOut;
import com.adamk33n3r.runelite.watchdog.ui.nodegraph.inputs.NumberInput;
import com.adamk33n3r.runelite.watchdog.ui.nodegraph.inputs.ViewInput;
import lombok.Getter;

import java.awt.Color;

@Getter
public class ClampNodePanel extends NodePanel {
    private final ConnectionPointOut<Number> resultOut;

    public ClampNodePanel(GraphPanel graphPanel, Clamp node, int x, int y, String name, Color color) {
        super(graphPanel, node, x, y, name, color);

        ConnectionPointIn<Number> inValue = new ConnectionPointIn<>(this, node.getValue());
        this.items.add(new ConnectionLine<>(inValue, new NumberInput("Value", node.getValue()), null));

        ConnectionPointIn<Number> inMin = new ConnectionPointIn<>(this, node.getMin());
        this.items.add(new ConnectionLine<>(inMin, new NumberInput("Min", node.getMin()), null));

        ConnectionPointIn<Number> inMax = new ConnectionPointIn<>(this, node.getMax());
        this.items.add(new ConnectionLine<>(inMax, new NumberInput("Max", node.getMax()), null));

        this.resultOut = new ConnectionPointOut<>(this, node.getResult());
        ViewInput<Number> resultView = new ViewInput<>("Result", node.getResult().getValue());
        addDisposer(node.getValue().onChange(v -> resultView.setValue(node.getResult().getValue())));
        addDisposer(node.getMin().onChange(v -> resultView.setValue(node.getResult().getValue())));
        addDisposer(node.getMax().onChange(v -> resultView.setValue(node.getResult().getValue())));
        this.items.add(new ConnectionLine<>(null, resultView, this.resultOut));

        this.watchDirty(node.getValue(), node.getMin(), node.getMax());
        this.pack();
    }
}
