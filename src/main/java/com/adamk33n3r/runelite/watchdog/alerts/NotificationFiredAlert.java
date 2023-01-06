package com.adamk33n3r.runelite.watchdog.alerts;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NotificationFiredAlert extends Alert {
    private String message;
    private boolean regexEnabled = false;

    public NotificationFiredAlert() {
        super("New Notification Fired Alert");
    }

    public NotificationFiredAlert(String name) {
        super(name);
    }
}
