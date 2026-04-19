package com.adamk33n3r.nodegraph.nodes.utility;

import com.adamk33n3r.nodegraph.VarInput;
import com.adamk33n3r.nodegraph.nodes.NoProcessNode;
import com.adamk33n3r.runelite.watchdog.InventoryItemData;
import lombok.Getter;

@Getter
public class DisplayNode extends NoProcessNode {
    private final VarInput<Object> value = new VarInput<>(this, "Value", Object.class, "");

    public DisplayNode() {
        reg(this.value);
    }

    /** Returns the human-readable string for the current value. */
    public String getStringRepresentation() {
        Object v = this.value.getValue();
        if (v == null) return "null";
        if (v.getClass().isArray()) {
            int length = ((Object[]) v).length;
            return "[" + v.getClass().getComponentType().getSimpleName() + "...][" + length + "]";
        } else if (v instanceof InventoryItemData.InventoryItemDataMap) {
            InventoryItemData.InventoryItemDataMap invMap = (InventoryItemData.InventoryItemDataMap) v;
            int i = 0;
            StringBuilder sb = new StringBuilder();
            for (var item : invMap.getItems().entrySet()) {
                sb.append(item.getValue().getItemComposition().getName())
                  .append("(").append(item.getValue().getQuantity()).append("),");
                if (++i > 3) break;
            }
            if (invMap.getItems().size() > 3) sb.append("...");
            else if (!invMap.getItems().isEmpty()) sb.deleteCharAt(sb.length() - 1);
            return sb.toString();
        } else if (v instanceof Boolean) {
            return (Boolean) v ? "✔" : "✖";
        } else {
            return v.toString();
        }
    }
}
