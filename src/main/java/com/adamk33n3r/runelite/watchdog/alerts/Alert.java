package com.adamk33n3r.runelite.watchdog.alerts;

import com.adamk33n3r.runelite.watchdog.TriggerType;
import com.adamk33n3r.runelite.watchdog.notifications.Notification;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
public abstract class Alert {
    @Setter
    private boolean enabled = true;

    @Setter
    private String name;

    @Setter
    private int debounceTime;

    private final List<Notification> notifications = new ArrayList<>();

    public Alert(String name) {
        this.name = name;
        this.debounceTime = 0;
    }

    public TriggerType getType() {
        return Arrays.stream(TriggerType.values())
            .filter(tType -> tType.getImplClass() == this.getClass())
            .findFirst()
            .orElse(null);
    }

    public void moveNotificationTo(Notification notification, int pos) {
        this.notifications.remove(notification);
        this.notifications.add(pos, notification);
    }

    public void moveNotificationToTop(Notification notification) {
        this.notifications.remove(notification);
        this.notifications.add(0, notification);
    }

    public void moveNotificationToBottom(Notification notification) {
        this.notifications.remove(notification);
        this.notifications.add(notification);
    }

    public void moveNotificationUp(Notification notification) {
        int curIdx = this.notifications.indexOf(notification);
        int newIdx = curIdx - 1;

        if (newIdx < 0) {
            return;
        }

        this.notifications.remove(notification);
        this.notifications.add(newIdx, notification);
    }

    public void moveNotificationDown(Notification notification) {
        int curIdx = this.notifications.indexOf(notification);
        int newIdx = curIdx + 1;

        if (newIdx >= this.notifications.size()) {
            return;
        }

        this.notifications.remove(notification);
        this.notifications.add(newIdx, notification);
    }
}
