package com.adamk33n3r.runelite.watchdog.ui.notifications.panels;

import com.adamk33n3r.runelite.watchdog.notifications.RequestFocus;
import com.adamk33n3r.runelite.watchdog.ui.panels.NotificationsPanel;
import com.adamk33n3r.runelite.watchdog.ui.panels.PanelUtils;

import javax.swing.JCheckBox;
import javax.swing.JPanel;

public class RequestFocusNotificationPanel extends NotificationPanel {
    public RequestFocusNotificationPanel(RequestFocus notification, NotificationsPanel parentPanel, Runnable onChangeListener, PanelUtils.OnRemove onRemove) {
        super(notification, parentPanel, onChangeListener, onRemove);
        this.buildContent(this.settings, onChangeListener);
    }

    @Override
    protected void buildContent(JPanel container, Runnable onChange) {
        buildContent((RequestFocus) this.notification, container, onChange);
    }

    public static void buildContent(RequestFocus notification, JPanel container, Runnable onChange) {
        JCheckBox force = PanelUtils.createCheckbox(
            "Force",
            "Force window focus (bring to foreground)",
            notification.isForceFocus(),
            (val) -> {
                notification.setForceFocus(val);
                onChange.run();
            }
        );
        container.add(force);
    }
}
