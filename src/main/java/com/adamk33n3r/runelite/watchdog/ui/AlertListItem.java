package com.adamk33n3r.runelite.watchdog.ui;

import com.adamk33n3r.runelite.watchdog.AlertManager;
import com.adamk33n3r.runelite.watchdog.WatchdogPanel;
import com.adamk33n3r.runelite.watchdog.alerts.Alert;
import com.adamk33n3r.runelite.watchdog.ui.panels.PanelUtils;

import net.runelite.client.ui.DynamicGridLayout;
import net.runelite.client.ui.PluginPanel;
import net.runelite.client.util.ImageUtil;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.image.BufferedImage;

public class AlertListItem extends JPanel {
    public static final ImageIcon DELETE_ICON;
    public static final ImageIcon DELETE_ICON_HOVER;

    static {
        final BufferedImage deleteImg = ImageUtil.resizeCanvas(ImageUtil.loadImageResource(WatchdogPanel.class, "delete_icon.png"), 10, 10);
        DELETE_ICON = new ImageIcon(deleteImg);
        DELETE_ICON_HOVER = new ImageIcon(ImageUtil.luminanceOffset(deleteImg, -80));
    }
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

        final JPanel actionButtons = new JPanel(new DynamicGridLayout(1, 0, 0, 0));
        this.add(actionButtons, BorderLayout.LINE_END);

        UpDownArrows upDownArrows = new UpDownArrows("Move Alert up", btn -> {
            alertManager.moveAlertUp(alert);
            panel.rebuild();
        }, "Move Alert down", btn -> {
            alertManager.moveAlertDown(alert);
            panel.rebuild();
        }, true);
        actionButtons.add(upDownArrows);

        final JButton deleteButton = PanelUtils.createActionButton(DELETE_ICON, DELETE_ICON_HOVER, "Delete Alert", btn -> {
            int result = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete the " + alert.getName() + " alert?", "Delete?", JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE);
            if (result == JOptionPane.YES_OPTION) {
                alertManager.removeAlert(alert);
            }
        });
        actionButtons.add(deleteButton);
    }
}
