package com.adamk33n3r.runelite.watchdog.ui.notifications.panels;

import com.adamk33n3r.runelite.watchdog.notifications.DismissOverlay;
import com.adamk33n3r.runelite.watchdog.ui.FlatTextArea;
import com.adamk33n3r.runelite.watchdog.ui.panels.NotificationsPanel;
import com.adamk33n3r.runelite.watchdog.ui.panels.PanelUtils;

import javax.swing.JPanel;

public class DismissOverlayNotificationPanel extends NotificationPanel {
    public DismissOverlayNotificationPanel(DismissOverlay notification, NotificationsPanel parentPanel, Runnable onChangeListener, PanelUtils.OnRemove onRemove) {
        super(notification, parentPanel, onChangeListener, onRemove);
        this.buildContent(this.settings, onChangeListener);
    }

    @Override
    protected void buildContent(JPanel container, Runnable onChange) {
        buildContent((DismissOverlay) this.notification, container, onChange);
    }

    public static void buildContent(DismissOverlay notification, JPanel container, Runnable onChange) {
        FlatTextArea flatTextArea = PanelUtils.createTextField(
            "Enter the ID of the overlay...",
            "This is set in the Overlay notification when set to Sticky.",
            notification.getDismissId(),
            val -> {
                notification.setDismissId(val);
                onChange.run();
            }
        );
        container.add(flatTextArea);
    }
}
