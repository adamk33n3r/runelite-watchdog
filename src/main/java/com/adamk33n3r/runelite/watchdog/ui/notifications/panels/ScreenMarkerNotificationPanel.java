package com.adamk33n3r.runelite.watchdog.ui.notifications.panels;

import com.adamk33n3r.runelite.watchdog.WatchdogPlugin;
import com.adamk33n3r.runelite.watchdog.notifications.ScreenMarker;
import com.adamk33n3r.runelite.watchdog.ui.Icons;
import com.adamk33n3r.runelite.watchdog.ui.notifications.screenmarker.ScreenMarkerOverlay;
import com.adamk33n3r.runelite.watchdog.ui.notifications.screenmarker.ScreenMarkerUtil;
import com.adamk33n3r.runelite.watchdog.ui.panels.PanelUtils;

import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.components.colorpicker.ColorPickerManager;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import java.awt.BorderLayout;

public class ScreenMarkerNotificationPanel extends NotificationContentPanel<ScreenMarker> {
    private final ColorPickerManager colorPickerManager;
    private ScreenMarkerOverlay screenMarkerOverlay;
    private JButton setMarkerButton;

    public ScreenMarkerNotificationPanel(ScreenMarker notification, ColorPickerManager colorPickerManager, Runnable onChange) {
        super(notification, onChange);
        this.colorPickerManager = colorPickerManager;
        this.init();
    }

    /**
     * Provides an onRemove hook so the screen marker overlay can be cleaned up when this
     * notification is deleted from the sidebar.
     */
    public PanelUtils.OnRemove wrapOnRemove(PanelUtils.OnRemove original) {
        return ele -> {
            ScreenMarkerUtil screenMarkerUtil = WatchdogPlugin.getInstance().getScreenMarkerUtil();
            if (this.screenMarkerOverlay != null) {
                screenMarkerUtil.deleteMarker(this.screenMarkerOverlay);
            }
            screenMarkerUtil.finishCreation(true);
            original.elementRemoved(ele);
        };
    }

    @Override
    protected void buildContent() {
        net.runelite.client.plugins.screenmarkers.ScreenMarker screenMarker = this.notification.getScreenMarker();

        this.setMarkerButton = PanelUtils.createButton("Set Marker", "Set Marker", (btn, modifiers) -> {
            ScreenMarkerUtil screenMarkerUtil = WatchdogPlugin.getInstance().getScreenMarkerUtil();
            if (screenMarkerUtil.isCreatingScreenMarker()) {
                this.screenMarkerOverlay = screenMarkerUtil.finishCreation(false);
                this.setMarkerButton.setText("Set Marker");
                this.setMarkerButton.setToolTipText("Set Marker");
            } else {
                screenMarkerUtil.startCreation(this.notification);
                if (this.screenMarkerOverlay != null) {
                    screenMarkerUtil.deleteMarker(this.screenMarkerOverlay);
                }
                this.setMarkerButton.setText("Finish");
                this.setMarkerButton.setToolTipText("Finish");
            }
        });
        this.add(this.setMarkerButton);

        this.add(PanelUtils.createTextField(
            "Optional marker label...", "",
            screenMarker.getName(),
            val -> {
                screenMarker.setName(val);
                screenMarker.setLabelled(!val.isEmpty());
                this.onChange.run();
            }
        ));

        this.add(PanelUtils.createColorPicker(
            "Border Color", "The color of the border", "Border Color",
            this, screenMarker.getColor(),
            this.colorPickerManager, true,
            val -> { screenMarker.setColor(val); this.onChange.run(); }));

        this.add(PanelUtils.createColorPicker(
            "Fill Color", "The color of the interior", "Fill Color",
            this, screenMarker.getFill(),
            this.colorPickerManager, true,
            val -> { screenMarker.setFill(val); this.onChange.run(); }));

        JSpinner thickness = PanelUtils.createSpinner(
            screenMarker.getBorderThickness(), 0, Integer.MAX_VALUE, 1,
            val -> { screenMarker.setBorderThickness(val); this.onChange.run(); });
        this.add(PanelUtils.createIconComponent(Icons.BORDER_OUTSIDE, "Border thickness", thickness));

        JSpinner displayTime = PanelUtils.createSpinner(this.notification.getDisplayTime(), 0, 99, 1, val -> {
            this.notification.setDisplayTime(val);
            this.onChange.run();
        });
        displayTime.setEnabled(!this.notification.isSticky());
        JPanel displayTimePanel = PanelUtils.createIconComponent(Icons.CLOCK, "Time to display the marker in seconds.", displayTime);
        JCheckBox sticky = PanelUtils.createCheckbox("Sticky", "Set the notification to not expire", this.notification.isSticky(), val -> {
            this.notification.setSticky(val);
            displayTime.setEnabled(!val);
            this.onChange.run();
        });

        JPanel sub = new JPanel(new BorderLayout(3, 3));
        sub.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        sub.add(sticky, BorderLayout.EAST);
        sub.add(displayTimePanel);
        this.add(sub);

        this.add(PanelUtils.createTextField(
            "ID for Dismiss Screen Marker...", "",
            this.notification.getId(),
            val -> { this.notification.setId(val); this.onChange.run(); }
        ));
    }
}
