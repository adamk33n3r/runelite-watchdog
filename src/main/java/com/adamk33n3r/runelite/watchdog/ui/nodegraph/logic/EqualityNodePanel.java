package com.adamk33n3r.runelite.watchdog.ui.nodegraph.logic;

import com.adamk33n3r.nodegraph.nodes.logic.Equality;
import com.adamk33n3r.runelite.watchdog.ui.nodegraph.GraphPanel;
import com.adamk33n3r.runelite.watchdog.ui.nodegraph.NodePanel;
import com.adamk33n3r.runelite.watchdog.ui.nodegraph.connections.ConnectionLine;
import com.adamk33n3r.runelite.watchdog.ui.nodegraph.connections.ConnectionPointIn;
import com.adamk33n3r.runelite.watchdog.ui.nodegraph.connections.ConnectionPointOut;
import com.adamk33n3r.runelite.watchdog.ui.nodegraph.inputs.EnumInput;
import com.adamk33n3r.runelite.watchdog.ui.nodegraph.inputs.NumberInput;
import com.adamk33n3r.runelite.watchdog.ui.nodegraph.inputs.ViewInput;
import lombok.Getter;

import java.awt.*;

@Getter
public class EqualityNodePanel extends NodePanel {
    private final ConnectionPointOut<Boolean> resultOut;

    public EqualityNodePanel(GraphPanel graphPanel, Equality node, int x, int y, String name, Color color) {
        super(graphPanel, node, x, y, name, color);

        ConnectionPointIn<Equality.Op> opIn = new ConnectionPointIn<>(this, node.getOp());
        this.items.add(new ConnectionLine<>(opIn, new EnumInput<>("Op", node.getOp()), null));

        ConnectionPointIn<Number> inA = new ConnectionPointIn<>(this, node.getA());
        this.items.add(new ConnectionLine<>(inA, new NumberInput("A", node.getA()), null));

        ConnectionPointIn<Number> inB = new ConnectionPointIn<>(this, node.getB());
        this.items.add(new ConnectionLine<>(inB, new NumberInput("B", node.getB()), null));

        this.resultOut = new ConnectionPointOut<>(this, node.getResult());
        ViewInput<Boolean> result = new ViewInput<>("Result", node.getResult().getValue());
        addDisposer(node.getA().onChange(a -> result.setValue(node.getResult().getValue())));
        addDisposer(node.getB().onChange(b -> result.setValue(node.getResult().getValue())));
        addDisposer(node.getOp().onChange(op -> result.setValue(node.getResult().getValue())));
        this.items.add(new ConnectionLine<>(null, result, this.resultOut));

        this.watchDirty(node.getOp(), node.getA(), node.getB());
        this.pack();
    }
}
