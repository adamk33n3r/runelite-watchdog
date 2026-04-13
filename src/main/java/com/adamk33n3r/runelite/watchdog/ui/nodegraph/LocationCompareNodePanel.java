package com.adamk33n3r.runelite.watchdog.ui.nodegraph;

import com.adamk33n3r.nodegraph.nodes.logic.LocationCompare;
import com.adamk33n3r.runelite.watchdog.ui.nodegraph.connections.ConnectionLine;
import com.adamk33n3r.runelite.watchdog.ui.nodegraph.connections.ConnectionPointIn;
import com.adamk33n3r.runelite.watchdog.ui.nodegraph.connections.ConnectionPointOut;
import com.adamk33n3r.runelite.watchdog.ui.nodegraph.inputs.NumberInput;
import com.adamk33n3r.runelite.watchdog.ui.nodegraph.inputs.ViewInput;
import com.adamk33n3r.runelite.watchdog.ui.panels.PanelUtils;
import lombok.Getter;
import net.runelite.api.Client;
import net.runelite.api.coords.WorldPoint;

import javax.swing.*;
import java.awt.*;

@Getter
public class LocationCompareNodePanel extends NodePanel {
    private final ConnectionPointOut<Boolean> resultOut;

    public LocationCompareNodePanel(GraphPanel graphPanel, LocationCompare node, int x, int y, String name, Color color, Client client) {
        super(graphPanel, node, x, y, name, color);

        // Input A: WorldPoint with spinners when not connected
        ConnectionPointIn<WorldPoint> inA = new ConnectionPointIn<>(this, node.getA());
        JPanel pointPanelA = createWorldPointPanel("A", node.getA().getValue(), wp -> node.getA().setValue(wp));
        addDisposer(node.getA().onConnectChange(connected -> {
            setChildrenEnabled(pointPanelA, !connected);
        }));
        JPanel labeledA = PanelUtils.createLabeledComponent("A", "Location A", pointPanelA);
        this.items.add(wrapWithConnectionPoint(inA, labeledA));

        // Button to set A to current location
        JButton setCurrentA = new JButton("Set A to Current");
        setCurrentA.addActionListener(e -> {
            if (client.getLocalPlayer() != null) {
                WorldPoint wp = WorldPoint.fromLocalInstance(client, client.getLocalPlayer().getLocalLocation());
                node.getA().setValue(wp);
                updateSpinners(pointPanelA, wp);
                this.notifyChange();
            }
        });
        this.items.add(setCurrentA);

        // Input B: WorldPoint with spinners when not connected
        ConnectionPointIn<WorldPoint> inB = new ConnectionPointIn<>(this, node.getB());
        JPanel pointPanelB = createWorldPointPanel("B", node.getB().getValue(), wp -> node.getB().setValue(wp));
        addDisposer(node.getB().onConnectChange(connected -> {
            setChildrenEnabled(pointPanelB, !connected);
        }));
        JPanel labeledB = PanelUtils.createLabeledComponent("B", "Location B", pointPanelB);
        this.items.add(wrapWithConnectionPoint(inB, labeledB));

        // Button to set B to current location
        JButton setCurrentB = new JButton("Set B to Current");
        setCurrentB.addActionListener(e -> {
            if (client.getLocalPlayer() != null) {
                WorldPoint wp = WorldPoint.fromLocalInstance(client, client.getLocalPlayer().getLocalLocation());
                node.getB().setValue(wp);
                updateSpinners(pointPanelB, wp);
                this.notifyChange();
            }
        });
        this.items.add(setCurrentB);

        // Distance spinner
        JSpinner distanceSpinner = PanelUtils.createSpinner(node.getDistance(), 0, Integer.MAX_VALUE, 1, val -> {
            node.setDistance(val);
            node.process();
            this.notifyChange();
        });
        this.items.add(PanelUtils.createLabeledComponent("Distance", "Max distance between points", distanceSpinner));

        // Cardinal Only checkbox
        JCheckBox cardinalOnlyBox = new JCheckBox("Cardinal Only", node.isCardinalOnly());
        cardinalOnlyBox.addActionListener(e -> {
            node.setCardinalOnly(cardinalOnlyBox.isSelected());
            node.process();
            this.notifyChange();
        });
        this.items.add(cardinalOnlyBox);

        // Result output
        this.resultOut = new ConnectionPointOut<>(this, node.getResult());
        ViewInput<Boolean> resultView = new ViewInput<>("Result", node.getResult().getValue());
        addDisposer(node.getA().onChange(a -> resultView.setValue(node.getResult().getValue())));
        addDisposer(node.getB().onChange(b -> resultView.setValue(node.getResult().getValue())));
        this.items.add(new ConnectionLine<>(null, resultView, this.resultOut));

        this.pack();
    }

    private JPanel createWorldPointPanel(String label, WorldPoint initial, java.util.function.Consumer<WorldPoint> onUpdate) {
        JPanel panel = new JPanel(new GridLayout(1, 3, 3, 3));
        panel.setOpaque(false);

        int[] coords = { initial.getX(), initial.getY(), initial.getPlane() };

        JSpinner xSpinner = PanelUtils.createSpinner(coords[0], Integer.MIN_VALUE, Integer.MAX_VALUE, 1, val -> {
            coords[0] = val;
            onUpdate.accept(new WorldPoint(coords[0], coords[1], coords[2]));
        });
        JSpinner ySpinner = PanelUtils.createSpinner(coords[1], Integer.MIN_VALUE, Integer.MAX_VALUE, 1, val -> {
            coords[1] = val;
            onUpdate.accept(new WorldPoint(coords[0], coords[1], coords[2]));
        });
        JSpinner planeSpinner = PanelUtils.createSpinner(coords[2], 0, 3, 1, val -> {
            coords[2] = val;
            onUpdate.accept(new WorldPoint(coords[0], coords[1], coords[2]));
        });

        panel.add(xSpinner);
        panel.add(ySpinner);
        panel.add(planeSpinner);
        return panel;
    }

    private void updateSpinners(JPanel panel, WorldPoint wp) {
        Component[] components = panel.getComponents();
        if (components.length >= 3) {
            ((JSpinner) components[0]).setValue(wp.getX());
            ((JSpinner) components[1]).setValue(wp.getY());
            ((JSpinner) components[2]).setValue(wp.getPlane());
        }
    }

    private JPanel wrapWithConnectionPoint(ConnectionPointIn<?> cp, JPanel content) {
        JPanel wrapper = new JPanel(new BorderLayout(5, 5));
        wrapper.setOpaque(false);
        wrapper.add(cp, BorderLayout.WEST);
        wrapper.add(content, BorderLayout.CENTER);
        return wrapper;
    }

    private void setChildrenEnabled(JPanel panel, boolean enabled) {
        for (Component c : panel.getComponents()) {
            c.setEnabled(enabled);
        }
    }
}
