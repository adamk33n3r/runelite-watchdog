package com.adamk33n3r.runelite.watchdog.ui.alerts;

import com.adamk33n3r.runelite.watchdog.WatchdogPanel;
import com.adamk33n3r.runelite.watchdog.alerts.InventoryAlert;
import com.adamk33n3r.runelite.watchdog.ui.ComparableNumber;
import com.adamk33n3r.runelite.watchdog.ui.panels.AlertPanel;
import com.adamk33n3r.runelite.watchdog.ui.panels.PanelUtils;

public class InventoryAlertPanel extends AlertPanel<InventoryAlert> {
    public InventoryAlertPanel(WatchdogPanel watchdogPanel, InventoryAlert alert) {
        super(watchdogPanel, alert);
    }

    @Override
    protected void build() {
        InventoryAlert.InventoryAlertType alertType = this.alert.getInventoryAlertType();
        boolean isItemChange = alertType == InventoryAlert.InventoryAlertType.ITEM_CHANGE;
        this.addAlertDefaults()
            .addSelect("Match", "Match on noted or un-noted", InventoryAlert.InventoryMatchType.class, this.alert.getInventoryMatchType(), this.alert::setInventoryMatchType)
            .addSelect("Type", "Type of inventory alert", InventoryAlert.InventoryAlertType.class, alertType, (val) -> {
                this.alert.setInventoryAlertType(val);
                this.rebuild();
            })
            .addIf(
                panel -> panel
                    .addRegexMatcher(this.alert, "Enter the name of the item to trigger on...", "The name to trigger on. Supports glob (*)")
                    .addSubPanelControl(PanelUtils.createLabeledComponent(
                        isItemChange ? "Change" : "Quantity",
                        isItemChange ? "The quantity change of the item (in one tick) to trigger on. Negative for loss, positive for gain, 0 for no change" : "The quantity of item to trigger on",
                        new ComparableNumber(this.alert.getItemQuantity(), this.alert::setItemQuantity, isItemChange ? Integer.MIN_VALUE : 0, Integer.MAX_VALUE, 1, this.alert.getQuantityComparator(), this.alert::setQuantityComparator))),
                () -> alertType == InventoryAlert.InventoryAlertType.ITEM || isItemChange
            )
            .addIf(
                panel -> panel
                    .addSubPanelControl(PanelUtils.createLabeledComponent("Quantity", "The quantity to trigger on",
                        new ComparableNumber(this.alert.getItemQuantity(), this.alert::setItemQuantity, isItemChange ? Integer.MIN_VALUE : 0, Integer.MAX_VALUE, 1, this.alert.getQuantityComparator(), this.alert::setQuantityComparator))),
                () -> alertType == InventoryAlert.InventoryAlertType.SLOTS
            )
            .addNotifications();
    }
}
