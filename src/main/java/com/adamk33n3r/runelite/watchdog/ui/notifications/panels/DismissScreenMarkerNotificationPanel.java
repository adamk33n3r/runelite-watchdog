package com.adamk33n3r.runelite.watchdog.ui.notifications.panels;

import com.adamk33n3r.runelite.watchdog.notifications.DismissScreenMarker;
import com.adamk33n3r.runelite.watchdog.ui.FlatTextArea;
import com.adamk33n3r.runelite.watchdog.ui.panels.NotificationsPanel;
import com.adamk33n3r.runelite.watchdog.ui.panels.PanelUtils;

import javax.swing.JPanel;

public class DismissScreenMarkerNotificationPanel extends NotificationPanel {
    public DismissScreenMarkerNotificationPanel(DismissScreenMarker notification, NotificationsPanel parentPanel, Runnable onChangeListener, PanelUtils.OnRemove onRemove) {
        super(notification, parentPanel, onChangeListener, onRemove);
        this.buildContent(this.settings, onChangeListener);
    }

    @Override
    protected void buildContent(JPanel container, Runnable onChange) {
        buildContent((DismissScreenMarker) this.notification, container, onChange);
    }

    public static void buildContent(DismissScreenMarker notification, JPanel container, Runnable onChange) {
        FlatTextArea flatTextArea = PanelUtils.createTextField(
            "Enter the ID of the screen marker...",
            "This is set in the Screen Marker notification when set to Sticky.",
            notification.getDismissId(),
            val -> {
                notification.setDismissId(val);
                onChange.run();
            }
        );
        container.add(flatTextArea);
    }
}
