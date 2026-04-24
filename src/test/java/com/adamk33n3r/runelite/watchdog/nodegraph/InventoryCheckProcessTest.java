package com.adamk33n3r.runelite.watchdog.nodegraph;

import com.adamk33n3r.nodegraph.nodes.logic.InventoryCheck;
import com.adamk33n3r.runelite.watchdog.InventoryItemData;
import com.adamk33n3r.runelite.watchdog.alerts.InventoryAlert;
import com.adamk33n3r.runelite.watchdog.ui.ComparableNumber;

import net.runelite.api.ItemComposition;

import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.*;

public class InventoryCheckProcessTest {

    private InventoryItemData.InventoryItemDataMap makeMap(int itemCount) {
        return new InventoryItemData.InventoryItemDataMap(itemCount);
    }

    private InventoryItemData.InventoryItemDataMap makeMapWithItem(int id, String name, int quantity, boolean noted) {
        InventoryItemData.InventoryItemDataMap map = new InventoryItemData.InventoryItemDataMap(1);
        ItemComposition comp = Mockito.mock(ItemComposition.class);
        Mockito.when(comp.getName()).thenReturn(name);
        Mockito.when(comp.getNote()).thenReturn(noted ? 1 : -1);
        InventoryItemData item = InventoryItemData.builder()
            .itemComposition(comp)
            .quantity(quantity)
            .build();
        map.getItems().put(id, item);
        return map;
    }

    // --- FULL ---

    @Test
    public void full_28Items_isTrue() {
        InventoryCheck ic = new InventoryCheck();
        ic.getInventoryAlertType().setValue(InventoryAlert.InventoryAlertType.FULL);
        ic.getInventory().setValue(makeMap(28));
        ic.process();
        assertTrue(ic.getResultOut().getValue());
    }

    @Test
    public void full_27Items_isFalse() {
        InventoryCheck ic = new InventoryCheck();
        ic.getInventoryAlertType().setValue(InventoryAlert.InventoryAlertType.FULL);
        ic.getInventory().setValue(makeMap(27));
        ic.process();
        assertFalse(ic.getResultOut().getValue());
    }

    // --- EMPTY ---

    @Test
    public void empty_0Items_isTrue() {
        InventoryCheck ic = new InventoryCheck();
        ic.getInventoryAlertType().setValue(InventoryAlert.InventoryAlertType.EMPTY);
        ic.getInventory().setValue(makeMap(0));
        ic.process();
        assertTrue(ic.getResultOut().getValue());
    }

    @Test
    public void empty_1Item_isFalse() {
        InventoryCheck ic = new InventoryCheck();
        ic.getInventoryAlertType().setValue(InventoryAlert.InventoryAlertType.EMPTY);
        ic.getInventory().setValue(makeMap(1));
        ic.process();
        assertFalse(ic.getResultOut().getValue());
    }

    // --- SLOTS ---

    @Test
    public void slots_greaterThanOrEquals_meetsThreshold_isTrue() {
        InventoryCheck ic = new InventoryCheck();
        ic.getInventoryAlertType().setValue(InventoryAlert.InventoryAlertType.SLOTS);
        ic.getItemQuantity().setValue(5);
        ic.getQuantityComparator().setValue(ComparableNumber.Comparator.GREATER_THAN_OR_EQUALS);
        ic.getInventory().setValue(makeMap(10));
        ic.process();
        assertTrue(ic.getResultOut().getValue());
    }

    @Test
    public void slots_greaterThan_belowThreshold_isFalse() {
        InventoryCheck ic = new InventoryCheck();
        ic.getInventoryAlertType().setValue(InventoryAlert.InventoryAlertType.SLOTS);
        ic.getItemQuantity().setValue(10);
        ic.getQuantityComparator().setValue(ComparableNumber.Comparator.GREATER_THAN);
        ic.getInventory().setValue(makeMap(5));
        ic.process();
        assertFalse(ic.getResultOut().getValue());
    }

    @Test
    public void slots_lessThan_belowThreshold_isTrue() {
        InventoryCheck ic = new InventoryCheck();
        ic.getInventoryAlertType().setValue(InventoryAlert.InventoryAlertType.SLOTS);
        ic.getItemQuantity().setValue(10);
        ic.getQuantityComparator().setValue(ComparableNumber.Comparator.LESS_THAN);
        ic.getInventory().setValue(makeMap(5));
        ic.process();
        assertTrue(ic.getResultOut().getValue());
    }

