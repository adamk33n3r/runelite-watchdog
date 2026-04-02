package com.adamk33n3r.runelite.watchdog.ui.notifications.panels;

import com.adamk33n3r.runelite.watchdog.notifications.MessageNotification;
import com.adamk33n3r.runelite.watchdog.ui.FlatTextArea;
import com.adamk33n3r.runelite.watchdog.ui.panels.NotificationsPanel;
import com.adamk33n3r.runelite.watchdog.ui.panels.PanelUtils;

import javax.swing.JPanel;

public class MessageNotificationPanel extends NotificationPanel {
    public MessageNotificationPanel(MessageNotification notification, NotificationsPanel parentPanel, Runnable onChangeListener, PanelUtils.OnRemove onRemove) {
        this(notification, false, parentPanel, onChangeListener, onRemove);
    }

    public MessageNotificationPanel(MessageNotification notification, boolean supportsFormattingTags, NotificationsPanel parentPanel, Runnable onChangeListener, PanelUtils.OnRemove onRemove) {
        super(notification, parentPanel, onChangeListener, onRemove);
        this.buildContent(this.settings, onChangeListener);
    }

    @Override
    protected void buildContent(JPanel container, Runnable onChange) {
        buildContent((MessageNotification) this.notification, false, container, onChange);
    }

    public static void buildContent(MessageNotification notification, boolean supportsFormattingTags, JPanel container, Runnable onChange) {
        FlatTextArea flatTextArea = PanelUtils.createTextField(
            supportsFormattingTags ? "Enter your formatted message..." : "Enter your message...",
            "",
            notification.getMessage(),
            val -> {
                notification.setMessage(val);
                onChange.run();
            }
        );
        container.add(flatTextArea);
    }
}
