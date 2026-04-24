package com.adamk33n3r.nodegraph.nodes.logic;

import com.adamk33n3r.nodegraph.Node;
import com.adamk33n3r.nodegraph.VarInput;
import com.adamk33n3r.nodegraph.VarOutput;
import com.adamk33n3r.runelite.watchdog.InventoryItemData;
import com.adamk33n3r.runelite.watchdog.Util;
import com.adamk33n3r.runelite.watchdog.alerts.InventoryAlert;
import com.adamk33n3r.runelite.watchdog.alerts.RegexMatcher;
import com.adamk33n3r.runelite.watchdog.ui.ComparableNumber;
import lombok.Getter;

import java.util.Map;

@Getter
public class InventoryCheck extends Node implements RegexMatcher {
    private static final InventoryItemData.InventoryItemDataMap EMPTY_INVENTORY = new InventoryItemData.InventoryItemDataMap(0);

    private final VarInput<InventoryItemData.InventoryItemDataMap> inventory =
        new VarInput<>(this, "Inventory", InventoryItemData.InventoryItemDataMap.class, EMPTY_INVENTORY);
    private final VarInput<InventoryAlert.InventoryAlertType> inventoryAlertType =
        new VarInput<>(this, "Alert Type", InventoryAlert.InventoryAlertType.class, InventoryAlert.InventoryAlertType.FULL);
    private final VarInput<InventoryAlert.InventoryMatchType> inventoryMatchType =
        new VarInput<>(this, "Match Type", InventoryAlert.InventoryMatchType.class, InventoryAlert.InventoryMatchType.BOTH);
    private final VarInput<String> itemName = new VarInput<>(this, "Item Name", String.class, "");
    private final VarInput<Boolean> regexEnabled = new VarInput<>(this, "Regex", Boolean.class, false);
    private final VarInput<Number> itemQuantity = new VarInput<>(this, "Quantity", Number.class, 1);
    private final VarInput<ComparableNumber.Comparator> quantityComparator =
        new VarInput<>(this, "Comparator", ComparableNumber.Comparator.class, ComparableNumber.Comparator.GREATER_THAN_OR_EQUALS);
    private final VarOutput<Boolean> resultOut = new VarOutput<>(this, "Result", Boolean.class, false);

    public InventoryCheck() {
        this.inventory.onChange(val -> this.process());

        reg(this.inventory);
        reg(this.inventoryAlertType);
        reg(this.inventoryMatchType);
        reg(this.itemName);
        reg(this.regexEnabled);
        reg(this.itemQuantity);
        reg(this.quantityComparator);
        reg(this.resultOut);
    }

    @Override
    public String getPattern() {
        return this.itemName.getValue();
    }

    @Override
    public void setPattern(String pattern) {
        this.itemName.setValue(pattern);
    }

    @Override
    public boolean isRegexEnabled() {
        return this.regexEnabled.getValue();
    }

    @Override
    public void setRegexEnabled(boolean v) {
        this.regexEnabled.setValue(v);
    }

    @Override
    public void process() {
        InventoryItemData.InventoryItemDataMap inv = this.inventory.getValue();
        long itemCount = inv.getItemCount();
        Map<Integer, InventoryItemData> itemMap = inv.getItems();
        InventoryAlert.InventoryAlertType alertType = this.inventoryAlertType.getValue();

        boolean result;
        switch (alertType) {
            case FULL:
                result = itemCount == 28;
                break;
            case EMPTY:
                result = itemCount == 0;
                break;
            case SLOTS:
                result = this.quantityComparator.getValue().compare(
                    (int) itemCount, this.itemQuantity.getValue().intValue());
                break;
            case ITEM:
            case ITEM_CHANGE: {
                InventoryAlert.InventoryMatchType matchType = this.inventoryMatchType.getValue();
                int qty = this.itemQuantity.getValue().intValue();
                ComparableNumber.Comparator cmp = this.quantityComparator.getValue();
                result = itemMap.entrySet().stream()
                    .filter(e -> matchType == InventoryAlert.InventoryMatchType.BOTH
                        || (matchType == InventoryAlert.InventoryMatchType.NOTED && e.getValue().isNoted())
                        || (matchType == InventoryAlert.InventoryMatchType.UN_NOTED && !e.getValue().isNoted()))
                    .anyMatch(e -> {
                        String[] groups = Util.matchPattern(this, e.getValue().getItemComposition().getName());
                        if (groups == null) return false;
                        return cmp.compare(e.getValue().getQuantity(), qty);
                    });
                break;
            }
            default:
                result = false;
        }
        this.resultOut.setValue(result);
    }
}
