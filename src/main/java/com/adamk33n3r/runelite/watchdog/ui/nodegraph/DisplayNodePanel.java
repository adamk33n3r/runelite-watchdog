package com.adamk33n3r.runelite.watchdog.ui.nodegraph;

import com.adamk33n3r.nodegraph.nodes.utility.DisplayNode;
import com.adamk33n3r.runelite.watchdog.ui.nodegraph.connections.ConnectionLine;
import com.adamk33n3r.runelite.watchdog.ui.nodegraph.connections.ConnectionPointIn;
import com.adamk33n3r.runelite.watchdog.ui.nodegraph.displayview.DisplayValueView;
import com.adamk33n3r.runelite.watchdog.ui.nodegraph.inputs.NullInput;

import net.runelite.client.game.ItemManager;

import lombok.Getter;

import java.awt.Color;

@Getter
public class DisplayNodePanel extends NodePanel {
    private final ConnectionPointIn<Object> valueIn;

    @SuppressWarnings("unchecked")
    public DisplayNodePanel(GraphPanel graphPanel, DisplayNode node, int x, int y, Color color, ItemManager itemManager) {
        super(graphPanel, node, x, y, "Display", color);

        this.valueIn = new ConnectionPointIn<>(this, node.getValue());
        this.items.add(new ConnectionLine<>(this.valueIn, new NullInput<>(), null));

        DisplayValueView valueView = new DisplayValueView(itemManager);
        valueView.render(node.getValue().getValue());
        addDisposer(node.getValue().onChange(v -> {
            valueView.render(v);
            this.pack();
        }));
        this.items.add(valueView);

        this.pack();
    }
}
