package com.adamk33n3r.runelite.watchdog.ui.nodegraph;

import com.adamk33n3r.runelite.watchdog.alerts.Alert;
import com.adamk33n3r.runelite.watchdog.alerts.ChatAlert;
import com.adamk33n3r.runelite.watchdog.alerts.SpawnedAlert;
import com.adamk33n3r.runelite.watchdog.ui.alerts.GameMessageAlertPanel;
import com.adamk33n3r.runelite.watchdog.ui.panels.PanelUtils;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import java.awt.BorderLayout;
import java.awt.Color;

public class AlertNode extends Node {
    public AlertNode(Graph graph, int x, int y, String name, Color color, Alert alert) {
        super(graph, x, y, name, color);

        this.items.add(new TextInput("Name", alert.getName()));
        JSpinner debounce = PanelUtils.createSpinner(alert.getDebounceTime(), 0, 8640000, 100, alert::setDebounceTime);
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
            /**
             *
             this.addAlertDefaults()
             .addSelect("Spawned/Despawned", "Spawned or Despawned", SpawnedAlert.SpawnedDespawned.class, this.alert.getSpawnedDespawned(), this.alert::setSpawnedDespawned)
             .addSelect("Type", "The type of object to trigger on", SpawnedAlert.SpawnedType.class, this.alert.getSpawnedType(), this.alert::setSpawnedType)
             .addRegexMatcher(this.alert, "Enter the object to trigger on...", "The name to trigger on. Supports glob (*)")
             */
        }

        ConnectionPoint connectionPoint = new ConnectionPoint(this);
        this.add(connectionPoint, BorderLayout.EAST);
    }
}
