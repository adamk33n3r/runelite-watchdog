package com.adamk33n3r.runelite.watchdog;

import com.adamk33n3r.runelite.watchdog.alerts.Alert;
import com.adamk33n3r.runelite.watchdog.notifications.Notification;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

public class AlertProcessor extends Thread {
    private final String[] triggerValues;
    private final boolean forceFire;
    private final Queue<Notification> notificationQueue = new LinkedList<>();

    public AlertProcessor(Alert alert, String[] triggerValues) {
        this(alert, triggerValues, false);
    }

    public AlertProcessor(Alert alert, String[] triggerValues, boolean forceFire) {
        this.triggerValues = triggerValues;
        this.forceFire = forceFire;
        if (alert.isRandomNotifications()) {
            this.notificationQueue.add(alert.getNotifications().get(new Random().nextInt(alert.getNotifications().size())));
        } else {
            this.notificationQueue.addAll(alert.getNotifications());
        }
    }

    @Override
    public void run() {
        while (!this.notificationQueue.isEmpty()) {
            Notification nextNotification = this.notificationQueue.poll();
            // This is checked in .fire(), but we don't want to delay if it won't fire
            if (!nextNotification.shouldFire() && !this.forceFire) {
                continue;
            }
            int delayMilliseconds = nextNotification.getDelayMilliseconds();
            if (delayMilliseconds > 0) {
                try {
                    Thread.sleep(delayMilliseconds);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }

            if (this.forceFire) {
                nextNotification.fireForced(this.triggerValues);
            } else {
                nextNotification.fire(this.triggerValues);
            }
        }
    }
}
