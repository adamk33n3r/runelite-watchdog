package com.adamk33n3r.runelite.watchdog.ui.nodegraph;

import com.adamk33n3r.runelite.watchdog.nodegraph.nodes.constants.Bool;
import com.adamk33n3r.runelite.watchdog.ui.panels.PanelUtils;
import lombok.Getter;

import javax.swing.*;
import java.awt.*;

@Getter
public class BoolNodePanel extends NodePanel {
    private final ConnectionPointOut<Boolean> boolValue;

    public BoolNodePanel(GraphPanel graphPanel, Bool boolNode, int x, int y, String name, Color color) {
        super(graphPanel, boolNode, x, y, name, color);

        this.boolValue = new ConnectionPointOut<>(this, boolNode.getValue());
        this.outConnectionPoints.add(this.boolValue);

        JCheckBox enabled = PanelUtils.createCheckbox("Boolean Value", "True or False", boolNode.getValue().getValue(), (val) -> {
            boolNode.getValue().setValue(val);
            graphPanel.processNode(boolNode);
        });
        this.items.add(enabled);
    }
}
