package com.adamk33n3r.runelite.watchdog.ui.nodegraph;

import com.adamk33n3r.nodegraph.ExecSignal;
import com.adamk33n3r.nodegraph.nodes.ContinuousTriggerNode;
import com.adamk33n3r.runelite.watchdog.alerts.Alert;
import com.adamk33n3r.nodegraph.nodes.TriggerNode;
import com.adamk33n3r.runelite.watchdog.ui.nodegraph.connections.ConnectionLine;
import com.adamk33n3r.runelite.watchdog.ui.nodegraph.connections.ConnectionPointIn;
import com.adamk33n3r.runelite.watchdog.ui.nodegraph.connections.ConnectionPointOut;
import com.adamk33n3r.runelite.watchdog.ui.nodegraph.inputs.BoolInput;
import com.adamk33n3r.runelite.watchdog.ui.nodegraph.inputs.ViewInput;
import com.adamk33n3r.runelite.watchdog.ui.panels.AlertContentPanel;
import com.adamk33n3r.runelite.watchdog.ui.panels.AlertPanelContentFactory;

import lombok.Getter;

import javax.swing.*;
import java.awt.*;
import java.util.Random;

@Getter
public class AlertNodePanel extends NodePanel {
    private ConnectionPointOut<Boolean> isTriggered;

    private final ConnectionPointOut<ExecSignal> execOut;
//    private final ConnectionPointOut<String> alertName;
//    private final ConnectionPointOut<Number> debounceOut;
    private final ConnectionPointIn<Boolean> enabled;
    private final ConnectionPointOut<Boolean> enabledOut;

    private final transient TriggerNode triggerNode;

    public AlertNodePanel(GraphPanel graphPanel, int x, int y, String name, Color color, TriggerNode triggerNode, AlertPanelContentFactory alertPanelContentFactory) {
        super(graphPanel, triggerNode, x, y, name, color);
        this.triggerNode = triggerNode;
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

        this.execOut = new ConnectionPointOut<>(this, triggerNode.getExec());
        ViewInput<ExecSignal> exec = new ViewInput<>("Exec", triggerNode.getExec().getValue());
        addDisposer(triggerNode.getCaptureGroupsIn().onChange((captureGroups) -> exec.setValue(new ExecSignal(captureGroups))));
        this.items.add(new ConnectionLine<>(null, exec, this.execOut));
//        this.alertName = new ConnectionPointOut<>(this, triggerNode.getNameOut());
//        this.items.add(new ConnectionLine<>(null, new TextInput("Alert Name", triggerNode.getNameOut().getValue()), this.alertName));
//        this.debounceOut = new ConnectionPointOut<>(this, triggerNode.getDebounceOut());
//        this.items.add(new ConnectionLine<>(new ConnectionPointIn<>(this, triggerNode.getDebounce()), new NumberInput("Debounce", triggerNode.getDebounceOut().getValue().intValue()), this.testOut));
        this.enabled = new ConnectionPointIn<>(this, triggerNode.getEnabled());
        BoolInput enabledInput = new BoolInput("Enabled", triggerNode.getEnabled());
        this.enabledOut = new ConnectionPointOut<>(this, triggerNode.getEnabledOut());
        this.items.add(new ConnectionLine<>(this.enabled, enabledInput, this.enabledOut));

        // Type-specific controls via factory — supports rebuild for conditional UI panels
        AlertContentPanel<?> content = alertPanelContentFactory.createContentPanel(alert, this::notifyChange);
        if (content != null) {
            content.setOnNameType(this::updateHeaderLabel);
            content.setOnNameChange(() -> this.updateHeaderLabel(triggerNode.getAlert().getName()));
            content.setOnRebuild(() -> {
                this.items.revalidate();
                this.items.repaint();
                this.pack();
            });
//            content.buildTypeContent();
            this.items.add(content);
        }

        JButton testBtn = new JButton("TEST");
        testBtn.addActionListener((ev) -> {
            int count = new Random().nextInt(4) + 1;
            String[] strings = new String[count];
            for (int i = 1; i < count + 1; i++) {
                strings[i - 1] = String.format("test%d", i);
            }
            graphPanel.trigger(triggerNode, strings);
        });
        this.items.add(testBtn);

        this.pack();
    }

    public void updateHeaderLabel(String newName) {
        String name = String.format("%s - %s", newName, this.triggerNode.getAlert().getType().getName());
        super.updateHeaderLabel(name);
    }
}
