package com.adamk33n3r.runelite.watchdog.ui.panels;

import javax.swing.JComponent;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.List;

class InputGroup extends JPanel {
    private final JPanel prefixes = new JPanel(new GridLayout(1, 0, 0, 0));
    private final JPanel suffixes = new JPanel(new GridLayout(1, 0, 0, 0));
    public InputGroup(JComponent mainComponent) {
        super(new BorderLayout());
        this.add(mainComponent);
        this.add(this.prefixes, BorderLayout.WEST);
        this.add(this.suffixes, BorderLayout.EAST);
    }

    public InputGroup addPrefix(JComponent component) {
        this.prefixes.add(component);
        return this;
    }

    public InputGroup addPrefixes(List<JComponent> components) {
        if (components != null) {
            components.forEach(this.prefixes::add);
        }
        return this;
    }

    public InputGroup addSuffix(JComponent component) {
        this.suffixes.add(component);
        return this;
    }

    public InputGroup addSuffixes(List<JComponent> components) {
        if (components != null) {
            components.forEach(this.suffixes::add);
        }
        return this;
    }
}
