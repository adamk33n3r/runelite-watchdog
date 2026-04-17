package com.adamk33n3r.runelite.watchdog.ui.nodegraph;

import com.adamk33n3r.nodegraph.nodes.utility.DisplayNode;
import com.adamk33n3r.runelite.watchdog.ui.nodegraph.connections.ConnectionLine;
import com.adamk33n3r.runelite.watchdog.ui.nodegraph.connections.ConnectionPointIn;
import com.adamk33n3r.runelite.watchdog.ui.nodegraph.inputs.ViewInput;
import lombok.Getter;

import java.awt.Color;

@Getter
public class DisplayNodePanel extends NodePanel {
    private final ConnectionPointIn<Object> valueIn;

    @SuppressWarnings("unchecked")
    public DisplayNodePanel(GraphPanel graphPanel, DisplayNode node, int x, int y, Color color) {
        super(graphPanel, node, x, y, "Display", color);

        this.valueIn = new ConnectionPointIn<>(this, node.getValue());
        ViewInput<Object> valueView = new ViewInput<>("Value", node.getValue().getValue());
        addDisposer(node.getValue().onChange(v -> valueView.setValue(v)));
        this.items.add(new ConnectionLine<>(this.valueIn, valueView, null));

        this.pack();
    }
}
