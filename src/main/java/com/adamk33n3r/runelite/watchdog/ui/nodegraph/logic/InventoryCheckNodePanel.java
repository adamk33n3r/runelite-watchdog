package com.adamk33n3r.runelite.watchdog.ui.nodegraph.logic;

import com.adamk33n3r.nodegraph.nodes.logic.InventoryCheck;
import com.adamk33n3r.runelite.watchdog.InventoryItemData;
import com.adamk33n3r.runelite.watchdog.alerts.InventoryAlert;
import com.adamk33n3r.runelite.watchdog.ui.ComparableNumber;
import com.adamk33n3r.runelite.watchdog.ui.nodegraph.GraphPanel;
import com.adamk33n3r.runelite.watchdog.ui.nodegraph.NodePanel;
import com.adamk33n3r.runelite.watchdog.ui.nodegraph.connections.ConnectionLine;
import com.adamk33n3r.runelite.watchdog.ui.nodegraph.connections.ConnectionPointIn;
import com.adamk33n3r.runelite.watchdog.ui.nodegraph.connections.ConnectionPointOut;
import com.adamk33n3r.runelite.watchdog.ui.nodegraph.inputs.BoolInput;
import com.adamk33n3r.runelite.watchdog.ui.nodegraph.inputs.EnumInput;
import com.adamk33n3r.runelite.watchdog.ui.nodegraph.inputs.IntegerInput;
import com.adamk33n3r.runelite.watchdog.ui.nodegraph.inputs.TextInput;
import com.adamk33n3r.runelite.watchdog.ui.nodegraph.inputs.ViewInput;
import lombok.Getter;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

@Getter
public class InventoryCheckNodePanel extends NodePanel {
    private final ConnectionPointIn<InventoryItemData.InventoryItemDataMap> inventoryIn;
    private final ConnectionPointOut<Boolean> valueOut;
    private final InventoryCheck inventoryCheck;
    private final ViewInput<Boolean> matchView;
    // Index after which conditional controls are inserted (alertType row is at index 1)
    private int conditionalStartIndex;

    public InventoryCheckNodePanel(GraphPanel graphPanel, InventoryCheck node, int x, int y, String name, Color color) {
        super(graphPanel, node, x, y, name, color);
        this.inventoryCheck = node;

        // Inventory input row
        this.inventoryIn = new ConnectionPointIn<>(this, node.getInventory());
        ViewInput<InventoryItemData.InventoryItemDataMap> invView = new ViewInput<>("Inventory", node.getInventory().getValue());
        this.items.add(new ConnectionLine<>(this.inventoryIn, invView, null));

        // Alert Type — filtered to exclude ITEM_CHANGE (matches original UI)
        InventoryAlert.InventoryAlertType[] visibleTypes = Arrays.stream(InventoryAlert.InventoryAlertType.values())
            .filter(t -> t != InventoryAlert.InventoryAlertType.ITEM_CHANGE)
            .toArray(InventoryAlert.InventoryAlertType[]::new);
        ConnectionPointIn<InventoryAlert.InventoryAlertType> typeIn = new ConnectionPointIn<>(this, node.getInventoryAlertType());
        this.items.add(new ConnectionLine<>(typeIn, new EnumInput<>("Alert Type", node.getInventoryAlertType(), visibleTypes), null));

        // Record where conditional controls begin
        this.conditionalStartIndex = this.items.getComponentCount();
        this.addConditionalControls(node);

        // Result output row
        this.valueOut = new ConnectionPointOut<>(this, node.getResultOut());
        this.matchView = new ViewInput<>("Match", node.getResultOut().getValue());
        addDisposer(node.getInventory().onChange(v -> this.matchView.setValue(node.getResultOut().getValue())));
        this.items.add(new ConnectionLine<>(null, this.matchView, this.valueOut));

        // Rebuild conditional controls when alert type changes
        // Panel is constructed after graph load, so deserialization onChange has already fired.
        addDisposer(node.getInventoryAlertType().onChange(v -> {
            this.updateResult();
            SwingUtilities.invokeLater(this::rebuildContent);
        }));

        this.watchDirty(
            node.getInventoryAlertType(),
            node.getInventoryMatchType(),
            node.getItemName(),
            node.getRegexEnabled(),
            node.getItemQuantity(),
            node.getQuantityComparator()
        );

        this.pack();
    }

