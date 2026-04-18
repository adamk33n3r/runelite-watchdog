package com.adamk33n3r.runelite.watchdog.nodegraph;

import com.adamk33n3r.runelite.watchdog.InventoryItemData;

import net.runelite.api.ItemComposition;

import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.*;

public class InventoryItemDataMapTest {

    private static InventoryItemData itemData(String name) {
        ItemComposition comp = Mockito.mock(ItemComposition.class);
        Mockito.when(comp.getName()).thenReturn(name);
        return InventoryItemData.builder().itemComposition(comp).quantity(1).build();
    }

    // ── constructors ──────────────────────────────────────────────────────────

    @Test
    public void convenience_constructor_creates_empty_slots_array() {
        var map = new InventoryItemData.InventoryItemDataMap(3);
        assertNotNull(map.getSlots());
        assertEquals(0, map.getSlots().length);
        assertEquals(3, map.getItemCount());
    }

    @Test
    public void full_constructor_preserves_slots_array() {
        InventoryItemData[] slots = new InventoryItemData[4];
        slots[0] = itemData("Shark");
        slots[2] = itemData("Cake");

        var map = new InventoryItemData.InventoryItemDataMap(2, slots);

        assertSame(slots, map.getSlots());
        assertEquals("Shark", map.getSlots()[0].getItemComposition().getName());
        assertNull(map.getSlots()[1]);
        assertEquals("Cake", map.getSlots()[2].getItemComposition().getName());
    }

    // ── copy constructor ──────────────────────────────────────────────────────

    @Test
    public void copy_constructor_clones_slots_array_not_alias() {
        InventoryItemData[] slots = new InventoryItemData[2];
        slots[0] = itemData("Shark");
        var original = new InventoryItemData.InventoryItemDataMap(1, slots);

        var copy = new InventoryItemData.InventoryItemDataMap(original);

        assertNotSame(original.getSlots(), copy.getSlots());
        assertEquals(original.getSlots().length, copy.getSlots().length);
        assertSame(original.getSlots()[0], copy.getSlots()[0]);
    }

    @Test
    public void copy_constructor_does_not_carry_over_mutation() {
        InventoryItemData[] slots = new InventoryItemData[2];
        slots[0] = itemData("Shark");
        var original = new InventoryItemData.InventoryItemDataMap(1, slots);
        var copy = new InventoryItemData.InventoryItemDataMap(original);

        // Mutating the copy's array should not affect the original
        copy.getSlots()[0] = itemData("Cake");

        assertEquals("Shark", original.getSlots()[0].getItemComposition().getName());
    }

    @Test
    public void copy_constructor_preserves_items_map() {
        InventoryItemData item = itemData("Coins");
        var original = new InventoryItemData.InventoryItemDataMap(1, new InventoryItemData[0]);
        original.getItems().put(995, item);

        var copy = new InventoryItemData.InventoryItemDataMap(original);

        assertTrue(copy.getItems().containsKey(995));
        assertSame(item, copy.getItems().get(995));
    }
}
