package com.adamk33n3r.runelite.watchdog.ui.alerts;

import com.adamk33n3r.runelite.watchdog.WatchdogPanel;
import com.adamk33n3r.runelite.watchdog.alerts.SpawnedAlert;
import com.adamk33n3r.runelite.watchdog.ui.ComparableNumber;
import com.adamk33n3r.runelite.watchdog.ui.panels.AlertContentBuilder;
import com.adamk33n3r.runelite.watchdog.ui.panels.AlertPanel;
import com.adamk33n3r.runelite.watchdog.ui.panels.PanelUtils;

import net.runelite.client.plugins.grounditems.config.OwnershipFilterMode;

public class SpawnedAlertPanel extends AlertPanel<SpawnedAlert> {
    public SpawnedAlertPanel(WatchdogPanel watchdogPanel, SpawnedAlert alert) {
        super(watchdogPanel, alert);
    }

    @Override
    protected void build() {
        this.addAlertDefaults();
        buildTypeContent(this.alert, new AlertContentBuilder(this.getControlContainer(), this.getSaveAction(), this::rebuild));
        this.addNotifications();
    }

    public static void buildTypeContent(SpawnedAlert alert, AlertContentBuilder builder) {
        builder
            .addSelect("Spawned/Despawned", "Spawned or Despawned", SpawnedAlert.SpawnedDespawned.class, alert.getSpawnedDespawned(), alert::setSpawnedDespawned)
            .addSelect("Type", "The type of object to trigger on", SpawnedAlert.SpawnedType.class, alert.getSpawnedType(), val -> {
                alert.setSpawnedType(val);
                builder.rebuild();
            })
            .addIf(b ->
                b.addSelect("Ownership", "Filter items by ownership", OwnershipFilterMode.class, alert.getItemOwnershipFilterMode(), alert::setItemOwnershipFilterMode),
                () -> alert.getSpawnedType() == SpawnedAlert.SpawnedType.ITEM)
            .addSubPanelControl(PanelUtils.createLabeledComponent(
                "Distance Limit",
                "Limit to a distance from the player. Use -1 for no limit. For objects which are larger than 1 tile, the location is the center most tile, rounded to the south-west",
                new ComparableNumber(alert.getDistance(), alert::setDistance, -1, Integer.MAX_VALUE, 1, alert.getDistanceComparator(), alert::setDistanceComparator)))
            .addRegexMatcher(alert, "Enter the object to trigger on...", "The name to trigger on. Supports glob (*)");
    }
}