    private void addConditionalControls(InventoryCheck node) {
        InventoryAlert.InventoryAlertType alertType = node.getInventoryAlertType().getValue();

        if (alertType == InventoryAlert.InventoryAlertType.ITEM
            || alertType == InventoryAlert.InventoryAlertType.ITEM_CHANGE) {

            ConnectionPointIn<String> nameIn = new ConnectionPointIn<>(this, node.getItemName());
            this.items.add(new ConnectionLine<>(nameIn,
                new TextInput("Enter item name...", "Item name, supports glob (*)", node.getItemName()), null));
            addDisposer(node.getItemName().onChange(_v -> this.updateResult()));

            ConnectionPointIn<InventoryAlert.InventoryMatchType> matchIn = new ConnectionPointIn<>(this, node.getInventoryMatchType());
            this.items.add(new ConnectionLine<>(matchIn,
                new EnumInput<>("Match Type", node.getInventoryMatchType()), null));
            addDisposer(node.getInventoryMatchType().onChange(_v -> this.updateResult()));

            ConnectionPointIn<Boolean> regexIn = new ConnectionPointIn<>(this, node.getRegexEnabled());
            this.items.add(new ConnectionLine<>(regexIn,
                new BoolInput("Regex", node.getRegexEnabled()), null));
            addDisposer(node.getRegexEnabled().onChange(_v -> this.updateResult()));

            ConnectionPointIn<Number> qtyIn = new ConnectionPointIn<>(this, node.getItemQuantity());
            this.items.add(new ConnectionLine<>(qtyIn,
                new IntegerInput("Quantity", node.getItemQuantity()), null));
            addDisposer(node.getItemQuantity().onChange(_v -> this.updateResult()));

            ConnectionPointIn<ComparableNumber.Comparator> cmpIn = new ConnectionPointIn<>(this, node.getQuantityComparator());
            this.items.add(new ConnectionLine<>(cmpIn,
                new EnumInput<>("Comparator", node.getQuantityComparator()), null));
            addDisposer(node.getQuantityComparator().onChange(_v -> this.updateResult()));

        } else if (alertType == InventoryAlert.InventoryAlertType.SLOTS) {
            ConnectionPointIn<Number> qtyIn = new ConnectionPointIn<>(this, node.getItemQuantity());
            this.items.add(new ConnectionLine<>(qtyIn,
                new IntegerInput("Quantity", node.getItemQuantity()), null));
            addDisposer(node.getItemQuantity().onChange(_v -> this.updateResult()));

            ConnectionPointIn<ComparableNumber.Comparator> cmpIn = new ConnectionPointIn<>(this, node.getQuantityComparator());
            this.items.add(new ConnectionLine<>(cmpIn,
                new EnumInput<>("Comparator", node.getQuantityComparator()), null));
            addDisposer(node.getQuantityComparator().onChange(_v -> this.updateResult()));
        }
    }

    private void updateResult() {
        this.inventoryCheck.process();
        if (this.matchView != null) {
            this.matchView.setValue(this.inventoryCheck.getResultOut().getValue());
        }
        this.notifyChange();
    }

    private void rebuildContent() {
        // Remove all conditional controls and the result row (everything from conditionalStartIndex onward)
        while (this.items.getComponentCount() > this.conditionalStartIndex) {
            this.items.remove(this.conditionalStartIndex);
        }

        this.addConditionalControls(this.inventoryCheck);

        // Re-add result row
        ViewInput<Boolean> valueView = new ViewInput<>("Match", this.inventoryCheck.getResultOut().getValue());
        this.items.add(new ConnectionLine<>(null, valueView, this.valueOut));

        this.pack();
        this.revalidate();
        this.repaint();
    }
}
