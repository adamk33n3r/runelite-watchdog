package com.adamk33n3r.runelite.watchdog.ui.nodegraph.inputs;

import com.adamk33n3r.runelite.watchdog.ui.panels.PanelUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.function.Consumer;

public class TextInput extends AbstractInput<String> {
    private final JTextArea textField;

    public TextInput(String label, String text) {
        JLabel labelComp = new JLabel(label);
        this.textField = PanelUtils.createTextArea(label, label, text, (v) -> {});
        this.add(labelComp, BorderLayout.WEST);
        this.add(this.textField);
    }

    @Override
    public String getValue() {
        return this.textField.getText();
    }

    @Override
    public void setValue(String text) {
        this.textField.setText(text);
    }

    @Override
    public void onChange(Consumer<String> onChange) {
        this.textField.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                textField.selectAll();
            }

            @Override
            public void focusLost(FocusEvent e) {
                onChange.accept(textField.getText());
            }
        });
    }

    @Override
    protected JComponent getValueComponent() {
        return this.textField;
    }
}
