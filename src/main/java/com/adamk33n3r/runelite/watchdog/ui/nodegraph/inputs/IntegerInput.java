package com.adamk33n3r.runelite.watchdog.ui.nodegraph.inputs;

import com.adamk33n3r.nodegraph.Var;
import com.adamk33n3r.runelite.watchdog.ui.panels.PanelUtils;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

public class IntegerInput extends AbstractInput<Number> {
    private final JSpinner spinner;

    public IntegerInput(String label, Var<Number> value) {
        JLabel labelComp = new JLabel(label);
        this.spinner = PanelUtils.createSpinner(
            value.getValue().intValue(),
            Integer.MIN_VALUE,
            Integer.MAX_VALUE,
            1,
            value::setValue
        );
        this.add(labelComp, BorderLayout.WEST);
        this.add(this.spinner);
    }

    @Override
    public Number getValue() {
        return (Number) this.spinner.getValue();
    }

    @Override
    public void setValue(Number value) {
        this.spinner.setValue(value.intValue());
    }

    @Override
    public void registerOnChange(Consumer<Number> onChange) {
        this.spinner.addChangeListener(e -> onChange.accept((Number) this.spinner.getValue()));
    }

    @Override
    protected JComponent getValueComponent() {
        return this.spinner;
    }
}
