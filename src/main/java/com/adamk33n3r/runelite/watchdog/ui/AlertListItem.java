package com.adamk33n3r.runelite.watchdog.ui;

import com.adamk33n3r.runelite.watchdog.WatchdogPanel;
import com.adamk33n3r.runelite.watchdog.WatchdogPlugin;
import com.adamk33n3r.runelite.watchdog.alerts.Alert;
import com.adamk33n3r.runelite.watchdog.ui.ToggleButton;
import net.runelite.client.ui.PluginPanel;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class AlertListItem extends JPanel {
    public AlertListItem(WatchdogPlugin plugin, WatchdogPanel panel, Alert alert) {
        this.setLayout(new BorderLayout(5, 0));
        this.setPreferredSize(new Dimension(PluginPanel.PANEL_WIDTH, 30));

        ToggleButton toggleButton = new ToggleButton();
        toggleButton.setSelected(alert.isEnabled());
        toggleButton.addItemListener(i -> {
            alert.setEnabled(toggleButton.isSelected());
            plugin.saveAlerts(plugin.getAlerts());
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
                List<Alert> alerts = plugin.getAlerts();
                alerts.remove(alert);
                plugin.saveAlerts(alerts);
                // Rebuild is called automatically when alerts config changed
            }
        });
        this.add(deleteButton, BorderLayout.LINE_END);
    }
}
