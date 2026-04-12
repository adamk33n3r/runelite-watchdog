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
        opBox.setSelectedItem(node.getOp());
        opBox.addActionListener(e -> {
            node.setOp((BooleanGate.Op) opBox.getSelectedItem());
            this.notifyChange();
        });
        this.items.add(opBox);

        ConnectionPointIn<Boolean> inA = new ConnectionPointIn<>(this, node.getA());
        this.items.add(new ConnectionLine<>(inA, new BoolInput("A", node.getA()), null));

        ConnectionPointIn<Boolean> inB = new ConnectionPointIn<>(this, node.getB());
        this.items.add(new ConnectionLine<>(inB, new BoolInput("B", node.getB()), null));

        this.resultOut = new ConnectionPointOut<>(this, node.getResult());
        this.items.add(new ConnectionLine<>(null, new ViewInput<>("Result", node.getResult().getValue()), this.resultOut));

        this.pack();
    }
}
