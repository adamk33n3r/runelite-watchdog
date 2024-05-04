package com.adamk33n3r.runelite.watchdog.alerts;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
public class NotificationFiredAlert extends Alert implements RegexMatcher {
    @Builder.Default
    private String message = "";
    @Builder.Default
    private boolean regexEnabled = false;

    @Override
    public String getPattern() {
        return this.message;
    }

    @Override
    public void setPattern(String pattern) {
        this.message = pattern;
    }

    public NotificationFiredAlert() {
        super("New Notification Fired Alert");
    }

    public NotificationFiredAlert(String name) {
        super(name);
    }
}
