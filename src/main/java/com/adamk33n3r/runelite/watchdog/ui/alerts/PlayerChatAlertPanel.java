package com.adamk33n3r.runelite.watchdog.ui.alerts;

import com.adamk33n3r.runelite.watchdog.PlayerChatType;
import com.adamk33n3r.runelite.watchdog.alerts.PlayerChatAlert;
import com.adamk33n3r.runelite.watchdog.ui.panels.AlertContentPanel;

public class PlayerChatAlertPanel extends AlertContentPanel<PlayerChatAlert> {

    public PlayerChatAlertPanel(PlayerChatAlert alert, Runnable onChange) {
        super(alert, onChange);
        this.init();
    }

    @Override
    public void buildTypeContent() {
        this.addRegexMatcher(this.alert, "Enter the message to trigger on...", "The message to trigger on. Supports glob (*)", MessagePickerButton.createPlayerChatPickerButton(selected -> {
                this.alert.setPattern(selected);
                this.rebuild();
            }, this.alert::getPlayerChatType))
            .addSelect("Chat Type", "The type of message", PlayerChatType.class, this.alert.getPlayerChatType(), (type) -> {
                this.alert.setPlayerChatType(type);
                this.rebuild();
            })
            .addIf(p -> {
                p.addSelect("Chat Direction", "The direction of the chat", PlayerChatAlert.ChatDirection.class, this.alert.getChatDirection(), this.alert::setChatDirection);
            }, () -> this.alert.getPlayerChatType() == PlayerChatType.PRIVATE)
            .addCheckbox("Prepend Name", "Prepend the name to the message in the form of '{name}: {message}'. Affects pattern matching", this.alert.isPrependSender(), this.alert::setPrependSender);
    }
}
