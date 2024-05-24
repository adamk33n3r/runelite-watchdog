package com.adamk33n3r.runelite.watchdog.ui;

import com.adamk33n3r.runelite.watchdog.AlertManager;
import com.adamk33n3r.runelite.watchdog.WatchdogPanel;
import com.adamk33n3r.runelite.watchdog.alerts.Alert;
import com.adamk33n3r.runelite.watchdog.alerts.AlertGroup;
import com.adamk33n3r.runelite.watchdog.ui.panels.PanelUtils;

import net.runelite.client.plugins.config.ConfigPlugin;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.components.MouseDragEventForwarder;
import net.runelite.client.util.ImageUtil;

import lombok.Getter;
import lombok.Setter;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.List;

public class AlertListItem extends JPanel {
    private static final ImageIcon SECTION_EXPAND_ICON;
    private static final ImageIcon SECTION_EXPAND_ICON_HOVER;
    private static final ImageIcon SECTION_RETRACT_ICON;
    private static final ImageIcon SECTION_RETRACT_ICON_HOVER;

    static {
        BufferedImage sectionRetractIcon = ImageUtil.loadImageResource(ConfigPlugin.class, "/util/arrow_right.png");
        sectionRetractIcon = ImageUtil.luminanceOffset(sectionRetractIcon, -121);
        SECTION_EXPAND_ICON = new ImageIcon(sectionRetractIcon);
        SECTION_EXPAND_ICON_HOVER = new ImageIcon(ImageUtil.alphaOffset(sectionRetractIcon, -100));
        final BufferedImage sectionExpandIcon = ImageUtil.rotateImage(sectionRetractIcon, Math.PI / 2);
        SECTION_RETRACT_ICON = new ImageIcon(sectionExpandIcon);
        SECTION_RETRACT_ICON_HOVER = new ImageIcon(ImageUtil.alphaOffset(sectionExpandIcon, -100));
    }

    private static final int ROW_HEIGHT = 30;
    private static final int PADDING = 2;
    private final MouseDragEventForwarder mouseDragEventForwarder;
    private final AlertManager alertManager;
    private final WatchdogPanel panel;

    private boolean collapsed = true;

    @Getter
    private final Alert alert;
    private final Runnable onChange;

    @Getter @Setter
    private boolean selected = false;
    private boolean selectMode = false;

    public AlertListItem(WatchdogPanel panel, AlertManager alertManager, Alert alert, JComponent parent, Runnable onChange) {
        this.panel = panel;
        this.alert = alert;
        this.alertManager = alertManager;
        this.onChange = onChange;
        this.setLayout(new BorderLayout(5, 0));
        this.setBorder(new EmptyBorder(PADDING, 0, PADDING, 0));
        this.setBackground(ColorScheme.DARK_GRAY_COLOR);
        this.mouseDragEventForwarder = new MouseDragEventForwarder(parent);

        this.rebuild();
    }

    public void setSelectMode(boolean selectMode) {
        this.selectMode = selectMode;
        this.rebuild();
    }

