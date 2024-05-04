package com.adamk33n3r.runelite.watchdog.alerts;

import com.adamk33n3r.runelite.watchdog.GameMessageType;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
public class ChatAlert extends Alert implements RegexMatcher {
    @Builder.Default
    private String message = "";
    @Builder.Default
    private boolean regexEnabled = false;
    @Builder.Default
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
