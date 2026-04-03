package com.adamk33n3r.runelite.watchdog.ui.notifications.panels;

import com.adamk33n3r.runelite.watchdog.notifications.DismissObjectMarker;
import com.adamk33n3r.runelite.watchdog.ui.panels.PanelUtils;

public class DismissObjectMarkerNotificationPanel extends NotificationContentPanel<DismissObjectMarker> {

    public DismissObjectMarkerNotificationPanel(DismissObjectMarker notification, Runnable onChange) {
        super(notification, onChange);
        this.init();
    }

    @Override
    protected void buildContent() {
        this.add(PanelUtils.createTextField(
            "Enter the ID of the object marker...",
            "This is set in the Object Marker notification when set to Sticky.",
            this.notification.getDismissId(),
            val -> {
                this.notification.setDismissId(val);
                this.onChange.run();
            }
        ));
    }
}
