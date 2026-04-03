package com.adamk33n3r.runelite.watchdog.ui.notifications.panels;

import com.adamk33n3r.runelite.watchdog.notifications.RequestFocus;
import com.adamk33n3r.runelite.watchdog.ui.panels.PanelUtils;

import javax.swing.JCheckBox;

public class RequestFocusNotificationPanel extends NotificationContentPanel<RequestFocus> {

    public RequestFocusNotificationPanel(RequestFocus notification, Runnable onChange) {
        super(notification, onChange);
        this.init();
    }

    @Override
    protected void buildContent() {
        JCheckBox force = PanelUtils.createCheckbox(
            "Force",
            "Force window focus (bring to foreground)",
            this.notification.isForceFocus(),
            val -> {
                this.notification.setForceFocus(val);
                this.onChange.run();
            }
        );
        this.add(force);
    }
}
