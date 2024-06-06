package com.adamk33n3r.runelite.watchdog.ui.nodegraph;

import com.adamk33n3r.runelite.watchdog.alerts.Alert;
import com.adamk33n3r.runelite.watchdog.alerts.ChatAlert;
import com.adamk33n3r.runelite.watchdog.alerts.SpawnedAlert;
import com.adamk33n3r.nodegraph.nodes.TriggerNode;
import com.adamk33n3r.runelite.watchdog.ui.nodegraph.connections.ConnectionLine;
import com.adamk33n3r.runelite.watchdog.ui.nodegraph.connections.ConnectionPointIn;
import com.adamk33n3r.runelite.watchdog.ui.nodegraph.connections.ConnectionPointOut;
import com.adamk33n3r.runelite.watchdog.ui.nodegraph.inputs.BoolInput;
import com.adamk33n3r.runelite.watchdog.ui.nodegraph.inputs.NumberInput;
import com.adamk33n3r.runelite.watchdog.ui.nodegraph.inputs.TextInput;
import com.adamk33n3r.runelite.watchdog.ui.nodegraph.inputs.ViewInput;
import com.adamk33n3r.runelite.watchdog.ui.panels.PanelUtils;
import lombok.Getter;

import javax.swing.*;
import java.awt.Color;

@Getter
public class AlertNodePanel extends NodePanel {
    private final ConnectionPointOut<String[]> captureGroupsOut;
    private final ConnectionPointOut<String> alertName;
    private final ConnectionPointOut<Number> testOut;
    private final ConnectionPointOut<Boolean> enabledOut;
    private final ConnectionPointIn<Boolean> enabled;

    public AlertNodePanel(GraphPanel graphPanel, int x, int y, String name, Color color, TriggerNode triggerNode) {
        super(graphPanel, triggerNode, x, y, name, color);
        Alert alert = triggerNode.getAlert();

        this.captureGroupsOut = new ConnectionPointOut<>(this, triggerNode.getCaptureGroups());
        this.items.add(new ConnectionLine<>(null, new ViewInput<>("Capture Groups", triggerNode.getCaptureGroups().getValue()), this.captureGroupsOut));
        this.alertName = new ConnectionPointOut<>(this, triggerNode.getNameOut());
        this.items.add(new ConnectionLine<>(null, new TextInput("Alert Name", triggerNode.getNameOut().getValue()), this.alertName));
//        this.outConnectionPoints.add(this.captureGroupsOut);
//        this.outConnectionPoints.add(this.alertName);
        this.testOut = new ConnectionPointOut<>(this, triggerNode.getDebounceOut());
        this.items.add(new ConnectionLine<>(null, new NumberInput("Test", triggerNode.getDebounceOut().getValue().intValue()), this.testOut));
//        this.outConnectionPoints.add(this.testOut);
        this.enabled = new ConnectionPointIn<>(this, triggerNode.getEnabled());
        BoolInput enabledInput = new BoolInput("Enabled", triggerNode.getEnabled().getValue());
        this.items.add(new ConnectionLine<>(this.enabled, enabledInput, null));

        // TODO: disallow connecting to same node
        this.enabledOut = new ConnectionPointOut<>(this, triggerNode.getEnabled().toOutput());
        this.items.add(new ConnectionLine<>(null, new BoolInput("Enabled Out", triggerNode.getEnabled().getValue()), this.enabledOut));


        JButton testBtn = new JButton("TEST");
        testBtn.addActionListener((ev) -> graphPanel.trigger(triggerNode));
        this.items.add(testBtn);

        TextInput nameInput = new TextInput("Name", alert.getName());
        this.items.add(new ConnectionLine<>(null, nameInput, this.alertName));
        JSpinner debounce = PanelUtils.createSpinner(alert.getDebounceTime(), 0, 8640000, 100, (val) -> {
            triggerNode.getDebounce().setValue(val);
            graphPanel.processNode(triggerNode);
        });

        JPanel labeledComponent = PanelUtils.createLabeledComponent("Debounce Time (ms)", "How long to wait before allowing this alert to trigger again in milliseconds", debounce);
        this.items.add(labeledComponent);
        if (alert instanceof ChatAlert) {
            this.items.add(new TextInput("Message", ((ChatAlert) alert).getPattern()));
        } else if (alert instanceof SpawnedAlert) {
            SpawnedAlert spawnedAlert = (SpawnedAlert) alert;
            JComboBox<SpawnedAlert.SpawnedDespawned> spawnedDespawned = PanelUtils.createSelect(SpawnedAlert.SpawnedDespawned.values(), spawnedAlert.getSpawnedDespawned(), spawnedAlert::setSpawnedDespawned);
            this.items.add(spawnedDespawned);
            JComboBox<SpawnedAlert.SpawnedType> spawnedType = PanelUtils.createSelect(SpawnedAlert.SpawnedType.values(), spawnedAlert.getSpawnedType(), spawnedAlert::setSpawnedType);
            this.items.add(spawnedType);
            this.items.add(new TextInput("Enter the object to trigger on...", spawnedAlert.getPattern()));
            /*
             *
             this.addAlertDefaults()
             .addSelect("Spawned/Despawned", "Spawned or Despawned", SpawnedAlert.SpawnedDespawned.class, this.alert.getSpawnedDespawned(), this.alert::setSpawnedDespawned)
             .addSelect("Type", "The type of object to trigger on", SpawnedAlert.SpawnedType.class, this.alert.getSpawnedType(), this.alert::setSpawnedType)
             .addRegexMatcher(this.alert, "Enter the object to trigger on...", "The name to trigger on. Supports glob (*)")
             */
        }

        this.pack();
    }
}
