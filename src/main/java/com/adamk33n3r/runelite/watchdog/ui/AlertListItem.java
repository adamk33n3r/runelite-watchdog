package com.adamk33n3r.runelite.watchdog.ui;

import com.adamk33n3r.runelite.watchdog.AlertManager;
import com.adamk33n3r.runelite.watchdog.WatchdogPanel;
import com.adamk33n3r.runelite.watchdog.WatchdogPlugin;
import com.adamk33n3r.runelite.watchdog.alerts.Alert;
import com.adamk33n3r.runelite.watchdog.ui.ToggleButton;
import net.runelite.client.ui.PluginPanel;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class AlertListItem extends JPanel {
    public AlertListItem(WatchdogPanel panel, AlertManager alertManager, Alert alert) {
        this.setLayout(new BorderLayout(5, 0));
        this.setPreferredSize(new Dimension(PluginPanel.PANEL_WIDTH, 30));

        ToggleButton toggleButton = new ToggleButton();
        toggleButton.setSelected(alert.isEnabled());
        toggleButton.addItemListener(i -> {
            alert.setEnabled(toggleButton.isSelected());
            alertManager.saveAlerts();
        });
        this.add(toggleButton, BorderLayout.LINE_START);

        final JButton alertButton = new JButton(alert.getName());
        alertButton.setToolTipText(alert.getName());
        alertButton.addActionListener(ev -> {
            panel.openAlert(alert);
        });
        this.add(alertButton, BorderLayout.CENTER);

        final JButton deleteButton = new JButton("x");
        deleteButton.setBorder(null);
        deleteButton.setPreferredSize(new Dimension(30, 0));
        deleteButton.addActionListener(ev -> {
            int result = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete the " + alert.getName() + " alert?", "Delete?", JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE);
            if (result == JOptionPane.YES_OPTION) {
                alertManager.removeAlert(alert);
            }
        });
        this.add(deleteButton, BorderLayout.LINE_END);
    }
}
