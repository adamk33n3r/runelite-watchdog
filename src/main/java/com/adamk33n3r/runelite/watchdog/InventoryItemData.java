package com.adamk33n3r.runelite.watchdog;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.runelite.api.ItemComposition;

import java.util.HashMap;
import java.util.Map;

@Getter
@Builder
public class InventoryItemData {
    private ItemComposition itemComposition;
    private int quantity;

    public boolean isNoted() {
        return itemComposition.getNote() != -1;
    }

    @Getter
    @RequiredArgsConstructor
    public static class InventoryItemDataMap {
        private final long itemCount;
        private final Map<Integer, InventoryItemData> items =  new HashMap<>();

        public InventoryItemDataMap(InventoryItemDataMap other) {
            this.itemCount = other.itemCount;
            this.items.putAll(other.items);
        }
    }
}
