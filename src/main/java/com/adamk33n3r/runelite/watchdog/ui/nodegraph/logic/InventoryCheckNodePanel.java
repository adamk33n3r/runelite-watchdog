package com.adamk33n3r.runelite.watchdog.ui.nodegraph.logic;

import com.adamk33n3r.nodegraph.nodes.logic.InventoryCheck;
import com.adamk33n3r.runelite.watchdog.InventoryItemData;
import com.adamk33n3r.runelite.watchdog.alerts.InventoryAlert;
import com.adamk33n3r.runelite.watchdog.ui.ComparableNumber;
import com.adamk33n3r.runelite.watchdog.ui.alerts.InventoryAlertPanel;
import com.adamk33n3r.runelite.watchdog.ui.nodegraph.GraphPanel;
import com.adamk33n3r.runelite.watchdog.ui.nodegraph.NodePanel;
import com.adamk33n3r.runelite.watchdog.ui.nodegraph.connections.ConnectionLine;
import com.adamk33n3r.runelite.watchdog.ui.nodegraph.connections.ConnectionPointIn;
import com.adamk33n3r.runelite.watchdog.ui.nodegraph.connections.ConnectionPointOut;
import com.adamk33n3r.runelite.watchdog.ui.nodegraph.inputs.TextInput;
import com.adamk33n3r.runelite.watchdog.ui.nodegraph.inputs.ViewInput;
import com.adamk33n3r.runelite.watchdog.ui.nodegraph.variables.VariableNodePanel;
import com.adamk33n3r.runelite.watchdog.ui.panels.PanelUtils;
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

    public InventoryCheckNodePanel(GraphPanel graphPanel, InventoryCheck node, int x, int y, String name, Color color) {
        super(graphPanel, node, x, y, name, color);
        this.inventoryCheck = node;

        this.inventoryIn = new ConnectionPointIn<>(this, node.getInventory());

        ViewInput<InventoryItemData.InventoryItemDataMap> invView = new ViewInput<>("Inventory", node.getInventory().getValue());
//        addDisposer(node.getInventory().onChange(invView::setValue));
        this.items.add(new ConnectionLine<>(this.inventoryIn, invView, null));

        JComboBox<InventoryAlert.InventoryAlertType> typeSelect = PanelUtils.createSelect(
            Arrays.stream(InventoryAlert.InventoryAlertType.values())
                .filter(iat -> iat != InventoryAlert.InventoryAlertType.ITEM_CHANGE)
                .toArray(InventoryAlert.InventoryAlertType[]::new),
            node.getInventoryAlertType(),
            selected -> {
                node.setInventoryAlertType(selected);
                this.updateResult();
                this.rebuildContent();
            });
        this.items.add(typeSelect);

        this.addConditionalControls(node);

        this.valueOut = new ConnectionPointOut<>(this, node.getResultOut());

        this.matchView = new ViewInput<>("Match", node.getResultOut().getValue());
        addDisposer(node.getInventory().onChange(v -> this.matchView.setValue(node.getResultOut().getValue())));
        this.items.add(new ConnectionLine<>(null, this.matchView, this.valueOut));

        this.pack();
    }

    private void addConditionalControls(InventoryCheck node) {
        InventoryAlert.InventoryAlertType alertType = node.getInventoryAlertType();
        boolean isItemChange = alertType == InventoryAlert.InventoryAlertType.ITEM_CHANGE;

        if (alertType == InventoryAlert.InventoryAlertType.ITEM || isItemChange) {
            TextInput itemNameInput = new TextInput(
                "Enter the name of the item to trigger on...",
                "The name to trigger on. Supports glob (*)",
                node.getItemName()
            );
            itemNameInput.registerOnChange(val -> {
                node.setItemName(val);
                this.updateResult();
            });
            this.items.add(itemNameInput);

            JComboBox<InventoryAlert.InventoryMatchType> matchSelect = PanelUtils.createSelect(
                InventoryAlert.InventoryMatchType.values(), node.getInventoryMatchType(), selected -> {
                    node.setInventoryMatchType(selected);
                    this.updateResult();
                });
            this.items.add(matchSelect);

            JCheckBox regexCheckbox = new JCheckBox("Regex", node.isRegexEnabled());
            regexCheckbox.addActionListener(e -> {
                node.setRegexEnabled(regexCheckbox.isSelected());
                this.updateResult();
            });
            this.items.add(regexCheckbox);

            String quantityLabel = isItemChange ? "Change" : "Quantity";
            int min = isItemChange ? Integer.MIN_VALUE : 0;
            ComparableNumber comparableNumber = new ComparableNumber(
                node.getItemQuantity(), val -> { node.setItemQuantity(val); this.updateResult(); },
                min, Integer.MAX_VALUE, 1,
                node.getQuantityComparator(), val -> { node.setQuantityComparator(val); this.updateResult(); });
            JPanel quantityPanel = PanelUtils.createLabeledComponent(quantityLabel,
                isItemChange ? "Quantity change to trigger on" : "Item quantity to trigger on", comparableNumber);
            this.items.add(quantityPanel);
        } else if (alertType == InventoryAlert.InventoryAlertType.SLOTS) {
            ComparableNumber comparableNumber = new ComparableNumber(
                node.getItemQuantity(), val -> { node.setItemQuantity(val); this.updateResult(); },
                0, Integer.MAX_VALUE, 1,
                node.getQuantityComparator(), val -> { node.setQuantityComparator(val); this.updateResult(); });
            JPanel quantityPanel = PanelUtils.createLabeledComponent("Quantity", "Slot count to trigger on", comparableNumber);
            this.items.add(quantityPanel);
        }
    }

    private void updateResult() {
        this.inventoryCheck.process();
        this.matchView.setValue(this.inventoryCheck.getResultOut().getValue());
        this.notifyChange();
    }

    private void rebuildContent() {
        // Remove everything after the type select (index 1) and before the output line
        // Items layout: [0]=matchSelect, [1]=typeSelect, [2..n-1]=conditional, [n]=connectionLine
        while (this.items.getComponentCount() > 2) {
            this.items.remove(2);
        }
        this.addConditionalControls(this.inventoryCheck);

        // Re-add the output line
        ViewInput<Boolean> valueView = new ViewInput<>("Match", this.inventoryCheck.getResultOut().getValue());
        this.items.add(new ConnectionLine<>(null, valueView, this.valueOut));

        this.pack();
        this.revalidate();
        this.repaint();
    }
}
