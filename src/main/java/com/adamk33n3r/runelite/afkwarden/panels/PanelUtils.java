package com.adamk33n3r.runelite.afkwarden.panels;

import javax.swing.*;
import java.awt.*;

class PanelUtils {
    private PanelUtils () {}

    public static JPanel createLabeledComponent(String label, Component component) {
        JPanel panel = new JPanel(new BorderLayout(5, 0));
        panel.add(new JLabel(label), BorderLayout.WEST);
        panel.add(component);
        return panel;
    }
}
