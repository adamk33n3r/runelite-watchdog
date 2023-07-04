package com.adamk33n3r.runelite.watchdog.ui.alerts;

import com.adamk33n3r.runelite.watchdog.WatchdogPanel;
import com.adamk33n3r.runelite.watchdog.alerts.SpawnedAlert;
import com.adamk33n3r.runelite.watchdog.ui.panels.AlertPanel;

public class SpawnedAlertPanel extends AlertPanel<SpawnedAlert> {
    public SpawnedAlertPanel(WatchdogPanel watchdogPanel, SpawnedAlert alert) {
        super(watchdogPanel, alert);
    }

    @Override
    protected void build() {
        this.addAlertDefaults()
            .addSelect("Spawned/Despawned", "Spawned or Despawned", SpawnedAlert.SpawnedDespawned.class, this.alert.getSpawnedDespawned(), this.alert::setSpawnedDespawned)
            .addSelect("Type", "The type of object to trigger on", SpawnedAlert.SpawnedType.class, this.alert.getSpawnedType(), this.alert::setSpawnedType)
            .addRegexMatcher(this.alert, "Enter the object to trigger on...", "The name to trigger on. Supports glob (*)")
            .addNotifications();
    }
}
