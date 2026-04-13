package com.adamk33n3r.runelite.watchdog.ui.nodegraph.variables;

import com.adamk33n3r.nodegraph.nodes.constants.Num;
import com.adamk33n3r.runelite.watchdog.ui.nodegraph.GraphPanel;
import com.adamk33n3r.runelite.watchdog.ui.nodegraph.NodePanel;
import com.adamk33n3r.runelite.watchdog.ui.nodegraph.connections.ConnectionLine;
import com.adamk33n3r.runelite.watchdog.ui.nodegraph.connections.ConnectionPointOut;
import com.adamk33n3r.runelite.watchdog.ui.nodegraph.inputs.NumberInput;
import com.adamk33n3r.runelite.watchdog.ui.nodegraph.inputs.TextInput;
import lombok.Getter;

import java.awt.*;

@Getter
public class NumberNodePanel extends NodePanel {

    private final ConnectionPointOut<Number> numValue;

    public NumberNodePanel(GraphPanel graphPanel, Num node, int x, int y, String name, Color color) {
        super(graphPanel, node, x, y, name, color);

        TextInput nameInput = new TextInput("Name", node.getNameOut());
        nameInput.registerOnType(this::updateHeaderLabel);
        this.items.add(nameInput);

        this.numValue = new ConnectionPointOut<>(this, node.getValue());

        NumberInput numberInput = new NumberInput("Number Value", node.getValue());
        this.items.add(new ConnectionLine<>(null, numberInput, this.numValue));

        this.pack();
    }
}
