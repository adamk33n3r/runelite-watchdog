package com.adamk33n3r.runelite.watchdog.ui.nodegraph.inputs;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

public class NumberInput extends AbstractInput<Number> {
    private final JSpinner spinner;

    public NumberInput(String label, Number value) {
        JLabel labelComp = new JLabel(label);
        this.spinner = new JSpinner();
        this.spinner.setValue(value);
        this.add(labelComp, BorderLayout.WEST);
        this.add(this.spinner);
    }

    @Override
    public Integer getValue() {
        return (Integer) this.spinner.getValue();
    }

    @Override
    public void setValue(Number value) {
        this.spinner.setValue(value);
    }

    @Override
    public void registerOnChange(Consumer<Number> onChange) {
        this.spinner.addChangeListener((e) -> onChange.accept((Number) this.spinner.getValue()));
    }

    @Override
    protected JComponent getValueComponent() {
        return this.spinner;
    }
}