    @Test
    public void slots_lessThanOrEquals_atThreshold_isTrue() {
        InventoryCheck ic = new InventoryCheck();
        ic.getInventoryAlertType().setValue(InventoryAlert.InventoryAlertType.SLOTS);
        ic.getItemQuantity().setValue(5);
        ic.getQuantityComparator().setValue(ComparableNumber.Comparator.LESS_THAN_OR_EQUALS);
        ic.getInventory().setValue(makeMap(5));
        ic.process();
        assertTrue(ic.getResultOut().getValue());
    }

    @Test
    public void slots_equals_exact_isTrue() {
        InventoryCheck ic = new InventoryCheck();
        ic.getInventoryAlertType().setValue(InventoryAlert.InventoryAlertType.SLOTS);
        ic.getItemQuantity().setValue(7);
        ic.getQuantityComparator().setValue(ComparableNumber.Comparator.EQUALS);
        ic.getInventory().setValue(makeMap(7));
        ic.process();
        assertTrue(ic.getResultOut().getValue());
    }

    @Test
    public void slots_notEquals_differentCount_isTrue() {
        InventoryCheck ic = new InventoryCheck();
        ic.getInventoryAlertType().setValue(InventoryAlert.InventoryAlertType.SLOTS);
        ic.getItemQuantity().setValue(7);
        ic.getQuantityComparator().setValue(ComparableNumber.Comparator.NOT_EQUALS);
        ic.getInventory().setValue(makeMap(3));
        ic.process();
        assertTrue(ic.getResultOut().getValue());
    }

    // --- ITEM (glob match, BOTH matchType) ---

    @Test
    public void item_globMatch_found_isTrue() {
        InventoryCheck ic = new InventoryCheck();
        ic.getInventoryAlertType().setValue(InventoryAlert.InventoryAlertType.ITEM);
        ic.getInventoryMatchType().setValue(InventoryAlert.InventoryMatchType.BOTH);
        ic.getItemName().setValue("Shark*");
        ic.getItemQuantity().setValue(1);
        ic.getQuantityComparator().setValue(ComparableNumber.Comparator.GREATER_THAN_OR_EQUALS);
        ic.getInventory().setValue(makeMapWithItem(1, "Shark", 3, false));
        ic.process();
        assertTrue(ic.getResultOut().getValue());
    }

    @Test
    public void item_globMatch_notFound_isFalse() {
        InventoryCheck ic = new InventoryCheck();
        ic.getInventoryAlertType().setValue(InventoryAlert.InventoryAlertType.ITEM);
        ic.getInventoryMatchType().setValue(InventoryAlert.InventoryMatchType.BOTH);
        ic.getItemName().setValue("Sword*");
        ic.getItemQuantity().setValue(1);
        ic.getQuantityComparator().setValue(ComparableNumber.Comparator.GREATER_THAN_OR_EQUALS);
        ic.getInventory().setValue(makeMapWithItem(1, "Shark", 3, false));
        ic.process();
        assertFalse(ic.getResultOut().getValue());
    }

    @Test
    public void item_regexMatch_found_isTrue() {
        InventoryCheck ic = new InventoryCheck();
        ic.getInventoryAlertType().setValue(InventoryAlert.InventoryAlertType.ITEM);
        ic.getInventoryMatchType().setValue(InventoryAlert.InventoryMatchType.BOTH);
        ic.setRegexEnabled(true);
        ic.getItemName().setValue("Sh.*k");
        ic.getItemQuantity().setValue(1);
        ic.getQuantityComparator().setValue(ComparableNumber.Comparator.GREATER_THAN_OR_EQUALS);
        ic.getInventory().setValue(makeMapWithItem(1, "Shark", 2, false));
        ic.process();
        assertTrue(ic.getResultOut().getValue());
    }

