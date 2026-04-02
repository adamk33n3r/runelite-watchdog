package com.adamk33n3r.runelite.watchdog.ui.alerts;

import com.adamk33n3r.runelite.watchdog.WatchdogPanel;
import com.adamk33n3r.runelite.watchdog.alerts.NotificationFiredAlert;
import com.adamk33n3r.runelite.watchdog.ui.panels.AlertContentBuilder;
import com.adamk33n3r.runelite.watchdog.ui.panels.AlertPanel;

public class NotificationFiredAlertPanel extends AlertPanel<NotificationFiredAlert> {
    public NotificationFiredAlertPanel(WatchdogPanel watchdogPanel, NotificationFiredAlert alert) {
        super(watchdogPanel, alert);
    }

    @Override
    protected void build() {
        this.addAlertDefaults();
        buildTypeContent(this.alert, new AlertContentBuilder(this.getControlContainer(), this.getSaveAction(), this::rebuild));
        this.addNotifications();
    }

    public static void buildTypeContent(NotificationFiredAlert alert, AlertContentBuilder builder) {
        builder
            .addRegexMatcher(alert, "Enter the message to trigger on...", "The message to trigger on. Supports glob (*)", MessagePickerButton.createNotificationPickerButton((selected) -> {
                alert.setPattern(selected);
                builder.rebuild();
            }))
            .addCheckbox("Allow Watchdog Notifications", "Allow Watchdog notifications to trigger this alert. Be careful with this, can easily cause an infinite loop", alert.isAllowSelf(), alert::setAllowSelf);
    }
}
