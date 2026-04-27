package com.adamk33n3r.runelite.watchdog.ui.nodegraph.inputs;

import com.adamk33n3r.nodegraph.Var;
import com.adamk33n3r.runelite.watchdog.ui.panels.PanelUtils;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

public class NumberInput extends AbstractInput<Number> {
    private final JSpinner spinner;

    public NumberInput(String label, Var<Number> value) {
        JLabel labelComp = new JLabel(label);
        this.spinner = PanelUtils.createSpinnerDouble(
            value.getValue().doubleValue(),
            -Double.MAX_VALUE,
            Double.MAX_VALUE,
            0.1,
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
        this.spinner.setValue(value.doubleValue());
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
