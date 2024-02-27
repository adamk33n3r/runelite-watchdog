package com.adamk33n3r.runelite.watchdog.alerts;

import com.adamk33n3r.runelite.watchdog.Displayable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InventoryAlert extends Alert implements RegexMatcher {
    private InventoryAlertType inventoryAlertType = InventoryAlertType.FULL;
    private String itemName = "";
    private boolean isRegexEnabled;
    private int itemQuantity;

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
