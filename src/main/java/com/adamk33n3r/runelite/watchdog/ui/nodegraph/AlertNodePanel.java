package com.adamk33n3r.runelite.watchdog.ui.nodegraph;

import com.adamk33n3r.nodegraph.nodes.ContinuousTriggerNode;
import com.adamk33n3r.runelite.watchdog.alerts.Alert;
import com.adamk33n3r.nodegraph.nodes.TriggerNode;
import com.adamk33n3r.runelite.watchdog.ui.nodegraph.connections.ConnectionLine;
import com.adamk33n3r.runelite.watchdog.ui.nodegraph.connections.ConnectionPointIn;
import com.adamk33n3r.runelite.watchdog.ui.nodegraph.connections.ConnectionPointOut;
import com.adamk33n3r.runelite.watchdog.ui.nodegraph.inputs.BoolInput;
import com.adamk33n3r.runelite.watchdog.ui.nodegraph.inputs.NumberInput;
import com.adamk33n3r.runelite.watchdog.ui.nodegraph.inputs.TextInput;
import com.adamk33n3r.runelite.watchdog.ui.nodegraph.inputs.ViewInput;
import com.adamk33n3r.runelite.watchdog.ui.panels.AlertContentBuilder;
import com.adamk33n3r.runelite.watchdog.ui.panels.AlertPanelContentFactory;
import com.adamk33n3r.runelite.watchdog.ui.panels.PanelUtils;

import lombok.Getter;

import javax.swing.*;
import java.awt.*;

@Getter
public class AlertNodePanel extends NodePanel {
    private ConnectionPointOut<Boolean> isTriggered;

    private final ConnectionPointOut<String[]> captureGroupsOut;
    private final ConnectionPointOut<String> alertName;
    private final ConnectionPointOut<Number> testOut;
    private final ConnectionPointIn<Boolean> enabled;

    public AlertNodePanel(GraphPanel graphPanel, int x, int y, String name, Color color, TriggerNode triggerNode, AlertPanelContentFactory alertPanelContentFactory) {
        super(graphPanel, triggerNode, x, y, name, color);
        Alert alert = triggerNode.getAlert();

        if (triggerNode instanceof ContinuousTriggerNode) {
            ContinuousTriggerNode continuousTriggerNode = (ContinuousTriggerNode) triggerNode;
            this.isTriggered = new ConnectionPointOut<>(this, continuousTriggerNode.getIsTriggered());
            ViewInput<Boolean> isTriggeredView = new ViewInput<>("Is Triggered", continuousTriggerNode.getIsTriggered().getValue());
            this.items.add(new ConnectionLine<>(null, isTriggeredView, this.isTriggered));

            JButton toggleIsTriggered = new JButton("Toggle Is Triggered");
            toggleIsTriggered.addActionListener((ev) -> isTriggeredView.setValue(!isTriggeredView.getValue()));
            this.items.add(toggleIsTriggered);
        }

        this.captureGroupsOut = new ConnectionPointOut<>(this, triggerNode.getCaptureGroups());
        this.items.add(new ConnectionLine<>(null, new ViewInput<>("Triggered", triggerNode.getCaptureGroups().getValue()), this.captureGroupsOut));
        this.alertName = new ConnectionPointOut<>(this, triggerNode.getNameOut());
        this.items.add(new ConnectionLine<>(null, new TextInput("Alert Name", triggerNode.getNameOut().getValue()), this.alertName));
        this.testOut = new ConnectionPointOut<>(this, triggerNode.getDebounceOut());
        this.items.add(new ConnectionLine<>(new ConnectionPointIn<>(this, triggerNode.getDebounce()), new NumberInput("Debounce", triggerNode.getDebounceOut().getValue().intValue()), this.testOut));
        this.enabled = new ConnectionPointIn<>(this, triggerNode.getEnabled());
        BoolInput enabledInput = new BoolInput("Enabled", triggerNode.getEnabled());
        this.items.add(new ConnectionLine<>(this.enabled, enabledInput, null));

        JButton testBtn = new JButton("TEST");
        testBtn.addActionListener((ev) -> graphPanel.trigger(triggerNode));
        this.items.add(testBtn);

        TextInput nameInput = new TextInput("Name", alert.getName());
        nameInput.registerOnChange(v -> {
            alert.setName(v);
            this.notifyChange();
        });
        this.items.add(new ConnectionLine<>(null, nameInput, this.alertName));

        JSpinner debounce = PanelUtils.createSpinner(alert.getDebounceTime(), 0, 8640000, 100, (val) -> {
            triggerNode.getDebounce().setValue(val);
            graphPanel.processNode(triggerNode);
            this.notifyChange();
        });
        this.items.add(PanelUtils.createLabeledComponent("Debounce Time (ms)", "How long to wait before allowing this alert to trigger again in milliseconds", debounce));

        // Type-specific controls via factory — supports rebuild for conditional UI panels
        final int fixedItemCount = this.items.getComponentCount();
        Runnable[] holder = { null };
        holder[0] = () -> {
            Component[] comps = this.items.getComponents();
            for (int i = comps.length - 1; i >= fixedItemCount; i--) {
                this.items.remove(comps[i]);
            }
            AlertContentBuilder b = new AlertContentBuilder(this.items, this::notifyChange, holder[0]);
            alertPanelContentFactory.populateContent(alert, b);
            this.items.revalidate();
            this.items.repaint();
            this.pack();
        };
        AlertContentBuilder builder = new AlertContentBuilder(this.items, this::notifyChange, holder[0]);
        alertPanelContentFactory.populateContent(alert, builder);

        this.pack();
    }
}
