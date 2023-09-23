package com.adamk33n3r.runelite.watchdog.alerts;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlayerChatAlert extends Alert implements RegexMatcher {
    private String message = "";
    private boolean regexEnabled = false;

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
