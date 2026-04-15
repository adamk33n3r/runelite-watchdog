package com.adamk33n3r.runelite.watchdog.ui.nodegraph.variables;

import com.adamk33n3r.nodegraph.nodes.constants.Inventory;
import com.adamk33n3r.runelite.watchdog.InventoryItemData;
import com.adamk33n3r.runelite.watchdog.ui.nodegraph.GraphPanel;
import com.adamk33n3r.runelite.watchdog.ui.nodegraph.connections.ConnectionLine;
import com.adamk33n3r.runelite.watchdog.ui.nodegraph.connections.ConnectionPointOut;
import com.adamk33n3r.runelite.watchdog.ui.nodegraph.inputs.ViewInput;
import lombok.Getter;

import java.awt.*;

@Getter
public class InventoryVariableNodePanel extends VariableNodePanel {
    private final ConnectionPointOut<InventoryItemData.InventoryItemDataMap> valueOut;
    private final ConnectionPointOut<Number> countOut;
    private final Inventory inventoryVar;

    public InventoryVariableNodePanel(GraphPanel graphPanel, Inventory node, int x, int y, String name, Color color) {
        super(graphPanel, node, x, y, name, color, false);
        this.inventoryVar = node;

        this.valueOut = new ConnectionPointOut<>(this, node.getValueOut());
        this.countOut = new ConnectionPointOut<>(this, node.getCountOut());

        ViewInput<InventoryItemData.InventoryItemDataMap> invView = new ViewInput<>("Inventory", node.getValueOut().getValue());
        addDisposer(node.getValue().onChange(inv -> invView.setValue(node.getValue().getValue())));
        this.items.add(new ConnectionLine<>(null, invView, this.valueOut));

        ViewInput<Number> countView = new ViewInput<>("Item Count", node.getCountOut().getValue());
        addDisposer(node.getValue().onChange(inv -> countView.setValue(node.getValue().getValue().getItemCount())));
        this.items.add(new ConnectionLine<>(null, countView, this.countOut));

        this.pack();
    }
}
