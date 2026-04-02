package com.adamk33n3r.runelite.watchdog.ui.notifications.panels;

import com.adamk33n3r.runelite.watchdog.notifications.DismissObjectMarker;
import com.adamk33n3r.runelite.watchdog.ui.FlatTextArea;
import com.adamk33n3r.runelite.watchdog.ui.panels.NotificationsPanel;
import com.adamk33n3r.runelite.watchdog.ui.panels.PanelUtils;

import javax.swing.JPanel;

public class DismissObjectMarkerNotificationPanel extends NotificationPanel {
    public DismissObjectMarkerNotificationPanel(DismissObjectMarker notification, NotificationsPanel parentPanel, Runnable onChangeListener, PanelUtils.OnRemove onRemove) {
        super(notification, parentPanel, onChangeListener, onRemove);
        this.buildContent(this.settings, onChangeListener);
    }

    @Override
    protected void buildContent(JPanel container, Runnable onChange) {
        buildContent((DismissObjectMarker) this.notification, container, onChange);
    }

    public static void buildContent(DismissObjectMarker notification, JPanel container, Runnable onChange) {
        FlatTextArea flatTextArea = PanelUtils.createTextField(
            "Enter the ID of the object marker...",
            "This is set in the Object Marker notification when set to Sticky.",
            notification.getDismissId(),
            val -> {
                notification.setDismissId(val);
                onChange.run();
            }
        );
        container.add(flatTextArea);
    }
}
