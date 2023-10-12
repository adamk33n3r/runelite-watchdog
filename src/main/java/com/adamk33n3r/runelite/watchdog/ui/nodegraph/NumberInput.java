package com.adamk33n3r.runelite.watchdog.ui.nodegraph;

import javax.swing.*;
import java.awt.*;

public class NumberInput extends JPanel {
    private final JLabel label;
    private final JSpinner spinner;

    public NumberInput(String label, int value) {
        this.label = new JLabel(label);
        this.spinner = new JSpinner();
        this.spinner.setValue(value);
        this.setLayout(new BorderLayout());
        this.add(this.label, BorderLayout.WEST);
        this.add(this.spinner);
    }
}
