package com.adamk33n3r.nodegraph.nodes.constants;

import com.adamk33n3r.nodegraph.VarInput;
import com.adamk33n3r.nodegraph.VarOutput;
import com.adamk33n3r.nodegraph.nodes.VariableNode;
import com.adamk33n3r.runelite.watchdog.alerts.InventoryAlert;
import com.adamk33n3r.runelite.watchdog.alerts.RegexMatcher;
import com.adamk33n3r.runelite.watchdog.ui.ComparableNumber;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class InventoryVar extends VariableNode implements RegexMatcher {
    private final VarInput<Boolean> value = new VarInput<>(this, "Value", Boolean.class, false);
    private final VarOutput<Boolean> valueOut = new VarOutput<>(this, "Value", Boolean.class, false);

    private InventoryAlert.InventoryAlertType inventoryAlertType = InventoryAlert.InventoryAlertType.FULL;
    private InventoryAlert.InventoryMatchType inventoryMatchType = InventoryAlert.InventoryMatchType.BOTH;
    private String itemName = "";
    @Accessors(chain = false)
    private boolean isRegexEnabled = false;
    private int itemQuantity = 1;
    private ComparableNumber.Comparator quantityComparator = ComparableNumber.Comparator.GREATER_THAN_OR_EQUALS;

    public InventoryVar() {
        this.value.onChange(val -> this.process());

        reg(this.value);
        reg(this.valueOut);
    }

    public void setValue(boolean value) {
        this.value.setValue(value);
    }

    @Override
    public String getPattern() {
        return this.itemName;
    }

    @Override
    public void setPattern(String pattern) {
        this.itemName = pattern;
    }

    @Override
    public void process() {
        this.valueOut.setValue(this.value.getValue());
    }
}
