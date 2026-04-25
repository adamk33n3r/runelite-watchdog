package com.adamk33n3r.runelite.watchdog.ui.nodegraph;

import com.adamk33n3r.nodegraph.nodes.utility.ToStringNode;
import com.adamk33n3r.runelite.watchdog.ui.nodegraph.connections.ConnectionLine;
import com.adamk33n3r.runelite.watchdog.ui.nodegraph.connections.ConnectionPointIn;
import com.adamk33n3r.runelite.watchdog.ui.nodegraph.connections.ConnectionPointOut;
import com.adamk33n3r.runelite.watchdog.ui.nodegraph.inputs.NullInput;
import com.adamk33n3r.runelite.watchdog.ui.nodegraph.inputs.ViewInput;
import lombok.Getter;

import java.awt.Color;

@Getter
public class ToStringNodePanel extends NodePanel {
    private final ConnectionPointIn<Object> valueIn;
    private final ConnectionPointOut<String> resultOut;

    public ToStringNodePanel(GraphPanel graphPanel, ToStringNode node, int x, int y, Color color) {
        super(graphPanel, node, x, y, "To String", color);

        this.valueIn = new ConnectionPointIn<>(this, node.getValue());
        this.items.add(new ConnectionLine<>(this.valueIn, new NullInput<>(), null));

        this.resultOut = new ConnectionPointOut<>(this, node.getResult());
        ViewInput<String> resultView = new ViewInput<>("Result", node.getResult().getValue());
        addDisposer(node.getValue().onChange(v -> resultView.setValue(node.getResult().getValue())));
        this.items.add(new ConnectionLine<>(null, resultView, this.resultOut));

        this.pack();
    }
}
