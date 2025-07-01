package com.adamk33n3r.runelite.watchdog.ui.nodegraph.inputs;

import com.adamk33n3r.nodegraph.Var;
import com.adamk33n3r.nodegraph.VarInput;
import com.adamk33n3r.nodegraph.VarOutput;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

public class BoolInput extends AbstractInput<Boolean> {
    private final JCheckBox checkbox;

    public BoolInput(String label, Var<Boolean> value) {
        JLabel labelComp = new JLabel(label);
        this.checkbox = new JCheckBox();
        this.checkbox.setSelected(value.getValue());
        this.registerOnChange(value::setValue);
        this.add(labelComp, BorderLayout.WEST);
        this.add(this.checkbox);
    }

    @Override
    public Boolean getValue() {
        return this.checkbox.isSelected();
    }

    @Override
    public void setValue(Boolean value) {
        this.checkbox.setSelected(value);
    }

    @Override
    public void registerOnChange(Consumer<Boolean> onChange) {
        this.checkbox.addItemListener((e) -> onChange.accept(this.checkbox.isSelected()));
    }

    @Override
    protected JComponent getValueComponent() {
        return this.checkbox;
    }
}
