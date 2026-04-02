package com.adamk33n3r.runelite.watchdog.ui.alerts;

import com.adamk33n3r.runelite.watchdog.GameMessageType;
import com.adamk33n3r.runelite.watchdog.WatchdogPanel;
import com.adamk33n3r.runelite.watchdog.alerts.ChatAlert;
import com.adamk33n3r.runelite.watchdog.ui.panels.AlertContentBuilder;
import com.adamk33n3r.runelite.watchdog.ui.panels.AlertPanel;

public class GameMessageAlertPanel extends AlertPanel<ChatAlert> {
    public GameMessageAlertPanel(WatchdogPanel watchdogPanel, ChatAlert alert) {
        super(watchdogPanel, alert);
    }

    @Override
    protected void build() {
        this.addAlertDefaults();
        buildTypeContent(this.alert, new AlertContentBuilder(this.getControlContainer(), this.getSaveAction(), this::rebuild));
        this.addNotifications();
    }

    public static void buildTypeContent(ChatAlert alert, AlertContentBuilder builder) {
        builder
            .addRegexMatcher(alert, "Enter the message to trigger on...", "The message to trigger on. Supports glob (*)", MessagePickerButton.createGameMessagePickerButton((selected) -> {
                alert.setPattern(selected);
                builder.rebuild();
            }, alert::getGameMessageType))
            .addSelect("Chat Type", "The type of message", GameMessageType.class, alert.getGameMessageType(), alert::setGameMessageType);
    }
}
