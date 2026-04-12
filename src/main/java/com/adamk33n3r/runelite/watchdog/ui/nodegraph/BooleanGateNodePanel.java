package com.adamk33n3r.runelite.watchdog.ui.nodegraph;

import com.adamk33n3r.nodegraph.nodes.logic.BooleanGate;
import com.adamk33n3r.runelite.watchdog.ui.nodegraph.connections.ConnectionLine;
import com.adamk33n3r.runelite.watchdog.ui.nodegraph.connections.ConnectionPointIn;
import com.adamk33n3r.runelite.watchdog.ui.nodegraph.connections.ConnectionPointOut;
import com.adamk33n3r.runelite.watchdog.ui.nodegraph.inputs.BoolInput;
import com.adamk33n3r.runelite.watchdog.ui.nodegraph.inputs.ViewInput;
import lombok.Getter;

import javax.swing.*;
import java.awt.*;

@Getter
public class BooleanGateNodePanel extends NodePanel {
    private final ConnectionPointOut<Boolean> resultOut;

    public BooleanGateNodePanel(GraphPanel graphPanel, BooleanGate node, int x, int y, String name, Color color) {
        super(graphPanel, node, x, y, name, color);

        JComboBox<BooleanGate.Op> opBox = new JComboBox<>(BooleanGate.Op.values());
        opBox.setSelectedItem(node.getOp().getValue());
        opBox.addActionListener(e -> {
            node.getOp().setValue((BooleanGate.Op) opBox.getSelectedItem());
            this.notifyChange();
        });
        this.items.add(opBox);

        ConnectionPointIn<Boolean> inA = new ConnectionPointIn<>(this, node.getA());
        this.items.add(new ConnectionLine<>(inA, new BoolInput("A", node.getA()), null));

        ConnectionPointIn<Boolean> inB = new ConnectionPointIn<>(this, node.getB());
        this.items.add(new ConnectionLine<>(inB, new BoolInput("B", node.getB()), null));

        this.resultOut = new ConnectionPointOut<>(this, node.getResult());
        ViewInput<Boolean> result = new ViewInput<>("Result", node.getResult().getValue());
        addDisposer(node.getA().onChange(a -> result.setValue(node.getResult().getValue())));
        addDisposer(node.getB().onChange(b -> result.setValue(node.getResult().getValue())));
        addDisposer(node.getOp().onChange(op -> result.setValue(node.getResult().getValue())));
        this.items.add(new ConnectionLine<>(null, result, this.resultOut));

        this.pack();
    }
}
