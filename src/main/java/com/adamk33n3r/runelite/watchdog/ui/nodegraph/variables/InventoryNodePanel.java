package com.adamk33n3r.runelite.watchdog.ui.nodegraph.variables;

import com.adamk33n3r.nodegraph.nodes.constants.InventoryVar;
import com.adamk33n3r.runelite.watchdog.alerts.InventoryAlert;
import com.adamk33n3r.runelite.watchdog.ui.ComparableNumber;
import com.adamk33n3r.runelite.watchdog.ui.nodegraph.GraphPanel;
import com.adamk33n3r.runelite.watchdog.ui.nodegraph.NodePanel;
import com.adamk33n3r.runelite.watchdog.ui.nodegraph.connections.ConnectionLine;
import com.adamk33n3r.runelite.watchdog.ui.nodegraph.connections.ConnectionPointOut;
import com.adamk33n3r.runelite.watchdog.ui.nodegraph.inputs.TextInput;
import com.adamk33n3r.runelite.watchdog.ui.nodegraph.inputs.ViewInput;
import com.adamk33n3r.runelite.watchdog.ui.panels.PanelUtils;
import lombok.Getter;

import javax.swing.*;
import java.awt.*;

@Getter
public class InventoryNodePanel extends NodePanel {
    private final ConnectionPointOut<Boolean> valueOut;
    private final InventoryVar inventoryVar;

    public InventoryNodePanel(GraphPanel graphPanel, InventoryVar node, int x, int y, String name, Color color) {
        super(graphPanel, node, x, y, name, color);
        this.inventoryVar = node;

        TextInput nameInput = new TextInput("Name", node.getNameOut());
        nameInput.registerOnType(this::updateHeaderLabel);
        this.items.add(nameInput);

        JComboBox<InventoryAlert.InventoryMatchType> matchSelect = PanelUtils.createSelect(
            InventoryAlert.InventoryMatchType.values(), node.getInventoryMatchType(), selected -> {
                node.setInventoryMatchType(selected);
                this.notifyChange();
            });
        this.items.add(matchSelect);

        JComboBox<InventoryAlert.InventoryAlertType> typeSelect = PanelUtils.createSelect(
            InventoryAlert.InventoryAlertType.values(), node.getInventoryAlertType(), selected -> {
                node.setInventoryAlertType(selected);
                this.notifyChange();
                this.rebuildContent();
            });
        this.items.add(typeSelect);

        this.addConditionalControls(node);

        this.valueOut = new ConnectionPointOut<>(this, node.getValue());

        ViewInput<Boolean> valueView = new ViewInput<>("Match", node.getValue().getValue());
        this.items.add(new ConnectionLine<>(null, valueView, this.valueOut));

        this.pack();
    }

    private void addConditionalControls(InventoryVar node) {
        InventoryAlert.InventoryAlertType alertType = node.getInventoryAlertType();
        boolean isItemChange = alertType == InventoryAlert.InventoryAlertType.ITEM_CHANGE;

        if (alertType == InventoryAlert.InventoryAlertType.ITEM || isItemChange) {
            TextInput itemNameInput = new TextInput("Item Name", node.getItemName());
            itemNameInput.registerOnChange(val -> {
                node.setItemName(val);
                this.notifyChange();
            });
            this.items.add(itemNameInput);

            JCheckBox regexCheckbox = new JCheckBox("Regex", node.isRegexEnabled());
            regexCheckbox.addActionListener(e -> {
                node.setRegexEnabled(regexCheckbox.isSelected());
                this.notifyChange();
            });
            this.items.add(regexCheckbox);

            String quantityLabel = isItemChange ? "Change" : "Quantity";
            int min = isItemChange ? Integer.MIN_VALUE : 0;
            ComparableNumber comparableNumber = new ComparableNumber(
                node.getItemQuantity(), val -> { node.setItemQuantity(val); this.notifyChange(); },
                min, Integer.MAX_VALUE, 1,
                node.getQuantityComparator(), val -> { node.setQuantityComparator(val); this.notifyChange(); });
            JPanel quantityPanel = PanelUtils.createLabeledComponent(quantityLabel,
                isItemChange ? "Quantity change to trigger on" : "Item quantity to trigger on", comparableNumber);
            this.items.add(quantityPanel);
        } else if (alertType == InventoryAlert.InventoryAlertType.SLOTS) {
            ComparableNumber comparableNumber = new ComparableNumber(
                node.getItemQuantity(), val -> { node.setItemQuantity(val); this.notifyChange(); },
                0, Integer.MAX_VALUE, 1,
                node.getQuantityComparator(), val -> { node.setQuantityComparator(val); this.notifyChange(); });
            JPanel quantityPanel = PanelUtils.createLabeledComponent("Quantity", "Slot count to trigger on", comparableNumber);
            this.items.add(quantityPanel);
        }
    }

    private void rebuildContent() {
        // Remove everything after the type select (index 2) and before the output line
        // Items layout: [0]=nameInput, [1]=matchSelect, [2]=typeSelect, [3..n-1]=conditional, [n]=connectionLine
        while (this.items.getComponentCount() > 3) {
            this.items.remove(3);
        }
        this.addConditionalControls(this.inventoryVar);

        // Re-add the output line
        ViewInput<Boolean> valueView = new ViewInput<>("Match", this.inventoryVar.getValue().getValue());
        this.items.add(new ConnectionLine<>(null, valueView, this.valueOut));

        this.pack();
        this.revalidate();
        this.repaint();
    }
}
