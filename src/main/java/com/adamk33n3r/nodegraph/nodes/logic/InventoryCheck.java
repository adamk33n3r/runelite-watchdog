package com.adamk33n3r.nodegraph.nodes.logic;

import com.adamk33n3r.nodegraph.Node;
import com.adamk33n3r.nodegraph.VarInput;
import com.adamk33n3r.nodegraph.VarOutput;
import com.adamk33n3r.nodegraph.nodes.constants.VariableNode;
import com.adamk33n3r.runelite.watchdog.InventoryItemData;
import com.adamk33n3r.runelite.watchdog.Util;
import com.adamk33n3r.runelite.watchdog.alerts.InventoryAlert;
import com.adamk33n3r.runelite.watchdog.alerts.RegexMatcher;
import com.adamk33n3r.runelite.watchdog.ui.ComparableNumber;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class InventoryCheck extends Node implements RegexMatcher {
    private static final InventoryItemData.InventoryItemDataMap EMPTY_INVENTORY = new InventoryItemData.InventoryItemDataMap(0);
    private final VarInput<InventoryItemData.InventoryItemDataMap> inventory =
        new VarInput<>(this, "Inventory", InventoryItemData.InventoryItemDataMap.class, EMPTY_INVENTORY);
    private final VarOutput<Boolean> resultOut = new VarOutput<>(this, "Result", Boolean.class, false);

    private InventoryAlert.InventoryAlertType inventoryAlertType = InventoryAlert.InventoryAlertType.FULL;
    private InventoryAlert.InventoryMatchType inventoryMatchType = InventoryAlert.InventoryMatchType.BOTH;
    private String itemName = "";
    private boolean isRegexEnabled = false;
    private int itemQuantity = 1;
    private ComparableNumber.Comparator quantityComparator = ComparableNumber.Comparator.GREATER_THAN_OR_EQUALS;

    public InventoryCheck() {
        this.inventory.onChange(val -> this.process());

        reg(this.inventory);
        reg(this.resultOut);
    }

    @Override
    public String getPattern() {
        return this.itemName;
    }

    @Override
    public void setPattern(String pattern) {
        this.itemName = pattern;
    }

    @Override
    public void process() {
        var result = this.evaluateInventoryVar(this, this.inventory.getValue().getItemCount(), this.inventory.getValue().getItems());
        this.resultOut.setValue(result);
    }

    private boolean evaluateInventoryVar(InventoryCheck inv, long itemCount, Map<Integer, InventoryItemData> itemMap) {
        switch (inv.getInventoryAlertType()) {
            case FULL:
                return itemCount == 28;
            case EMPTY:
                return itemCount == 0;
            case SLOTS:
                return inv.getQuantityComparator().compare((int) itemCount, inv.getItemQuantity());
            case ITEM:
            case ITEM_CHANGE:
                // For continuous state, ITEM_CHANGE behaves the same as ITEM (no delta tracking)
                return itemMap.entrySet().stream()
                    .filter(e -> inv.getInventoryMatchType() == InventoryAlert.InventoryMatchType.BOTH
                        || (inv.getInventoryMatchType() == InventoryAlert.InventoryMatchType.NOTED && e.getValue().isNoted())
                        || (inv.getInventoryMatchType() == InventoryAlert.InventoryMatchType.UN_NOTED && !e.getValue().isNoted()))
                    .anyMatch(e -> {
                        String[] groups = Util.matchPattern(inv, e.getValue().getItemComposition().getName());
                        if (groups == null) return false;
                        return inv.getQuantityComparator().compare(e.getValue().getQuantity(), inv.getItemQuantity());
                    });
            default:
                return false;
        }
    }
}
