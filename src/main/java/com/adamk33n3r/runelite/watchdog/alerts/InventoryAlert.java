package com.adamk33n3r.runelite.watchdog.alerts;

import com.adamk33n3r.runelite.watchdog.Displayable;
import com.adamk33n3r.runelite.watchdog.ui.ComparableNumber;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
public class InventoryAlert extends Alert implements RegexMatcher {
    @Builder.Default
    private InventoryAlertType inventoryAlertType = InventoryAlertType.FULL;
    @Builder.Default
    private String itemName = "";
    @Builder.Default
    private boolean isRegexEnabled = false;
    @Builder.Default
    private int itemQuantity = 1;
    @Builder.Default
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
        ITEM("Item Count", "Triggers when an item hits a certain count"),
        ITEM_CHANGE("Item Change", "Triggers when an item is added or removed")
        ;

        private final String name;
        private final String tooltip;
    }
}
