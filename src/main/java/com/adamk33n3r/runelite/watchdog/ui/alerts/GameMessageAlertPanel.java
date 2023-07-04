package com.adamk33n3r.runelite.watchdog.ui.alerts;

import com.adamk33n3r.runelite.watchdog.WatchdogPanel;
import com.adamk33n3r.runelite.watchdog.alerts.ChatAlert;
import com.adamk33n3r.runelite.watchdog.ui.panels.AlertPanel;

public class GameMessageAlertPanel extends AlertPanel<ChatAlert> {
    public GameMessageAlertPanel(WatchdogPanel watchdogPanel, ChatAlert alert) {
        super(watchdogPanel, alert);
    }

    @Override
    protected void build() {
        this.addAlertDefaults()
            .addRegexMatcher(this.alert, "Enter the message to trigger on...", "The message to trigger on. Supports glob (*)")
            .addLabel("<html><i>Note: Will not trigger on<br>player chat messages</i></html>")
            .addNotifications();
    }
}