    public void rebuild() {
        this.removeAll();

        final JPanel container = new JPanel(new StretchedStackedLayout(3));
        container.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        final JPanel topWrapper = new JPanel(new BorderLayout(3, 3));
        container.add(topWrapper);

        topWrapper.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        topWrapper.setBorder(new CompoundBorder(
            BorderFactory.createMatteBorder(0, 0, this.collapsed ? 0 : 2, 0, ColorScheme.DARK_GRAY_COLOR),
            BorderFactory.createMatteBorder(5, 10, 5, 0, ColorScheme.DARKER_GRAY_COLOR)));

        final ToggleButton toggleButton;
        if (selectMode) {
            toggleButton = new ToggleButton("Deselect", "Select");
            toggleButton.addItemListener((ev) -> this.selected = toggleButton.isSelected());
        } else {
            toggleButton = new ToggleButton();
            toggleButton.setSelected(this.alert.isEnabled());
            toggleButton.addItemListener(i -> {
                this.alert.setEnabled(toggleButton.isSelected());
                this.alertManager.saveAlerts();
            });
        }
        topWrapper.add(toggleButton, BorderLayout.WEST);

        final JPanel nameWrapper = new JPanel(new BorderLayout());
        nameWrapper.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        topWrapper.add(nameWrapper, BorderLayout.CENTER);

        if (this.alert instanceof AlertGroup) {
            final JButton collapseButton = PanelUtils.createActionButton(
                this.collapsed ? SECTION_EXPAND_ICON : SECTION_RETRACT_ICON,
                this.collapsed ? SECTION_EXPAND_ICON_HOVER : SECTION_RETRACT_ICON_HOVER,
                this.collapsed ? "Expand" : "Collapse",
                (btn, evt) -> {
                    this.collapsed = !this.collapsed;
                    this.rebuild();
                    this.revalidate();
                }
            );
            nameWrapper.add(collapseButton, BorderLayout.WEST);
        }

        final JLabel nameLabel = new JLabel(this.alert.getName());
        nameLabel.setToolTipText(this.alert.getName());
        nameWrapper.add(nameLabel);

        topWrapper.addMouseListener(this.mouseDragEventForwarder);
        topWrapper.addMouseMotionListener(this.mouseDragEventForwarder);
        nameWrapper.addMouseListener(this.mouseDragEventForwarder);
        nameWrapper.addMouseMotionListener(this.mouseDragEventForwarder);
        nameLabel.addMouseListener(this.mouseDragEventForwarder);
        if (alert instanceof AlertGroup) {
            nameLabel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (e.getButton() == MouseEvent.BUTTON1) {
                        collapsed = !collapsed;
                        rebuild();
                        revalidate();
                    }
                }
            });
        }
        nameLabel.addMouseMotionListener(this.mouseDragEventForwarder);

        final JPanel rightActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 3, 0));
        rightActions.setBorder(new EmptyBorder(4, 0, 0, 0));
        rightActions.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        topWrapper.add(rightActions, BorderLayout.EAST);

        rightActions.add(PanelUtils.createActionButton(Icons.EDIT, Icons.EDIT_HOVER, "Edit Alert", (btn, modifiers) -> {
            this.panel.openAlert(this.alert);
        })).setEnabled(!selectMode);

        rightActions.add(PanelUtils.createActionButton(Icons.CLONE, Icons.CLONE_HOVER, "Clone Alert", (btn, modifiers) -> {
            Alert cloned = this.alertManager.cloneAlert(this.alert);
            AlertGroup parent = this.alert.getParent();
            if (parent != null) {
                cloned.setParent(parent);
                parent.getAlerts().add(cloned);
            } else {
                this.alertManager.getAlerts().add(cloned);
            }
            this.alertManager.saveAlerts();
            this.onChange.run();
        })).setEnabled(!selectMode);

        rightActions.add(PanelUtils.createActionButton(Icons.DELETE, Icons.DELETE_HOVER, "Delete Alert", (btn, modifiers) -> {
            int result = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete the " + this.alert.getName() + " alert?", "Delete?", JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE);
            if (result == JOptionPane.YES_OPTION) {
                this.alertManager.removeAlert(this.alert);
                this.onChange.run();
            }
        })).setEnabled(!selectMode);

        if (this.alert instanceof AlertGroup && !this.collapsed) {
            final JPanel settings = new JPanel(new StretchedStackedLayout(3));
            settings.setBorder(new EmptyBorder(0, 10, 5, 10));
            settings.setBackground(ColorScheme.DARKER_GRAY_COLOR);
            List<Alert> subAlerts = ((AlertGroup) this.alert).getAlerts();
            for (Alert subAlert : subAlerts) {
                String labelStr = String.format("%s (%s)", subAlert.getName(), subAlert.getType().getName());
                JLabel alertLabel = new JLabel(labelStr);
                alertLabel.setToolTipText(labelStr);
                settings.add(alertLabel);
            }
            if (subAlerts.isEmpty()) {
                settings.add(new JLabel("No alerts in group"));
            }
            container.add(settings);
        }

        this.add(container, BorderLayout.CENTER);
    }
}
