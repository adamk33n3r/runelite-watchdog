package com.adamk33n3r.runelite.watchdog.ui.alerts;

import com.adamk33n3r.runelite.watchdog.PlayerChatType;
import com.adamk33n3r.runelite.watchdog.WatchdogPanel;
import com.adamk33n3r.runelite.watchdog.alerts.PlayerChatAlert;
import com.adamk33n3r.runelite.watchdog.ui.panels.AlertPanel;

public class PlayerChatAlertPanel extends AlertPanel<PlayerChatAlert> {
    public PlayerChatAlertPanel(WatchdogPanel watchdogPanel, PlayerChatAlert alert) {
        super(watchdogPanel, alert);
    }

    @Override
    protected void build() {
        this.addAlertDefaults()
            .addRegexMatcher(this.alert, "Enter the message to trigger on...", "The message to trigger on. Supports glob (*)", MessagePickerButton.createPlayerChatPickerButton((selected) -> {
                this.alert.setPattern(selected);
                this.rebuild();
            }, this.alert::getPlayerChatType))
            .addSelect("Chat Type", "The type of message", PlayerChatType.class, this.alert.getPlayerChatType(), this.alert::setPlayerChatType)
            .addNotifications();
    }
}
