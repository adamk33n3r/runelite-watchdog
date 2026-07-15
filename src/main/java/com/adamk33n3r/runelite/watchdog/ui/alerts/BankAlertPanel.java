package com.adamk33n3r.runelite.watchdog.ui.alerts;

import com.adamk33n3r.runelite.watchdog.alerts.BankAlert;
import com.adamk33n3r.runelite.watchdog.alerts.InventoryAlert;
import com.adamk33n3r.runelite.watchdog.ui.ComparableNumber;
import com.adamk33n3r.runelite.watchdog.ui.panels.AlertContentPanel;
import com.adamk33n3r.runelite.watchdog.ui.panels.PanelUtils;

public class BankAlertPanel extends AlertContentPanel<BankAlert> {

    public BankAlertPanel(BankAlert alert, Runnable onChange) {
        super(alert, onChange);
        this.init();
    }

    @Override
    public void buildTypeContent() {
        BankAlert.BankAlertType alertType = this.alert.getBankAlertType();
        boolean isItemChange = alertType == BankAlert.BankAlertType.ITEM_CHANGE;
        this.addSelect("Type", "Type of bank alert", BankAlert.BankAlertType.class, alertType,
            val -> {
                this.alert.setBankAlertType(val);
                this.rebuild();
            })
            .addIf(
                b -> b.addCheckbox("Don't fire when opening bank", "Only fire when the condition first becomes true when removing or adding to the bank", this.alert.isFireOnChange(), this.alert::setFireOnChange),
                () -> alertType != BankAlert.BankAlertType.ITEM_CHANGE
            )
            .addIf(
                b -> b
                    .addRegexMatcher(this.alert, "Enter the name of the item to trigger on...", "The name to trigger on. Supports glob (*)")
                    .addSubPanelControl(PanelUtils.createLabeledComponent(
                        isItemChange ? "Change" : "Quantity",
                        isItemChange ? "The quantity change of the item (in one tick) to trigger on. Negative for loss, positive for gain, 0 for no change" : "The quantity of item to trigger on",
                        new ComparableNumber(this.alert.getItemQuantity(), this.alert::setItemQuantity, isItemChange ? Integer.MIN_VALUE : 0, Integer.MAX_VALUE, 1, this.alert.getQuantityComparator(), this.alert::setQuantityComparator))),
                () -> alertType == BankAlert.BankAlertType.ITEM || isItemChange
            );
    }
}
