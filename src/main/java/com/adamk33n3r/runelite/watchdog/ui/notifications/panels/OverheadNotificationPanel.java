package com.adamk33n3r.runelite.watchdog.ui.notifications.panels;

import com.adamk33n3r.runelite.watchdog.notifications.Overhead;
import com.adamk33n3r.runelite.watchdog.ui.Icons;
import com.adamk33n3r.runelite.watchdog.ui.panels.NotificationsPanel;
import com.adamk33n3r.runelite.watchdog.ui.panels.PanelUtils;

import net.runelite.client.ui.components.ColorJButton;
import net.runelite.client.ui.components.colorpicker.ColorPickerManager;

import javax.swing.JSpinner;

public class OverheadNotificationPanel extends MessageNotificationPanel {
    public OverheadNotificationPanel(Overhead notification, NotificationsPanel parentPanel, ColorPickerManager colorPickerManager, Runnable onChangeListener, PanelUtils.OnRemove onRemove) {
        super(notification, parentPanel, onChangeListener, onRemove);

        ColorJButton colorPickerBtn = PanelUtils.createColorPicker(
            "Pick a color",
            "The color of the overhead text. Right click to reset.",
            "Text Color",
            this,
            notification.getTextColor(),
            colorPickerManager,
            false,
            val -> {
                notification.setTextColor(val);
                onChangeListener.run();
            });
        this.settings.add(colorPickerBtn);

        JSpinner displayTime = PanelUtils.createSpinner(notification.getDisplayTime(), 1, 99, 1, val -> {
            notification.setDisplayTime(val);
            onChangeListener.run();
        });
        this.settings.add(PanelUtils.createIconComponent(Icons.CLOCK, "Time to display overhead in seconds", displayTime));
    }
}
