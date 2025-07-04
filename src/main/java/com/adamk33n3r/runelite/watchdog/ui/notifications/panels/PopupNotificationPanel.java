package com.adamk33n3r.runelite.watchdog.ui.notifications.panels;

import com.adamk33n3r.runelite.watchdog.notifications.Popup;
import com.adamk33n3r.runelite.watchdog.ui.FlatTextArea;
import com.adamk33n3r.runelite.watchdog.ui.panels.NotificationsPanel;
import com.adamk33n3r.runelite.watchdog.ui.panels.PanelUtils;
import net.runelite.client.ui.components.ColorJButton;
import net.runelite.client.ui.components.colorpicker.ColorPickerManager;

import java.awt.*;

public class PopupNotificationPanel extends MessageNotificationPanel {
    public PopupNotificationPanel(Popup notification, NotificationsPanel parentPanel, ColorPickerManager colorPickerManager, Runnable onChangeListener, PanelUtils.OnRemove onRemove) {
        super(notification, true, parentPanel, onChangeListener, onRemove);

        Component message = this.settings.getComponent(0);
        this.settings.remove(message);

        FlatTextArea title = PanelUtils.createTextArea(
            "Title. Empty uses the alert's name.",
            "The title of the popup. Leave empty to use the alert's name. Also supports formatting and capture groups.",
            notification.getTitle(),
            notification::setTitle
        );

        this.settings.add(title);
        this.settings.add(message);

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
