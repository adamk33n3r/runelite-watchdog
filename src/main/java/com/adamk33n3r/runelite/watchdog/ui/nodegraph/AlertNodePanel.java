package com.adamk33n3r.runelite.watchdog.ui.nodegraph;

import com.adamk33n3r.runelite.watchdog.alerts.Alert;
import com.adamk33n3r.runelite.watchdog.alerts.ChatAlert;
import com.adamk33n3r.runelite.watchdog.alerts.SpawnedAlert;
import com.adamk33n3r.runelite.watchdog.nodegraph.nodes.TriggerNode;
import com.adamk33n3r.runelite.watchdog.ui.panels.PanelUtils;
import lombok.Getter;

import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.Color;

@Getter
public class AlertNodePanel extends NodePanel {
    private final ConnectionPointOut<String[]> captureGroupsOut;
    private final ConnectionPointOut<String> alertName;
    private final ConnectionPointOut<Number> testOut;

    public AlertNodePanel(GraphPanel graphPanel, int x, int y, String name, Color color, TriggerNode triggerNode) {
        super(graphPanel, triggerNode, x, y, name, color);
        Alert alert = triggerNode.getAlert();

        this.captureGroupsOut = new ConnectionPointOut<>(this, triggerNode.getCaptureGroups());
        this.alertName = new ConnectionPointOut<>(this, triggerNode.getNameOut());
        this.outConnectionPoints.add(this.captureGroupsOut);
        this.outConnectionPoints.add(this.alertName);
        this.testOut = new ConnectionPointOut<>(this, triggerNode.getDebounceOut());
        this.outConnectionPoints.add(this.testOut);

        JButton testBtn = new JButton("TEST");
        testBtn.addActionListener((ev) -> graphPanel.processNode(triggerNode));
        this.items.add(testBtn);

        this.items.add(new TextInput("Name", alert.getName()));
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
    }
}
