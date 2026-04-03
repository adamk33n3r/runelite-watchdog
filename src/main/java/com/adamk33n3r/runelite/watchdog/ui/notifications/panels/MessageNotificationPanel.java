package com.adamk33n3r.runelite.watchdog.ui.notifications.panels;

import com.adamk33n3r.runelite.watchdog.notifications.GameMessage;
import com.adamk33n3r.runelite.watchdog.notifications.MessageNotification;
import com.adamk33n3r.runelite.watchdog.ui.panels.PanelUtils;

public class MessageNotificationPanel extends NotificationContentPanel<MessageNotification> {

    public MessageNotificationPanel(MessageNotification notification, Runnable onChange) {
        super(notification, onChange);
        this.init();
    }

    @Override
    protected void buildContent() {
        boolean supportsFormattingTags = this.notification instanceof GameMessage;
        this.add(PanelUtils.createTextField(
            supportsFormattingTags ? "Enter your formatted message..." : "Enter your message...",
            "",
            this.notification.getMessage(),
            val -> {
                this.notification.setMessage(val);
                this.onChange.run();
            }
        ));
    }
}
