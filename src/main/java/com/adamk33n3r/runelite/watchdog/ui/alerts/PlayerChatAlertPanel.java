package com.adamk33n3r.runelite.watchdog.ui.alerts;

import com.adamk33n3r.runelite.watchdog.PlayerChatType;
import com.adamk33n3r.runelite.watchdog.WatchdogPanel;
import com.adamk33n3r.runelite.watchdog.alerts.PlayerChatAlert;
import com.adamk33n3r.runelite.watchdog.ui.panels.AlertContentBuilder;
import com.adamk33n3r.runelite.watchdog.ui.panels.AlertPanel;

public class PlayerChatAlertPanel extends AlertPanel<PlayerChatAlert> {
    public PlayerChatAlertPanel(WatchdogPanel watchdogPanel, PlayerChatAlert alert) {
        super(watchdogPanel, alert);
    }

    @Override
    protected void build() {
        this.addAlertDefaults();
        buildTypeContent(this.alert, new AlertContentBuilder(this.getControlContainer(), this.getSaveAction(), this::rebuild));
        this.addNotifications();
    }

    public static void buildTypeContent(PlayerChatAlert alert, AlertContentBuilder builder) {
        builder
            .addRegexMatcher(alert, "Enter the message to trigger on...", "The message to trigger on. Supports glob (*)", MessagePickerButton.createPlayerChatPickerButton((selected) -> {
                alert.setPattern(selected);
                builder.rebuild();
            }, alert::getPlayerChatType))
            .addSelect("Chat Type", "The type of message", PlayerChatType.class, alert.getPlayerChatType(), alert::setPlayerChatType)
            .addCheckbox("Prepend Sender", "Prepend the sender's name to the message in the form of '{name}: {message}'. Affects pattern matching", alert.isPrependSender(), alert::setPrependSender);
    }
}
