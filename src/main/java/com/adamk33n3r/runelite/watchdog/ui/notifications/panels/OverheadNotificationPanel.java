package com.adamk33n3r.runelite.watchdog.ui.notifications.panels;

import com.adamk33n3r.runelite.watchdog.notifications.Overhead;
import com.adamk33n3r.runelite.watchdog.ui.panels.NotificationsPanel;
import com.adamk33n3r.runelite.watchdog.ui.panels.PanelUtils;

import javax.swing.JSpinner;

public class OverheadNotificationPanel extends MessageNotificationPanel {
    public OverheadNotificationPanel(Overhead notification, NotificationsPanel parentPanel, Runnable onChangeListener, PanelUtils.OnRemove onRemove) {
        super(notification, parentPanel, onChangeListener, onRemove);

        JSpinner displayTime = PanelUtils.createSpinner(notification.getDisplayTime(), 1, 99, 1, val -> {
            notification.setDisplayTime(val);
            onChangeListener.run();
        });
        this.settings.add(PanelUtils.createIconComponent(CLOCK_ICON, "Time to display overhead in seconds", displayTime));
    }
}
