package com.adamk33n3r.runelite.watchdog.ui;

import com.adamk33n3r.runelite.watchdog.AlertManager;
import com.adamk33n3r.runelite.watchdog.WatchdogPanel;
import com.adamk33n3r.runelite.watchdog.alerts.Alert;
import com.adamk33n3r.runelite.watchdog.ui.panels.PanelUtils;

import net.runelite.client.plugins.config.ConfigPlugin;
import net.runelite.client.ui.DynamicGridLayout;
import net.runelite.client.ui.PluginPanel;
import net.runelite.client.ui.components.MouseDragEventForwarder;
import net.runelite.client.util.ImageUtil;
import net.runelite.client.util.SwingUtil;

import lombok.Getter;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.List;

@Deprecated
public class AlertListItem extends JPanel {
    public static final ImageIcon DELETE_ICON_HOVER;
    public static final ImageIcon CLONE_ICON = new ImageIcon(ImageUtil.loadImageResource(ConfigPlugin.class, "mdi_content-duplicate.png"));
    public static final ImageIcon DELETE_ICON = new ImageIcon(ImageUtil.loadImageResource(ConfigPlugin.class, "mdi_delete.png"));
    public static final ImageIcon DRAG_VERT = new ImageIcon(ImageUtil.loadImageResource(WatchdogPanel.class, "mdi_drag-vertical.png"));

    static {
        DELETE_ICON_HOVER = new ImageIcon(ImageUtil.luminanceOffset(DELETE_ICON.getImage(), -80));
    }

    private static final int ROW_HEIGHT = 30;
    private static final int PADDING = 2;

    @Getter
    private final Alert alert;

    public AlertListItem(WatchdogPanel panel, AlertManager alertManager, Alert alert, List<Alert> parentList, JComponent parent, Runnable onChange) {
        this.alert = alert;
        this.setLayout(new BorderLayout(5, 0));

        MouseDragEventForwarder mouseDragEventForwarder = new MouseDragEventForwarder(parent);

        this.setBorder(new EmptyBorder(PADDING, 0, PADDING, 0));
        this.setPreferredSize(new Dimension(PluginPanel.PANEL_WIDTH, ROW_HEIGHT + PADDING * 2));

        JPanel frontGroup = new JPanel(new DynamicGridLayout(1, 0, 3, 0));

        JButton dragHandle = new JButton(DRAG_VERT);
        SwingUtil.removeButtonDecorations(dragHandle);
        dragHandle.setPreferredSize(new Dimension(8, 16));
        dragHandle.addMouseListener(mouseDragEventForwarder);
        dragHandle.addMouseMotionListener(mouseDragEventForwarder);
        frontGroup.add(dragHandle);

        ToggleButton toggleButton = new ToggleButton();
        toggleButton.setSelected(alert.isEnabled());
        toggleButton.addItemListener(i -> {
            alert.setEnabled(toggleButton.isSelected());
            alertManager.saveAlerts();
        });
        toggleButton.setOpaque(false);
        frontGroup.add(toggleButton);

        this.add(frontGroup, BorderLayout.LINE_START);

        final JButton alertButton = new JButton(alert.getName());
        alertButton.setToolTipText(alert.getName());
        alertButton.addActionListener(ev -> {
            panel.openAlert(alert);
        });
        this.add(alertButton, BorderLayout.CENTER);

        final JPanel actionButtons = new JPanel(new DynamicGridLayout(1, 0, 0, 0));
        this.add(actionButtons, BorderLayout.LINE_END);

        actionButtons.add(PanelUtils.createActionButton(CLONE_ICON, CLONE_ICON, "Clone Alert", (btn, modifiers) -> {
            Alert cloned = alertManager.cloneAlert(alert);
            parentList.add(cloned);
            alertManager.saveAlerts();
            onChange.run();
        }));

        final JButton deleteButton = PanelUtils.createActionButton(DELETE_ICON, DELETE_ICON, "Delete Alert", (btn, modifiers) -> {
            int result = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete the " + alert.getName() + " alert?", "Delete?", JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE);
            if (result == JOptionPane.YES_OPTION) {
                parentList.remove(alert);
                alertManager.saveAlerts();
                onChange.run();
            }
        });
        actionButtons.add(deleteButton);
    }
}
