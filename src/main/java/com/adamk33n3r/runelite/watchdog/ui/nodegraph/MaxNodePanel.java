package com.adamk33n3r.runelite.watchdog.ui.nodegraph;

import com.adamk33n3r.nodegraph.nodes.math.Max;
import com.adamk33n3r.runelite.watchdog.ui.nodegraph.connections.ConnectionLine;
import com.adamk33n3r.runelite.watchdog.ui.nodegraph.connections.ConnectionPointIn;
import com.adamk33n3r.runelite.watchdog.ui.nodegraph.connections.ConnectionPointOut;
import com.adamk33n3r.runelite.watchdog.ui.nodegraph.inputs.NumberInput;
import com.adamk33n3r.runelite.watchdog.ui.nodegraph.inputs.ViewInput;
import lombok.Getter;

import java.awt.Color;

@Getter
public class MaxNodePanel extends NodePanel {
    private final ConnectionPointOut<Number> resultOut;

    public MaxNodePanel(GraphPanel graphPanel, Max node, int x, int y, String name, Color color) {
        super(graphPanel, node, x, y, name, color);

        ConnectionPointIn<Number> inA = new ConnectionPointIn<>(this, node.getA());
        this.items.add(new ConnectionLine<>(inA, new NumberInput("A", node.getA()), null));

        ConnectionPointIn<Number> inB = new ConnectionPointIn<>(this, node.getB());
        this.items.add(new ConnectionLine<>(inB, new NumberInput("B", node.getB()), null));

        this.resultOut = new ConnectionPointOut<>(this, node.getResult());
        ViewInput<Number> resultView = new ViewInput<>("Result", node.getResult().getValue());
        addDisposer(node.getA().onChange(a -> resultView.setValue(node.getResult().getValue())));
        addDisposer(node.getB().onChange(b -> resultView.setValue(node.getResult().getValue())));
        this.items.add(new ConnectionLine<>(null, resultView, this.resultOut));

        this.watchDirty(node.getA(), node.getB());
        this.pack();
    }
}
