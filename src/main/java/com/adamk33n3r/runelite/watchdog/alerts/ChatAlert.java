package com.adamk33n3r.runelite.watchdog.alerts;

import com.adamk33n3r.runelite.watchdog.GameMessageType;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class ChatAlert extends Alert implements RegexMatcher {
    private String message = "";
    private boolean regexEnabled = false;
    private GameMessageType gameMessageType = GameMessageType.ANY;

    @Override
    public String getPattern() {
        return this.message;
    }

    @Override
    public void setPattern(String pattern) {
        this.message = pattern;
    }

    public ChatAlert() {
        super("New Game Message Alert");
    }

    public ChatAlert(String name) {
        super(name);
    }
}
