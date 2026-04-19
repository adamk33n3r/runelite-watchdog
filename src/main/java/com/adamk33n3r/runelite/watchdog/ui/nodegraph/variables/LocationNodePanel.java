package com.adamk33n3r.runelite.watchdog.ui.nodegraph.variables;

import com.adamk33n3r.nodegraph.nodes.constants.Location;
import com.adamk33n3r.runelite.watchdog.ui.nodegraph.GraphPanel;
import com.adamk33n3r.runelite.watchdog.ui.nodegraph.connections.ConnectionLine;
import com.adamk33n3r.runelite.watchdog.ui.nodegraph.connections.ConnectionPointOut;
import com.adamk33n3r.runelite.watchdog.ui.nodegraph.inputs.ViewInput;
import lombok.Getter;
import net.runelite.api.coords.WorldPoint;

import java.awt.*;

@Getter
public class LocationNodePanel extends VariableNodePanel {
    private final ConnectionPointOut<WorldPoint> locationOut;

    public LocationNodePanel(GraphPanel graphPanel, Location node, int x, int y, String name, Color color) {
        super(graphPanel, node, x, y, name, color);

        this.locationOut = new ConnectionPointOut<>(this, node.getValueOut());

        ViewInput<WorldPoint> locationView = new ViewInput<>("Location", node.getValueOut().getValue());
        addDisposer(node.getValue().onChange(a -> locationView.setValue(node.getValue().getValue())));
        this.items.add(new ConnectionLine<>(null, locationView, this.locationOut));

        this.pack();
    }
}
