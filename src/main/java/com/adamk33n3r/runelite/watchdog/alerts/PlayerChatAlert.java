package com.adamk33n3r.runelite.watchdog.alerts;

import com.adamk33n3r.runelite.watchdog.PlayerChatType;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class PlayerChatAlert extends Alert implements RegexMatcher {
    private String message = "";
    private boolean regexEnabled = false;
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
