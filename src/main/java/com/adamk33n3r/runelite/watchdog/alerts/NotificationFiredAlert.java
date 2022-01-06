package com.adamk33n3r.runelite.watchdog.alerts;

import com.adamk33n3r.runelite.watchdog.TriggerType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NotificationFiredAlert extends Alert {
    private String message;

    public NotificationFiredAlert(String name) {
        super(name, TriggerType.NOTIFICATION_FIRED);
    }
}
