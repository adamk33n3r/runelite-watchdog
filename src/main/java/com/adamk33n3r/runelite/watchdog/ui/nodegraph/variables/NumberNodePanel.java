package com.adamk33n3r.runelite.watchdog.ui.nodegraph.variables;

import com.adamk33n3r.nodegraph.nodes.constants.Num;
import com.adamk33n3r.runelite.watchdog.ui.nodegraph.GraphPanel;
import com.adamk33n3r.runelite.watchdog.ui.nodegraph.connections.ConnectionLine;
import com.adamk33n3r.runelite.watchdog.ui.nodegraph.connections.ConnectionPointOut;
import com.adamk33n3r.runelite.watchdog.ui.nodegraph.inputs.NumberInput;
import lombok.Getter;

import java.awt.*;

@Getter
public class NumberNodePanel extends VariableNodePanel {

    private final ConnectionPointOut<Number> numValue;

    public NumberNodePanel(GraphPanel graphPanel, Num node, int x, int y, String name, Color color) {
        super(graphPanel, node, x, y, name, color);

        this.numValue = new ConnectionPointOut<>(this, node.getValue());

        NumberInput numberInput = new NumberInput("Number Value", node.getValue());
        this.items.add(new ConnectionLine<>(null, numberInput, this.numValue));

        this.pack();
    }
}
