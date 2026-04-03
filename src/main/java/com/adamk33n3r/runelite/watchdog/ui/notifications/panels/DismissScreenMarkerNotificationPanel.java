package com.adamk33n3r.runelite.watchdog.ui.notifications.panels;

import com.adamk33n3r.runelite.watchdog.notifications.DismissScreenMarker;
import com.adamk33n3r.runelite.watchdog.ui.panels.PanelUtils;

public class DismissScreenMarkerNotificationPanel extends NotificationContentPanel<DismissScreenMarker> {

    public DismissScreenMarkerNotificationPanel(DismissScreenMarker notification, Runnable onChange) {
        super(notification, onChange);
        this.init();
    }

    @Override
    protected void buildContent() {
        this.add(PanelUtils.createTextField(
            "Enter the ID of the screen marker...",
            "This is set in the Screen Marker notification when set to Sticky.",
            this.notification.getDismissId(),
            val -> {
                this.notification.setDismissId(val);
                this.onChange.run();
            }
        ));
    }
}
