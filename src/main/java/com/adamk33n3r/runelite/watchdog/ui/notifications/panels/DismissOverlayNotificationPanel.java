package com.adamk33n3r.runelite.watchdog.ui.notifications.panels;

import com.adamk33n3r.runelite.watchdog.notifications.DismissOverlay;
import com.adamk33n3r.runelite.watchdog.ui.panels.PanelUtils;

public class DismissOverlayNotificationPanel extends NotificationContentPanel<DismissOverlay> {

    public DismissOverlayNotificationPanel(DismissOverlay notification, Runnable onChange) {
        super(notification, onChange);
        this.init();
    }

    @Override
    protected void buildContent() {
        this.add(PanelUtils.createTextField(
            "Enter the ID of the overlay...",
            "This is set in the Overlay notification when set to Sticky.",
            this.notification.getDismissId(),
            val -> {
                this.notification.setDismissId(val);
                this.onChange.run();
            }
        ));
    }
}
