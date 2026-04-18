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
        /** Slot-indexed array matching the in-game inventory layout; null entries are empty slots. */
        private final InventoryItemData[] slots;
        private final Map<Integer, InventoryItemData> items = new HashMap<>();

        /** Convenience constructor for callers that don't have slot data (e.g. empty-inventory sentinels). */
        public InventoryItemDataMap(long itemCount) {
            this(itemCount, new InventoryItemData[0]);
        }

        public InventoryItemDataMap(InventoryItemDataMap other) {
            this.itemCount = other.itemCount;
            this.slots = other.slots.clone();
            this.items.putAll(other.items);
        }
    }
}
