package com.adamk33n3r.runelite.watchdog.ui.alerts;

import com.adamk33n3r.runelite.watchdog.alerts.NotificationFiredAlert;
import com.adamk33n3r.runelite.watchdog.ui.panels.AlertContentPanel;

public class NotificationFiredAlertPanel extends AlertContentPanel<NotificationFiredAlert> {

    public NotificationFiredAlertPanel(NotificationFiredAlert alert, Runnable onChange) {
        super(alert, onChange);
        this.init();
    }

    @Override
    public void buildTypeContent() {
        this.addRegexMatcher(this.alert, "Enter the message to trigger on...", "The message to trigger on. Supports glob (*)", MessagePickerButton.createNotificationPickerButton(selected -> {
                this.alert.setPattern(selected);
                this.rebuild();
            }))
            .addCheckbox("Allow Watchdog Notifications", "Allow Watchdog notifications to trigger this alert. Be careful with this, can easily cause an infinite loop", this.alert.isAllowSelf(), this.alert::setAllowSelf);
    }
}
