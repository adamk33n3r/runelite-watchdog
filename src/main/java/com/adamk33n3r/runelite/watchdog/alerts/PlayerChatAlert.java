package com.adamk33n3r.runelite.watchdog.alerts;

import com.adamk33n3r.runelite.watchdog.PlayerChatType;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
public class PlayerChatAlert extends Alert implements RegexMatcher {
    @Builder.Default
    private String message = "";
    @Builder.Default
    private boolean regexEnabled = false;
    @Builder.Default
    private PlayerChatType playerChatType = PlayerChatType.ANY;

    @Override
    public String getPattern() {
        return this.message;
    }

    @Override
    public void setPattern(String pattern) {
        this.message = pattern;
    }

    public PlayerChatAlert() {
        super("New Player Chat Message Alert");
    }

    public PlayerChatAlert(String name) {
        super(name);
    }
}
