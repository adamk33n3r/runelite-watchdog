package com.adamk33n3r.runelite.watchdog.ui.notifications.panels;

import com.adamk33n3r.runelite.watchdog.notifications.Overlay;
import com.adamk33n3r.runelite.watchdog.ui.panels.NotificationsPanel;
import com.adamk33n3r.runelite.watchdog.ui.panels.PanelUtils;

import net.runelite.client.ui.components.ColorJButton;
import net.runelite.client.ui.components.colorpicker.ColorPickerManager;

import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JSpinner;

public class OverlayNotificationPanel extends MessageNotificationPanel {
    private JPanel displayTime;

    public OverlayNotificationPanel(Overlay notification, NotificationsPanel parentPanel, ColorPickerManager colorPickerManager, Runnable onChangeListener, PanelUtils.OnRemove onRemove) {
        super(notification, parentPanel, onChangeListener, onRemove);

        ColorJButton colorPicker = PanelUtils.createColorPicker(
            "Pick a color",
            "The background color of the notification",
            "Background Color",
            this,
            notification.getColor(),
            colorPickerManager,
            val -> {
                notification.setColor(val);
                onChangeListener.run();
            }
        );
        this.settings.add(colorPicker);

        JCheckBox sticky = PanelUtils.createCheckbox("Sticky", "Set the notification to not expire", notification.isSticky(), val -> {
            notification.setSticky(val);
            if (val) {
                this.settings.remove(this.displayTime);
            } else {
                this.settings.add(this.displayTime);
            }
            this.revalidate();
            onChangeListener.run();
        });
        this.settings.add(sticky);

        JSpinner displayTime = PanelUtils.createSpinner(notification.getTimeToLive(), 1, 99, 1, val -> {
            notification.setTimeToLive(val);
            onChangeListener.run();
        });
        this.displayTime = PanelUtils.createIconComponent(CLOCK_ICON, "Time to display in seconds", displayTime);

        if (!notification.isSticky()) {
            this.settings.add(this.displayTime);
        }
    }
}
