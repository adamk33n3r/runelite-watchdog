package com.adamk33n3r.runelite.watchdog.ui.notifications.panels;

import com.adamk33n3r.runelite.watchdog.notifications.Popup;
import com.adamk33n3r.runelite.watchdog.ui.panels.PanelUtils;

import net.runelite.client.ui.components.ColorJButton;
import net.runelite.client.ui.components.colorpicker.ColorPickerManager;

public class PopupNotificationPanel extends NotificationContentPanel<Popup> {
    private final ColorPickerManager colorPickerManager;

    public PopupNotificationPanel(Popup notification, ColorPickerManager colorPickerManager, Runnable onChange) {
        super(notification, onChange);
        this.colorPickerManager = colorPickerManager;
        this.init();
    }

    @Override
    protected void buildContent() {
        this.add(PanelUtils.createTextArea(
            "Title. Empty uses the alert's name.",
            "The title of the popup. Leave empty to use the alert's name. Also supports formatting and capture groups.",
            this.notification.getTitle(),
            this.notification::setTitle
        ));

        this.add(PanelUtils.createTextField(
            "Enter your formatted message...",
            "",
            this.notification.getMessage(),
            val -> {
                this.notification.setMessage(val);
                this.onChange.run();
            }
        ));

        ColorJButton textColorPicker = PanelUtils.createColorPicker(
            "Pick a color",
            "The text color of the notification",
            "Text Color",
            this,
            this.notification.getTextColor(),
            this.colorPickerManager,
            false,
            val -> {
                this.notification.setTextColor(val);
                this.onChange.run();
            }
        );
        this.add(textColorPicker);
    }
}
