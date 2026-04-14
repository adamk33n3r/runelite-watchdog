package com.adamk33n3r.runelite.watchdog.ui.nodegraph.inputs;

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
        this.onChangeListeners.forEach(onChange -> onChange.accept(value));
    }

    @Override
    public void registerOnChange(Consumer<T> onChange) {
        this.onChangeListeners.add(onChange);
    }

    private String getStringRepresentation() {
        if (this.value.getClass().isArray()) {
            int length = ((Object[]) this.value).length;
            return "[" + this.value.getClass().getComponentType().getSimpleName() + "...][" + length + "]";
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
