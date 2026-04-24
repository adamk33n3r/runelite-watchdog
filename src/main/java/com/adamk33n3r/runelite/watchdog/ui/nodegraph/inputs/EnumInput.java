package com.adamk33n3r.runelite.watchdog.ui.nodegraph.inputs;

import com.adamk33n3r.nodegraph.Var;
import com.adamk33n3r.runelite.watchdog.ui.panels.PanelUtils;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class EnumInput<E extends Enum<E>> extends AbstractInput<E> {
    private final JComboBox<E> combo;
    private boolean suppressEvents = false;
    private final List<Consumer<E>> listeners = new ArrayList<>();

    @SuppressWarnings("unchecked")
    public EnumInput(String label, Var<E> value) {
        this(label, value, (E[]) value.getType().getEnumConstants());
    }

    public EnumInput(String label, Var<E> value, E[] values) {
        JLabel labelComp = new JLabel(label);
        this.combo = PanelUtils.createSelect(values, value.getValue(), selected -> {
            if (!this.suppressEvents) {
                this.listeners.forEach(c -> c.accept(selected));
            }
        });
        this.registerOnChange(value::setValue);
        this.add(labelComp, BorderLayout.WEST);
        this.add(this.combo);
    }

    @Override
    public E getValue() {
        return this.combo.getItemAt(this.combo.getSelectedIndex());
    }

    @Override
    public void setValue(E value) {
        this.suppressEvents = true;
        try {
            this.combo.setSelectedItem(value);
        } finally {
            this.suppressEvents = false;
        }
    }

    @Override
    public void registerOnChange(Consumer<E> onChange) {
        this.listeners.add(onChange);
    }

    @Override
    protected JComponent getValueComponent() {
        return this.combo;
    }
}
