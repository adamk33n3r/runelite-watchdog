package com.adamk33n3r.runelite.watchdog.ui.notifications.panels;

import com.adamk33n3r.runelite.watchdog.notifications.MessageNotification;
import com.adamk33n3r.runelite.watchdog.ui.FlatTextArea;
import com.adamk33n3r.runelite.watchdog.ui.panels.NotificationsPanel;
import com.adamk33n3r.runelite.watchdog.ui.panels.PanelUtils;

public class MessageNotificationPanel extends NotificationPanel {
    public MessageNotificationPanel(MessageNotification notification, NotificationsPanel parentPanel, Runnable onChangeListener, PanelUtils.OnRemove onRemove) {
        this(notification, false, parentPanel, onChangeListener, onRemove);
    }

    public MessageNotificationPanel(MessageNotification notification, boolean supportsFormattingTags, NotificationsPanel parentPanel, Runnable onChangeListener, PanelUtils.OnRemove onRemove) {
        super(notification, parentPanel, onChangeListener, onRemove);

        FlatTextArea flatTextArea = PanelUtils.createTextField(
            supportsFormattingTags ? "Enter your formatted message..." : "Enter your message...",
            "",
            notification.getMessage(),
            val -> {
                notification.setMessage(val);
                onChangeListener.run();
            }
        );
        this.settings.add(flatTextArea);
    }
}
