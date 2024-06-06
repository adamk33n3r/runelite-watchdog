package com.adamk33n3r.runelite.watchdog.ui.nodegraph.inputs;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

public class ViewInput<T> extends AbstractInput<T> {
    private final JLabel valueLabel;
    private T value;

    public ViewInput(String label, T value) {
        JLabel labelComp = new JLabel(label);
        this.value = value;
        this.valueLabel = new JLabel(this.getStringRepresentation());
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
        this.valueLabel.setText(this.getStringRepresentation());
    }

    @Override
    public void onChange(Consumer<T> onChange) {
        // Do nothing since this input is not editable
    }

    private String getStringRepresentation() {
        if (this.value.getClass().isArray()) {
            int length = ((Object[]) this.value).length;
            return  "[" + this.value.getClass().getComponentType().getSimpleName() + "...][" + length + "]";
        } else {
            return this.value.toString();
        }
    }

    @Override
    protected JComponent getValueComponent() {
        return this.valueLabel;
    }
}
