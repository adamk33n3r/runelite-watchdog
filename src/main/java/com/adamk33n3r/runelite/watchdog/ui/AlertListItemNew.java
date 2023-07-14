package com.adamk33n3r.runelite.watchdog.ui;

import com.adamk33n3r.runelite.watchdog.AlertManager;
import com.adamk33n3r.runelite.watchdog.Icons;
import com.adamk33n3r.runelite.watchdog.WatchdogPanel;
import com.adamk33n3r.runelite.watchdog.alerts.Alert;
import com.adamk33n3r.runelite.watchdog.alerts.AlertGroup;
import com.adamk33n3r.runelite.watchdog.ui.panels.PanelUtils;

import net.runelite.client.plugins.screenmarkers.ScreenMarkerPlugin;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.DynamicGridLayout;
import net.runelite.client.ui.PluginPanel;
import net.runelite.client.ui.components.MouseDragEventForwarder;
import net.runelite.client.util.ImageUtil;

import lombok.Getter;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;

//import static com.adamk33n3r.runelite.watchdog.ui.AlertListItem.CLONE_ICON;
//import static com.adamk33n3r.runelite.watchdog.ui.AlertListItem.DELETE_ICON;

public class AlertListItemNew extends JPanel {
//    private static final ImageIcon EDIT_ICON;
//
//    static {
//        final BufferedImage editImg = ImageUtil.loadImageResource(ScreenMarkerPlugin.class, "border_color_icon.png");
//        EDIT_ICON = new ImageIcon(editImg);
//    }

    private static final int ROW_HEIGHT = 30;
    private static final int PADDING = 2;

    @Getter
    private final Alert alert;

    public AlertListItemNew(WatchdogPanel panel, AlertManager alertManager, Alert alert, List<Alert> parentList, JComponent parent, Runnable onChange) {
        this.alert = alert;
        this.setLayout(new BorderLayout(5, 0));
        this.setBorder(new EmptyBorder(PADDING, 0, PADDING, 0));
        this.setMaximumSize(new Dimension(PluginPanel.PANEL_WIDTH, 999));
        this.setAlignmentX(JPanel.LEFT_ALIGNMENT);
//        this.setPreferredSize(new Dimension(PluginPanel.PANEL_WIDTH, ROW_HEIGHT + PADDING * 2));
        this.setBackground(ColorScheme.DARK_GRAY_COLOR);
        MouseDragEventForwarder mouseDragEventForwarder = new MouseDragEventForwarder(parent);

        JPanel container = new JPanel(new StretchedStackedLayout(3, 3));
        container.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        JPanel topWrapper = new JPanel(new BorderLayout(3, 3));
        container.add(topWrapper);

        topWrapper.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        topWrapper.setBorder(new CompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, ColorScheme.DARK_GRAY_COLOR),
            BorderFactory.createMatteBorder(5, 10, 5, 0, ColorScheme.DARKER_GRAY_COLOR)));

        ToggleButton toggleButton = new ToggleButton();
        toggleButton.setSelected(alert.isEnabled());
        toggleButton.addItemListener(i -> {
            alert.setEnabled(toggleButton.isSelected());
            alertManager.saveAlerts();
        });
//            toggleButton.setOpaque(false);
        topWrapper.add(toggleButton, BorderLayout.WEST);

        JPanel nameWrapper = new JPanel(new DynamicGridLayout(1, 0, 2, 2));
        nameWrapper.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        topWrapper.add(nameWrapper, BorderLayout.CENTER);

        JLabel nameLabel = new JLabel(alert.getName());
        nameLabel.setToolTipText(alert.getName());
        nameWrapper.add(nameLabel);

        topWrapper.addMouseListener(mouseDragEventForwarder);
        topWrapper.addMouseMotionListener(mouseDragEventForwarder);
        nameWrapper.addMouseListener(mouseDragEventForwarder);
        nameWrapper.addMouseMotionListener(mouseDragEventForwarder);
        nameLabel.addMouseListener(mouseDragEventForwarder);
        nameLabel.addMouseMotionListener(mouseDragEventForwarder);

        JPanel rightActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 3, 0));
        rightActions.setBorder(new EmptyBorder(4, 0, 0, 0));
        rightActions.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        topWrapper.add(rightActions, BorderLayout.EAST);

        rightActions.add(PanelUtils.createActionButton(Icons.EDIT_ICON, Icons.EDIT_ICON, "Edit Alert", (btn, modifiers) -> {
            panel.openAlert(alert);
        }));

        rightActions.add(PanelUtils.createActionButton(Icons.CLONE_ICON, Icons.CLONE_ICON, "Clone Alert", (btn, modifiers) -> {
            alertManager.cloneAlert(alert);
        }));

        final JButton deleteButton = PanelUtils.createActionButton(Icons.DELETE_ICON, Icons.DELETE_ICON, "Delete Alert", (btn, modifiers) -> {
            int result = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete the " + alert.getName() + " alert?", "Delete?", JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE);
            if (result == JOptionPane.YES_OPTION) {
                alertManager.removeAlert(alert);
            }
        });
        rightActions.add(deleteButton);

        JPanel settings = new JPanel(new StretchedStackedLayout(3, 3));
        settings.setBorder(new EmptyBorder(5, 10, 5, 10));
        settings.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        settings.add(new JLabel("settings place"));
        JButton jButton = new JButton("open");
        jButton.addActionListener((ev) -> {
            panel.openAlert(alert);
        });
        settings.add(jButton);
        container.add(settings);

        this.add(container);
    }
}
