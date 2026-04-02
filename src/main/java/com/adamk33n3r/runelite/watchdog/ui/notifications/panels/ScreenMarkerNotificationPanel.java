package com.adamk33n3r.runelite.watchdog.ui.notifications.panels;

import com.adamk33n3r.runelite.watchdog.WatchdogPlugin;
import com.adamk33n3r.runelite.watchdog.notifications.ScreenMarker;
import com.adamk33n3r.runelite.watchdog.ui.FlatTextArea;
import com.adamk33n3r.runelite.watchdog.ui.Icons;
import com.adamk33n3r.runelite.watchdog.ui.notifications.screenmarker.ScreenMarkerOverlay;
import com.adamk33n3r.runelite.watchdog.ui.notifications.screenmarker.ScreenMarkerUtil;
import com.adamk33n3r.runelite.watchdog.ui.panels.NotificationsPanel;
import com.adamk33n3r.runelite.watchdog.ui.panels.PanelUtils;

import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.components.colorpicker.ColorPickerManager;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import java.awt.BorderLayout;

public class ScreenMarkerNotificationPanel extends NotificationPanel {
    private final ColorPickerManager colorPickerManager;
    private ScreenMarkerOverlay screenMarkerOverlay;
    private JButton setMarkerButton;

    public ScreenMarkerNotificationPanel(ScreenMarker notification, NotificationsPanel parentPanel, ColorPickerManager colorPickerManager, Runnable onChangeListener, PanelUtils.OnRemove onRemove) {
        super(notification, parentPanel, onChangeListener, onRemove);
        this.colorPickerManager = colorPickerManager;
        // Rebind onRemove to hook into it so that we can delete the screen marker when this notification is deleted
        this.onRemove = (ele) -> {
            ScreenMarkerUtil screenMarkerUtil = WatchdogPlugin.getInstance().getScreenMarkerUtil();
            if (this.screenMarkerOverlay != null) {
                screenMarkerUtil.deleteMarker(this.screenMarkerOverlay);
            }
            screenMarkerUtil.finishCreation(true);
            onRemove.elementRemoved(ele);
        };
        this.buildContent(this.settings, onChangeListener);
    }

    @Override
    protected void buildContent(JPanel container, Runnable onChange) {
        net.runelite.client.plugins.screenmarkers.ScreenMarker screenMarker = ((ScreenMarker) this.notification).getScreenMarker();

        this.setMarkerButton = PanelUtils.createButton("Set Marker", "Set Marker", (btn, modifiers) -> {
            ScreenMarkerUtil screenMarkerUtil = WatchdogPlugin.getInstance().getScreenMarkerUtil();
            if (screenMarkerUtil.isCreatingScreenMarker()) {
                this.screenMarkerOverlay = screenMarkerUtil.finishCreation(false);
                this.setMarkerButton.setText("Set Marker");
                this.setMarkerButton.setToolTipText("Set Marker");
            } else {
                screenMarkerUtil.startCreation((ScreenMarker) this.notification);
                if (this.screenMarkerOverlay != null) {
                    screenMarkerUtil.deleteMarker(this.screenMarkerOverlay);
                }
                this.setMarkerButton.setText("Finish");
                this.setMarkerButton.setToolTipText("Finish");
            }
        });
        container.add(this.setMarkerButton);

        FlatTextArea markerLabel = PanelUtils.createTextField(
            "Optional marker label...", "",
            screenMarker.getName(),
            val -> {
                screenMarker.setName(val);
                screenMarker.setLabelled(!val.isEmpty());
                onChange.run();
            }
        );
        container.add(markerLabel);

        container.add(PanelUtils.createColorPicker(
            "Border Color", "The color of the border", "Border Color",
            container, screenMarker.getColor(),
            this.colorPickerManager, true,
            val -> { screenMarker.setColor(val); onChange.run(); }));

        container.add(PanelUtils.createColorPicker(
            "Fill Color", "The color of the interior", "Fill Color",
            container, screenMarker.getFill(),
            this.colorPickerManager, true,
            val -> { screenMarker.setFill(val); onChange.run(); }));

        JSpinner thickness = PanelUtils.createSpinner(
            screenMarker.getBorderThickness(), 0, Integer.MAX_VALUE, 1,
            val -> { screenMarker.setBorderThickness(val); onChange.run(); });
        container.add(PanelUtils.createIconComponent(Icons.BORDER_OUTSIDE, "Border thickness", thickness));

        JSpinner displayTime = PanelUtils.createSpinner(((ScreenMarker) this.notification).getDisplayTime(), 0, 99, 1, val -> {
            ((ScreenMarker) this.notification).setDisplayTime(val);
            onChange.run();
        });
        displayTime.setEnabled(!((ScreenMarker) this.notification).isSticky());
        JPanel displayTimePanel = PanelUtils.createIconComponent(Icons.CLOCK, "Time to display the marker in seconds.", displayTime);
        JCheckBox sticky = PanelUtils.createCheckbox("Sticky", "Set the notification to not expire", ((ScreenMarker) this.notification).isSticky(), val -> {
            ((ScreenMarker) this.notification).setSticky(val);
            displayTime.setEnabled(!val);
            onChange.run();
        });

        JPanel sub = new JPanel(new BorderLayout(3, 3));
        sub.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        sub.add(sticky, BorderLayout.EAST);
        sub.add(displayTimePanel);
        container.add(sub);

        container.add(PanelUtils.createTextField(
            "ID for Dismiss Screen Marker...", "",
            ((ScreenMarker) this.notification).getId(),
            val -> { ((ScreenMarker) this.notification).setId(val); onChange.run(); }
        ));
    }

    // ScreenMarkerNotificationPanel is intentionally not using a static buildContent because
    // it needs instance state (screenMarkerOverlay, setMarkerButton) that is incompatible with a
    // stateless static factory.  The factory's populateContent will call the instance buildContent.
}
