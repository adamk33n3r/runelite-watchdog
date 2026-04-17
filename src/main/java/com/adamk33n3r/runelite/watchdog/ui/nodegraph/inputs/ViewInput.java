package com.adamk33n3r.runelite.watchdog.ui.nodegraph.inputs;

import com.adamk33n3r.runelite.watchdog.InventoryItemData;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ViewInput<T> extends AbstractInput<T> {
    private final JLabel valueLabel;
    private T value;
    private final List<Consumer<T>> onChangeListeners = new ArrayList<>();

    public ViewInput(String label, T value) {
        JLabel labelComp = new JLabel(label);
        this.value = value;
        String stringRepresentation = this.getStringRepresentation();
        this.valueLabel = new JLabel(stringRepresentation);
        this.valueLabel.setToolTipText(stringRepresentation);
        this.add(labelComp, BorderLayout.WEST);
        this.add(this.valueLabel);
    }

    @Override
    public T getValue() {
        return this.value;
    }

    @Override
    public void setValue(T value) {
        this.value = value;
        String stringRepresentation = this.getStringRepresentation();
        this.valueLabel.setText(stringRepresentation);
        this.valueLabel.setToolTipText(stringRepresentation);
        this.onChangeListeners.forEach(onChange -> onChange.accept(value));
    }

    @Override
    public void registerOnChange(Consumer<T> onChange) {
        this.onChangeListeners.add(onChange);
    }

    private String getStringRepresentation() {
        if (this.value == null) {
            return "";
        }

        if (this.value.getClass().isArray()) {
            int length = ((Object[]) this.value).length;
            return "[" + this.value.getClass().getComponentType().getSimpleName() + "...][" + length + "]";
        } else if (this.value instanceof InventoryItemData.InventoryItemDataMap) {
            var invMap = (InventoryItemData.InventoryItemDataMap) this.value;
            int i = 0;
            StringBuilder stringBuilder = new StringBuilder();
            for (var item : invMap.getItems().entrySet()) {
               stringBuilder
                   .append(item.getValue().getItemComposition().getName())
                   .append("(")
                   .append(item.getValue().getQuantity())
                   .append("),");
               i++;
               if (i > 3) {
                   break;
               }
            }

            if (invMap.getItems().size() > 3) {
                stringBuilder.append("...");
            } else if (!invMap.getItems().isEmpty()){
                stringBuilder.deleteCharAt(stringBuilder.length() - 1);
            }
            return stringBuilder.toString();
        } else if (this.value instanceof Boolean) {
            return (Boolean) this.value ? "✔️" : "✖️";
        } else {
            return this.value.toString();
        }
    }

    @Override
    protected JComponent getValueComponent() {
        return this.valueLabel;
    }
}