    @Test
    public void item_quantityBelowThreshold_isFalse() {
        InventoryCheck ic = new InventoryCheck();
        ic.getInventoryAlertType().setValue(InventoryAlert.InventoryAlertType.ITEM);
        ic.getInventoryMatchType().setValue(InventoryAlert.InventoryMatchType.BOTH);
        ic.getItemName().setValue("Shark");
        ic.getItemQuantity().setValue(10);
        ic.getQuantityComparator().setValue(ComparableNumber.Comparator.GREATER_THAN_OR_EQUALS);
        ic.getInventory().setValue(makeMapWithItem(1, "Shark", 3, false));
        ic.process();
        assertFalse(ic.getResultOut().getValue());
    }

    // --- ITEM matchType filtering ---

    @Test
    public void item_notedFilter_noted_matches() {
        InventoryCheck ic = new InventoryCheck();
        ic.getInventoryAlertType().setValue(InventoryAlert.InventoryAlertType.ITEM);
        ic.getInventoryMatchType().setValue(InventoryAlert.InventoryMatchType.NOTED);
        ic.getItemName().setValue("Shark");
        ic.getItemQuantity().setValue(1);
        ic.getQuantityComparator().setValue(ComparableNumber.Comparator.GREATER_THAN_OR_EQUALS);
        ic.getInventory().setValue(makeMapWithItem(1, "Shark", 5, true));
        ic.process();
        assertTrue(ic.getResultOut().getValue());
    }

    @Test
    public void item_notedFilter_unNoted_doesNotMatch() {
        InventoryCheck ic = new InventoryCheck();
        ic.getInventoryAlertType().setValue(InventoryAlert.InventoryAlertType.ITEM);
        ic.getInventoryMatchType().setValue(InventoryAlert.InventoryMatchType.NOTED);
        ic.getItemName().setValue("Shark");
        ic.getItemQuantity().setValue(1);
        ic.getQuantityComparator().setValue(ComparableNumber.Comparator.GREATER_THAN_OR_EQUALS);
        ic.getInventory().setValue(makeMapWithItem(1, "Shark", 5, false));
        ic.process();
        assertFalse(ic.getResultOut().getValue());
    }

    @Test
    public void item_unNotedFilter_unNoted_matches() {
        InventoryCheck ic = new InventoryCheck();
        ic.getInventoryAlertType().setValue(InventoryAlert.InventoryAlertType.ITEM);
        ic.getInventoryMatchType().setValue(InventoryAlert.InventoryMatchType.UN_NOTED);
        ic.getItemName().setValue("Shark");
        ic.getItemQuantity().setValue(1);
        ic.getQuantityComparator().setValue(ComparableNumber.Comparator.GREATER_THAN_OR_EQUALS);
        ic.getInventory().setValue(makeMapWithItem(1, "Shark", 5, false));
        ic.process();
        assertTrue(ic.getResultOut().getValue());
    }

    @Test
    public void item_unNotedFilter_noted_doesNotMatch() {
        InventoryCheck ic = new InventoryCheck();
        ic.getInventoryAlertType().setValue(InventoryAlert.InventoryAlertType.ITEM);
        ic.getInventoryMatchType().setValue(InventoryAlert.InventoryMatchType.UN_NOTED);
        ic.getItemName().setValue("Shark");
        ic.getItemQuantity().setValue(1);
        ic.getQuantityComparator().setValue(ComparableNumber.Comparator.GREATER_THAN_OR_EQUALS);
        ic.getInventory().setValue(makeMapWithItem(1, "Shark", 5, true));
        ic.process();
        assertFalse(ic.getResultOut().getValue());
    }

    // --- ITEM_CHANGE behaves same as ITEM for continuous state ---

    @Test
    public void itemChange_sameAsItem_found_isTrue() {
        InventoryCheck ic = new InventoryCheck();
        ic.getInventoryAlertType().setValue(InventoryAlert.InventoryAlertType.ITEM_CHANGE);
        ic.getInventoryMatchType().setValue(InventoryAlert.InventoryMatchType.BOTH);
        ic.getItemName().setValue("Shark");
        ic.getItemQuantity().setValue(1);
        ic.getQuantityComparator().setValue(ComparableNumber.Comparator.GREATER_THAN_OR_EQUALS);
        ic.getInventory().setValue(makeMapWithItem(1, "Shark", 2, false));
        ic.process();
        assertTrue(ic.getResultOut().getValue());
    }
}
