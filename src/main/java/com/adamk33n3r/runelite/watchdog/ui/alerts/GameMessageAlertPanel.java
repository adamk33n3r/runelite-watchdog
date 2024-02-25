package com.adamk33n3r.runelite.watchdog.ui.alerts;

import com.adamk33n3r.runelite.watchdog.GameMessageType;
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
            .addRegexMatcher(this.alert, "Enter the message to trigger on...", "The message to trigger on. Supports glob (*)", MessagePickerButton.createGameMessagePickerButton((selected) -> {
                this.alert.setPattern(selected);
                this.rebuild();
            }, this.alert::getGameMessageType))
            .addSelect("Chat Type", "The type of message", GameMessageType.class, this.alert.getGameMessageType(), this.alert::setGameMessageType)
            .addNotifications();
    }
}
