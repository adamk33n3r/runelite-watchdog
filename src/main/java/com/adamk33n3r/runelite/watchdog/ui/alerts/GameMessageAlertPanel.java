package com.adamk33n3r.runelite.watchdog.ui.alerts;

import com.adamk33n3r.runelite.watchdog.GameMessageType;
import com.adamk33n3r.runelite.watchdog.alerts.ChatAlert;
import com.adamk33n3r.runelite.watchdog.ui.panels.AlertContentPanel;

public class GameMessageAlertPanel extends AlertContentPanel<ChatAlert> {

    public GameMessageAlertPanel(ChatAlert alert, Runnable onChange) {
        super(alert, onChange);
        this.init();
    }

    @Override
    public void buildTypeContent() {
        this.addRegexMatcher(this.alert, "Enter the message to trigger on...", "The message to trigger on. Supports glob (*)", MessagePickerButton.createGameMessagePickerButton(selected -> {
                this.alert.setPattern(selected);
                this.rebuild();
            }, this.alert::getGameMessageType))
            .addSelect("Chat Type", "The type of message", GameMessageType.class, this.alert.getGameMessageType(), this.alert::setGameMessageType);
    }
}
