package com.adamk33n3r.runelite.watchdog.ui.notifications.panels;

import com.adamk33n3r.runelite.watchdog.WatchdogPlugin;
import com.adamk33n3r.runelite.watchdog.notifications.objectmarkers.ObjectMarker;
import com.adamk33n3r.runelite.watchdog.notifications.objectmarkers.ObjectMarkerManager;
import com.adamk33n3r.runelite.watchdog.ui.Icons;
import com.adamk33n3r.runelite.watchdog.ui.panels.PanelUtils;

import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.components.colorpicker.ColorPickerManager;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import java.awt.BorderLayout;
import java.awt.GridLayout;

public class ObjectMarkerNotificationPanel extends NotificationContentPanel<ObjectMarker> {
    private final ColorPickerManager colorPickerManager;
    private JButton setMarkerButton;

    public ObjectMarkerNotificationPanel(ObjectMarker notification, ColorPickerManager colorPickerManager, Runnable onChange) {
        super(notification, onChange);
        this.colorPickerManager = colorPickerManager;
        this.init();
    }

    @Override
    protected void buildContent() {
        this.setMarkerButton = PanelUtils.createButton("Set Marker", "Set Marker", (btn, modifiers) -> {
            ObjectMarkerManager objectMarkerManager = WatchdogPlugin.getInstance().getObjectMarkerManager();
            if (objectMarkerManager.isInObjectMarkerMode() && this.setMarkerButton.getText().equals("Finish")) {
                this.setMarkerButton.setText("Set Marker");
                this.setMarkerButton.setToolTipText("Set Marker");
                objectMarkerManager.turnOffObjectMarkerMode();
                this.onChange.run();
            } else if (!objectMarkerManager.isInObjectMarkerMode() && this.setMarkerButton.getText().equals("Set Marker")) {
                this.setMarkerButton.setText("Finish");
                this.setMarkerButton.setToolTipText("Finish");
                objectMarkerManager.turnOnObjectMarkerMode(this.notification);
            }
        });
        this.add(this.setMarkerButton);

        this.add(PanelUtils.createColorPicker(
            "Border Color", "The color of the border", "Border Color",
            this, this.notification.getBorderColor(),
            this.colorPickerManager, true,
            val -> { this.notification.setBorderColor(val); this.onChange.run(); }));

        this.add(PanelUtils.createColorPicker(
            "Fill Color", "The color of the interior", "Fill Color",
            this, this.notification.getFillColor(),
            this.colorPickerManager, true,
            val -> { this.notification.setFillColor(val); this.onChange.run(); }));

        JPanel checkboxes = new JPanel(new GridLayout(2, 2, 5, 5));
        checkboxes.add(PanelUtils.createCheckbox("Hull", "Render Hull", this.notification.isHull(), val -> { this.notification.setHull(val); this.onChange.run(); }));
        checkboxes.add(PanelUtils.createCheckbox("Outline", "Render Outline", this.notification.isOutline(), val -> { this.notification.setOutline(val); this.onChange.run(); }));
        checkboxes.add(PanelUtils.createCheckbox("Clickbox", "Render Clickbox", this.notification.isClickbox(), val -> { this.notification.setClickbox(val); this.onChange.run(); }));
        checkboxes.add(PanelUtils.createCheckbox("Tile", "Render Tile", this.notification.isTile(), val -> { this.notification.setTile(val); this.onChange.run(); }));
        checkboxes.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        this.add(checkboxes);

        JPanel feather = PanelUtils.createIconComponent(Icons.FEATHER, "Specify between 0-4 how much of the model outline should be faded.",
            PanelUtils.createSpinner(this.notification.getOutlineFeather(), 0, 4, 1, val -> { this.notification.setOutlineFeather(val); this.onChange.run(); }));

        JSpinner thickness = PanelUtils.createSpinnerDouble(
            this.notification.getBorderWidth(), 0, 200, 0.1d,
            val -> { this.notification.setBorderWidth(val); this.onChange.run(); });
        JPanel borderThickness = PanelUtils.createIconComponent(Icons.BORDER_OUTSIDE, "Width of the marked object border.", thickness);

        JPanel borderFeatherSub = new JPanel(new GridLayout(1, 2, 3, 3));
        borderFeatherSub.add(borderThickness);
        borderFeatherSub.add(feather);
        borderFeatherSub.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        this.add(borderFeatherSub);

        JPanel stickySub = new JPanel(new BorderLayout(3, 3));
        stickySub.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        this.add(stickySub);

        JSpinner displayTime = PanelUtils.createSpinner(this.notification.getDisplayTime(), 0, 99, 1, val -> { this.notification.setDisplayTime(val); this.onChange.run(); });
        displayTime.setEnabled(!this.notification.isSticky());
        JPanel displayTimePanel = PanelUtils.createIconComponent(Icons.CLOCK, "Time to display the marker in seconds.", displayTime);
        JCheckBox sticky = PanelUtils.createCheckbox("Sticky", "Set the notification to not expire", this.notification.isSticky(), val -> {
            this.notification.setSticky(val);
            displayTime.setEnabled(!val);
            stickySub.revalidate();
            stickySub.repaint();
            this.onChange.run();
        });
        stickySub.add(sticky, BorderLayout.EAST);
        stickySub.add(displayTimePanel);

        this.add(PanelUtils.createTextArea(
            "ID for Dismiss Object Marker...", "",
            this.notification.getId(),
            val -> { this.notification.setId(val); this.onChange.run(); }
        ));
    }
}
