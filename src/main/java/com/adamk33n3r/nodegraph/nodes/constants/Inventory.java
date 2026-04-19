package com.adamk33n3r.nodegraph.nodes.constants;

import com.adamk33n3r.nodegraph.VarInput;
import com.adamk33n3r.nodegraph.VarOutput;
import com.adamk33n3r.runelite.watchdog.InventoryItemData;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Inventory extends VariableNode {
    private static final InventoryItemData.InventoryItemDataMap EMPTY_INVENTORY = new InventoryItemData.InventoryItemDataMap(0);
    private final VarInput<InventoryItemData.InventoryItemDataMap> value =
        new VarInput<>(this, "Value", InventoryItemData.InventoryItemDataMap.class, EMPTY_INVENTORY);
    private final VarOutput<InventoryItemData.InventoryItemDataMap> valueOut =
        new VarOutput<>(this, "Value", InventoryItemData.InventoryItemDataMap.class, EMPTY_INVENTORY);
    private final VarOutput<Number> countOut = new VarOutput<>(this, "Count", Number.class, 0);

    public Inventory() {
        this.value.onChange(val -> this.process());

        reg(this.value);
        reg(this.valueOut);
        reg(this.countOut);
    }

    public void setValue(InventoryItemData.InventoryItemDataMap value) {
        this.value.setValue(value);
    }

    @Override
    public void process() {
        this.valueOut.setValue(this.value.getValue());
        this.countOut.setValue(this.value.getValue().getItemCount());
    }
}
