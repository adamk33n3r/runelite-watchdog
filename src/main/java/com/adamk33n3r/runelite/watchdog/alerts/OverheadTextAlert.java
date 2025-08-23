package com.adamk33n3r.runelite.watchdog.alerts;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class OverheadTextAlert extends Alert implements RegexMatcher {
    private boolean regexEnabled = false;
    private String message = "";
    private boolean npcRegexEnabled = false;
    private String npcName = "";

    @Override
    public String getPattern() {
        return this.message;
    }

    @Override
    public void setPattern(String pattern) {
        this.message = pattern;
    }

    public OverheadTextAlert() {
        super("New Overhead Text Alert");
    }

    public OverheadTextAlert(String name) {
        super(name);
    }
}
