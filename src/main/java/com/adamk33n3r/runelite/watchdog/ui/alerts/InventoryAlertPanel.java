package com.adamk33n3r.runelite.watchdog.ui.alerts;

import com.adamk33n3r.runelite.watchdog.WatchdogPanel;
import com.adamk33n3r.runelite.watchdog.alerts.InventoryAlert;
import com.adamk33n3r.runelite.watchdog.ui.ComparableNumber;
import com.adamk33n3r.runelite.watchdog.ui.panels.AlertContentBuilder;
import com.adamk33n3r.runelite.watchdog.ui.panels.AlertPanel;
import com.adamk33n3r.runelite.watchdog.ui.panels.PanelUtils;

public class InventoryAlertPanel extends AlertPanel<InventoryAlert> {
    public InventoryAlertPanel(WatchdogPanel watchdogPanel, InventoryAlert alert) {
        super(watchdogPanel, alert);
    }

    @Override
    protected void build() {
        this.addAlertDefaults();
        buildTypeContent(this.alert, new AlertContentBuilder(this.getControlContainer(), this.getSaveAction(), this::rebuild));
        this.addNotifications();
    }

    public static void buildTypeContent(InventoryAlert alert, AlertContentBuilder builder) {
        InventoryAlert.InventoryAlertType alertType = alert.getInventoryAlertType();
        boolean isItemChange = alertType == InventoryAlert.InventoryAlertType.ITEM_CHANGE;
        builder
            .addSelect("Match", "Match on noted or un-noted", InventoryAlert.InventoryMatchType.class, alert.getInventoryMatchType(), alert::setInventoryMatchType)
            .addSelect("Type", "Type of inventory alert", InventoryAlert.InventoryAlertType.class, alertType, val -> {
                alert.setInventoryAlertType(val);
                builder.rebuild();
            })
            .addIf(
                b -> b
                    .addRegexMatcher(alert, "Enter the name of the item to trigger on...", "The name to trigger on. Supports glob (*)")
                    .addSubPanelControl(PanelUtils.createLabeledComponent(
                        isItemChange ? "Change" : "Quantity",
                        isItemChange ? "The quantity change of the item (in one tick) to trigger on. Negative for loss, positive for gain, 0 for no change" : "The quantity of item to trigger on",
                        new ComparableNumber(alert.getItemQuantity(), alert::setItemQuantity, isItemChange ? Integer.MIN_VALUE : 0, Integer.MAX_VALUE, 1, alert.getQuantityComparator(), alert::setQuantityComparator))),
                () -> alertType == InventoryAlert.InventoryAlertType.ITEM || isItemChange
            )
            .addIf(
                b -> b
                    .addSubPanelControl(PanelUtils.createLabeledComponent("Quantity", "The quantity to trigger on",
                        new ComparableNumber(alert.getItemQuantity(), alert::setItemQuantity, isItemChange ? Integer.MIN_VALUE : 0, Integer.MAX_VALUE, 1, alert.getQuantityComparator(), alert::setQuantityComparator))),
                () -> alertType == InventoryAlert.InventoryAlertType.SLOTS
            );
    }
}
