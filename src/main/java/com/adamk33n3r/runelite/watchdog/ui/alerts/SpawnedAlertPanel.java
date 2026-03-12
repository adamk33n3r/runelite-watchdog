package com.adamk33n3r.runelite.watchdog.ui.alerts;

import com.adamk33n3r.runelite.watchdog.WatchdogPanel;
import com.adamk33n3r.runelite.watchdog.alerts.SpawnedAlert;
import com.adamk33n3r.runelite.watchdog.ui.ComparableNumber;
import com.adamk33n3r.runelite.watchdog.ui.panels.AlertPanel;
import com.adamk33n3r.runelite.watchdog.ui.panels.PanelUtils;
import net.runelite.client.plugins.grounditems.config.OwnershipFilterMode;

public class SpawnedAlertPanel extends AlertPanel<SpawnedAlert> {
    public SpawnedAlertPanel(WatchdogPanel watchdogPanel, SpawnedAlert alert) {
        super(watchdogPanel, alert);
    }

    @Override
    protected void build() {
        this.addAlertDefaults()
            .addSelect("Spawned/Despawned", "Spawned or Despawned", SpawnedAlert.SpawnedDespawned.class, this.alert.getSpawnedDespawned(), this.alert::setSpawnedDespawned)
            .addSelect("Type", "The type of object to trigger on", SpawnedAlert.SpawnedType.class, this.alert.getSpawnedType(), val -> {
                this.alert.setSpawnedType(val);
                this.rebuild();
            })
            .addIf(panel ->
                panel.addSelect("Ownership", "Filter items by ownership", OwnershipFilterMode.class, this.alert.getItemOwnershipFilterMode(), this.alert::setItemOwnershipFilterMode),
                () -> this.alert.getSpawnedType() == SpawnedAlert.SpawnedType.ITEM)
            .addSubPanelControl(PanelUtils.createLabeledComponent(
                "Distance Limit",
                "Limit to a distance from the player. Use -1 for no limit. For objects which are larger than 1 tile, the location is the center most tile, rounded to the south-west",
                new ComparableNumber(this.alert.getDistance(), this.alert::setDistance, -1, Integer.MAX_VALUE, 1, this.alert.getDistanceComparator(), this.alert::setDistanceComparator)))
            .addRegexMatcher(this.alert, "Enter the object to trigger on...", "The name to trigger on. Supports glob (*)")
            .addNotifications();
    }
}
