package com.adamk33n3r.runelite.watchdog.notifications;

import com.adamk33n3r.runelite.watchdog.EventHandler;
import com.adamk33n3r.runelite.watchdog.Util;

import javax.inject.Inject;

public class NotificationEvent extends MessageNotification {
    @Inject
    private transient EventHandler eventHandler;

    @Override
    protected void fireImpl(String[] triggerValues) {
        this.eventHandler.notify(Util.processTriggerValues(this.message, triggerValues));
    }
}
