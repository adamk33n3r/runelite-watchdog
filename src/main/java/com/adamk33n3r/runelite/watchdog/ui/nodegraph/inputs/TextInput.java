package com.adamk33n3r.runelite.watchdog.ui.nodegraph.inputs;

import com.adamk33n3r.nodegraph.Var;
import com.adamk33n3r.runelite.watchdog.ui.FlatTextArea;
import com.adamk33n3r.runelite.watchdog.ui.panels.PanelUtils;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.function.Consumer;

public class TextInput extends AbstractInput<String> {
    private final FlatTextArea textField;

    public TextInput(String label, String text) {
        JLabel labelComp = new JLabel(label);
        this.textField = PanelUtils.createTextArea(label, label, text, (v) -> {});
        this.add(labelComp, BorderLayout.WEST);
        this.add(this.textField);
    }

    public TextInput(String label, Var<String> value) {
        this(label, value.getValue() != null ? value.getValue() : "");
        this.registerOnChange(value::setValue);
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
    public void registerOnChange(Consumer<String> onChange) {
        this.textField.getTextArea().addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                textField.getTextArea().selectAll();
            }

            @Override
            public void focusLost(FocusEvent e) {
                onChange.accept(textField.getText());
            }
        });
    }

    public void registerOnType(Consumer<String> onChange) {
        this.textField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { onChange.accept(textField.getText()); }
            @Override
            public void removeUpdate(DocumentEvent e) { onChange.accept(textField.getText()); }
            @Override
            public void changedUpdate(DocumentEvent e) {}
        });
    }

    @Override
    protected JComponent getValueComponent() {
        return this.textField;
    }
}
