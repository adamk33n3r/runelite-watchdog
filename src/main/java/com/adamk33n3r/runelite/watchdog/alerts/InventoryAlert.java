package com.adamk33n3r.runelite.watchdog.alerts;

import com.adamk33n3r.runelite.watchdog.Displayable;
import com.adamk33n3r.runelite.watchdog.ui.ComparableNumber;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class InventoryAlert extends Alert implements RegexMatcher {
    private InventoryAlertType inventoryAlertType = InventoryAlertType.FULL;
    private String itemName = "";
    @Accessors(chain = false)
    private boolean isRegexEnabled = false;
    private InventoryMatchType inventoryMatchType = InventoryMatchType.BOTH;
    private int itemQuantity = 1;
    private ComparableNumber.Comparator quantityComparator = ComparableNumber.Comparator.GREATER_THAN_OR_EQUALS;

    @Override
    public String getPattern() {
        return this.itemName;
    }

    @Override
    public void setPattern(String pattern) {
        this.itemName = pattern;
    }

    public InventoryAlert() {
        super("New Inventory Alert");
    }

    public InventoryAlert(String name) {
        super(name);
    }

    @Getter
    @AllArgsConstructor
    public enum InventoryAlertType implements Displayable {
        EMPTY("Empty", "Triggers on an empty inventory"),
        FULL("Full", "Triggers on a full inventory"),
        SLOTS("Slots", "Triggers when a number of slots are filled"),
        ITEM("Item Count", "Triggers when an item hits a certain count"),
        ITEM_CHANGE("Item Change", "Triggers when an item is added or removed")
        ;

        private final String name;
        private final String tooltip;
    }

    @Getter
    @AllArgsConstructor
    public enum InventoryMatchType implements Displayable {
        BOTH("Both", "Matches both noted and un-noted items"),
        NOTED("Noted", "Matches only noted items"),
        UN_NOTED("Un-noted", "Matches only un-noted items")
        ;

        private final String name;
        private final String tooltip;
    }
}
