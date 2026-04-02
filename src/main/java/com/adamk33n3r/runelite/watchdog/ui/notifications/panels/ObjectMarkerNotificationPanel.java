package com.adamk33n3r.runelite.watchdog.ui.notifications.panels;

import com.adamk33n3r.runelite.watchdog.WatchdogPlugin;
import com.adamk33n3r.runelite.watchdog.notifications.objectmarkers.ObjectMarker;
import com.adamk33n3r.runelite.watchdog.notifications.objectmarkers.ObjectMarkerManager;
import com.adamk33n3r.runelite.watchdog.ui.Icons;
import com.adamk33n3r.runelite.watchdog.ui.panels.NotificationsPanel;
import com.adamk33n3r.runelite.watchdog.ui.panels.PanelUtils;

import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.components.colorpicker.ColorPickerManager;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import java.awt.BorderLayout;
import java.awt.GridLayout;

public class ObjectMarkerNotificationPanel extends NotificationPanel {
    private final ColorPickerManager colorPickerManager;
    private JButton setMarkerButton;

    public ObjectMarkerNotificationPanel(ObjectMarker notification, NotificationsPanel parentPanel, ColorPickerManager colorPickerManager, Runnable onChangeListener, PanelUtils.OnRemove onRemove) {
        super(notification, parentPanel, onChangeListener, onRemove);
        this.colorPickerManager = colorPickerManager;
        this.buildContent(this.settings, onChangeListener);
    }

    @Override
    protected void buildContent(JPanel container, Runnable onChange) {
        ObjectMarker notification = (ObjectMarker) this.notification;

        this.setMarkerButton = PanelUtils.createButton("Set Marker", "Set Marker", (btn, modifiers) -> {
            ObjectMarkerManager objectMarkerManager = WatchdogPlugin.getInstance().getObjectMarkerManager();
            if (objectMarkerManager.isInObjectMarkerMode() && this.setMarkerButton.getText().equals("Finish")) {
                this.setMarkerButton.setText("Set Marker");
                this.setMarkerButton.setToolTipText("Set Marker");
                objectMarkerManager.turnOffObjectMarkerMode();
                onChange.run();
            } else if (!objectMarkerManager.isInObjectMarkerMode() && this.setMarkerButton.getText().equals("Set Marker")) {
                this.setMarkerButton.setText("Finish");
                this.setMarkerButton.setToolTipText("Finish");
                objectMarkerManager.turnOnObjectMarkerMode(notification);
            }
        });
        container.add(this.setMarkerButton);

        container.add(PanelUtils.createColorPicker(
            "Border Color", "The color of the border", "Border Color",
            container, notification.getBorderColor(),
            this.colorPickerManager, true,
            val -> { notification.setBorderColor(val); onChange.run(); }));

        container.add(PanelUtils.createColorPicker(
            "Fill Color", "The color of the interior", "Fill Color",
            container, notification.getFillColor(),
            this.colorPickerManager, true,
            val -> { notification.setFillColor(val); onChange.run(); }));

        JPanel checkboxes = new JPanel(new GridLayout(2, 2, 5, 5));
        checkboxes.add(PanelUtils.createCheckbox("Hull", "Render Hull", notification.isHull(), val -> { notification.setHull(val); onChange.run(); }));
        checkboxes.add(PanelUtils.createCheckbox("Outline", "Render Outline", notification.isOutline(), val -> { notification.setOutline(val); onChange.run(); }));
        checkboxes.add(PanelUtils.createCheckbox("Clickbox", "Render Clickbox", notification.isClickbox(), val -> { notification.setClickbox(val); onChange.run(); }));
        checkboxes.add(PanelUtils.createCheckbox("Tile", "Render Tile", notification.isTile(), val -> { notification.setTile(val); onChange.run(); }));
        checkboxes.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        container.add(checkboxes);

        JPanel feather = PanelUtils.createIconComponent(Icons.FEATHER, "Specify between 0-4 how much of the model outline should be faded.",
            PanelUtils.createSpinner(notification.getOutlineFeather(), 0, 4, 1, val -> { notification.setOutlineFeather(val); onChange.run(); }));

        JSpinner thickness = PanelUtils.createSpinnerDouble(
            notification.getBorderWidth(), 0, 200, 0.1d,
            val -> { notification.setBorderWidth(val); onChange.run(); });
        JPanel borderThickness = PanelUtils.createIconComponent(Icons.BORDER_OUTSIDE, "Width of the marked object border.", thickness);

        JPanel borderFeatherSub = new JPanel(new GridLayout(1, 2, 3, 3));
        borderFeatherSub.add(borderThickness);
        borderFeatherSub.add(feather);
        borderFeatherSub.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        container.add(borderFeatherSub);

        JPanel stickySub = new JPanel(new BorderLayout(3, 3));
        stickySub.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        container.add(stickySub);

        JSpinner displayTime = PanelUtils.createSpinner(notification.getDisplayTime(), 0, 99, 1, val -> { notification.setDisplayTime(val); onChange.run(); });
        displayTime.setEnabled(!notification.isSticky());
        JPanel displayTimePanel = PanelUtils.createIconComponent(Icons.CLOCK, "Time to display the marker in seconds.", displayTime);
        JCheckBox sticky = PanelUtils.createCheckbox("Sticky", "Set the notification to not expire", notification.isSticky(), val -> {
            notification.setSticky(val);
            displayTime.setEnabled(!val);
            stickySub.revalidate();
            stickySub.repaint();
            onChange.run();
        });
        stickySub.add(sticky, BorderLayout.EAST);
        stickySub.add(displayTimePanel);

        container.add(PanelUtils.createTextArea(
            "ID for Dismiss Object Marker...", "",
            notification.getId(),
            val -> { notification.setId(val); onChange.run(); }
        ));
    }

    // ObjectMarkerNotificationPanel uses instance state (setMarkerButton) like ScreenMarkerNotificationPanel.
}
