package com.adamk33n3r.runelite.watchdog.ui.notifications.panels;

import com.adamk33n3r.runelite.watchdog.notifications.Overhead;
import com.adamk33n3r.runelite.watchdog.ui.Icons;
import com.adamk33n3r.runelite.watchdog.ui.panels.PanelUtils;

import net.runelite.client.ui.components.ColorJButton;
import net.runelite.client.ui.components.colorpicker.ColorPickerManager;

import javax.swing.JSpinner;

public class OverheadNotificationPanel extends NotificationContentPanel<Overhead> {
    private final ColorPickerManager colorPickerManager;

    public OverheadNotificationPanel(Overhead notification, ColorPickerManager colorPickerManager, Runnable onChange) {
        super(notification, onChange);
        this.colorPickerManager = colorPickerManager;
        this.init();
    }

    @Override
    protected void buildContent() {
        this.add(PanelUtils.createTextField(
            "Enter your formatted message...",
            "",
            this.notification.getMessage(),
            val -> {
                this.notification.setMessage(val);
                this.onChange.run();
            }
        ));

        ColorJButton colorPickerBtn = PanelUtils.createColorPicker(
            "Pick a color",
            "The color of the overhead text. Right click to reset.",
            "Text Color",
            this,
            this.notification.getTextColor(),
            this.colorPickerManager,
            false,
            val -> {
                this.notification.setTextColor(val);
                this.onChange.run();
            });
        this.add(colorPickerBtn);

        JSpinner displayTime = PanelUtils.createSpinner(this.notification.getDisplayTime(), 1, 99, 1, val -> {
            this.notification.setDisplayTime(val);
            this.onChange.run();
        });
        this.add(PanelUtils.createIconComponent(Icons.CLOCK, "Time to display overhead in seconds", displayTime));
    }
}
