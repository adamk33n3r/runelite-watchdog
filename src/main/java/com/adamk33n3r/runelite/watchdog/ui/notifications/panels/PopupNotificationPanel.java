package com.adamk33n3r.runelite.watchdog.ui.notifications.panels;

import com.adamk33n3r.runelite.watchdog.notifications.Popup;
import com.adamk33n3r.runelite.watchdog.ui.panels.NotificationsPanel;
import com.adamk33n3r.runelite.watchdog.ui.panels.PanelUtils;
import net.runelite.client.ui.components.ColorJButton;
import net.runelite.client.ui.components.colorpicker.ColorPickerManager;

import javax.swing.*;

public class PopupNotificationPanel extends MessageNotificationPanel {
    public PopupNotificationPanel(Popup notification, NotificationsPanel parentPanel, ColorPickerManager colorPickerManager, Runnable onChangeListener, PanelUtils.OnRemove onRemove) {
        super(notification, true, parentPanel, onChangeListener, onRemove);

        ColorJButton textColorPicker = PanelUtils.createColorPicker(
            "Pick a color",
            "The text color of the notification",
            "Text Color",
            this,
            notification.getTextColor(),
            colorPickerManager,
            false,
            val -> {
                notification.setTextColor(val);
                onChangeListener.run();
            }
        );
        this.settings.add(textColorPicker);
    }
}
