package com.adamk33n3r.runelite.watchdog.ui.alerts;

import com.adamk33n3r.runelite.watchdog.WatchdogPanel;
import com.adamk33n3r.runelite.watchdog.alerts.InventoryAlert;
import com.adamk33n3r.runelite.watchdog.ui.panels.AlertPanel;

public class InventoryAlertPanel extends AlertPanel<InventoryAlert> {
    public InventoryAlertPanel(WatchdogPanel watchdogPanel, InventoryAlert alert) {
        super(watchdogPanel, alert);
    }

    @Override
    protected void build() {
        this.addAlertDefaults()
            .addSelect("Type", "Type", InventoryAlert.InventoryAlertType.class, this.alert.getInventoryAlertType(), (val) -> {
                this.alert.setInventoryAlertType(val);
                this.muxer.popState();
                watchdogPanel.openAlert(alert);
            })
            .addIf(
                panel -> panel.addRegexMatcher(this.alert, "Enter the name of the item to trigger on...", "The name to trigger on. Supports glob (*)")
                    .addSpinner("Quantity", "The quantity of item to trigger on, use 0 for every time", this.alert.getItemQuantity(), this.alert::setItemQuantity),
                () -> this.alert.getInventoryAlertType() == InventoryAlert.InventoryAlertType.ITEM
            )
            .addNotifications();
    }
}
