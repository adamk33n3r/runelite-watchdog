package com.adamk33n3r.runelite.watchdog;

import com.adamk33n3r.runelite.watchdog.alerts.AdvancedAlert;
import com.adamk33n3r.runelite.watchdog.alerts.InventoryAlert;
import com.adamk33n3r.runelite.watchdog.alerts.InventoryAlert.InventoryAlertType;
import com.adamk33n3r.runelite.watchdog.ui.ComparableNumber;

import net.runelite.api.Item;
import net.runelite.api.ItemComposition;
import net.runelite.api.ItemContainer;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.api.gameval.InventoryID;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;

@RunWith(MockitoJUnitRunner.class)
public class InventoryAlertEventHandlerTest extends AlertTestBase {

    @InjectMocks
    EventHandler eventHandler;

    @Before
    public void setup() {
        Mockito.doNothing().when(this.watchdogPlugin).processAlert(any(), any(), anyBoolean());
    }

    // region Helpers

    /** Creates an ItemContainerChanged event for the player inventory with the given items. */
    private ItemContainerChanged inventoryEvent(Item... items) {
        ItemContainer container = Mockito.mock(ItemContainer.class);
        Mockito.when(container.getId()).thenReturn(InventoryID.INV);
        Mockito.when(container.getItems()).thenReturn(items);
        return new ItemContainerChanged(InventoryID.INV, container);
    }

    /** Creates an Item and stubs itemManager to return a named ItemComposition for it. */
    private Item item(int id, int quantity) {
        ItemComposition comp = Mockito.mock(ItemComposition.class);
        Mockito.when(comp.getName()).thenReturn("Item_" + id);
        Mockito.when(this.itemManager.getItemComposition(id)).thenReturn(comp);
        return new Item(id, quantity);
    }

    /** Wires alertManager to return the given InventoryAlert and an empty AdvancedAlert stream. */
    private void mockAlertManager(InventoryAlert alert) {
        Mockito.doAnswer(inv -> {
            Class<?> clazz = inv.getArgument(0);
            if (clazz == InventoryAlert.class) return Stream.of(alert);
            if (clazz == AdvancedAlert.class) return Stream.empty();
            return Stream.empty();
        }).when(this.alertManager).getAllEnabledAlertsOfType(any());
    }

    private InventoryAlert fullAlert() {
        InventoryAlert alert = new InventoryAlert("Full");
        alert.setInventoryAlertType(InventoryAlertType.FULL);
        this.mockAlertManager(alert);
        return alert;
    }

    private InventoryAlert itemCountAlert(int quantity) {
        InventoryAlert alert = new InventoryAlert("Item Count");
        alert.setInventoryAlertType(InventoryAlertType.ITEM);
        alert.setItemName("*");
        alert.setItemQuantity(quantity);
        alert.setQuantityComparator(ComparableNumber.Comparator.EQUALS);
        this.mockAlertManager(alert);
        return alert;
    }

    // endregion

    @Test
    public void fullAlert_fromEmptyInventory_fires() {
        InventoryAlert alert = this.fullAlert();
        Item[] fullInventory = new Item[28];
        for (int i = 0; i < 28; i++) {
            fullInventory[i] = this.item(i + 1, 1);
        }

        this.eventHandler.onItemContainerChanged(this.inventoryEvent()); // tick 1: empty → initializes
        this.eventHandler.onItemContainerChanged(this.inventoryEvent(fullInventory)); // tick 2: full → should fire

        Mockito.verify(this.watchdogPlugin).processAlert(Mockito.eq(alert), any(), Mockito.eq(false));
    }

    @Test
    public void fullAlert_inventoryNotFull_doesNotFire() {
        this.fullAlert();
        Item[] halfInventory = new Item[14];
        for (int i = 0; i < 14; i++) {
            halfInventory[i] = this.item(i + 1, 1);
        }

        this.eventHandler.onItemContainerChanged(this.inventoryEvent());
        this.eventHandler.onItemContainerChanged(this.inventoryEvent(halfInventory));

        Mockito.verify(this.watchdogPlugin, Mockito.never()).processAlert(any(), any(), anyBoolean());
    }

    @Test
    public void itemCountAlert_fromEmptyInventory_fires() {
        InventoryAlert alert = this.itemCountAlert(14);
        Item coinsStack = this.item(995, 14);

        this.eventHandler.onItemContainerChanged(this.inventoryEvent()); // tick 1: empty → initializes
        this.eventHandler.onItemContainerChanged(this.inventoryEvent(coinsStack)); // tick 2: 14 coins → should fire

        Mockito.verify(this.watchdogPlugin).processAlert(Mockito.eq(alert), any(), Mockito.eq(false));
    }

    @Test
    public void itemCountAlert_itemAlreadyPresent_fires() {
        InventoryAlert alert = this.itemCountAlert(14);

        this.eventHandler.onItemContainerChanged(this.inventoryEvent(this.item(995, 1))); // tick 1: 1 coin
        this.eventHandler.onItemContainerChanged(this.inventoryEvent(this.item(995, 14))); // tick 2: 14 coins → should fire

        Mockito.verify(this.watchdogPlugin).processAlert(Mockito.eq(alert), any(), Mockito.eq(false));
    }

    @Test
    public void noAlerts_onFirstInventoryEvent() {
        this.fullAlert();
        Item[] fullInventory = new Item[28];
        for (int i = 0; i < 28; i++) {
            fullInventory[i] = this.item(i + 1, 1);
        }

        this.eventHandler.onItemContainerChanged(this.inventoryEvent(fullInventory)); // first tick: must be skipped

        Mockito.verify(this.watchdogPlugin, Mockito.never()).processAlert(any(), any(), anyBoolean());
    }
}
