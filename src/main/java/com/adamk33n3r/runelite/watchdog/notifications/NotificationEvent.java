package com.adamk33n3r.runelite.watchdog.notifications;

import com.adamk33n3r.runelite.watchdog.EventHandler;
import com.adamk33n3r.runelite.watchdog.Util;
import com.adamk33n3r.runelite.watchdog.WatchdogConfig;

import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.inject.Inject;

@NoArgsConstructor
@Accessors(chain = true)
public class NotificationEvent extends MessageNotification {
    @Inject
    private transient EventHandler eventHandler;

    @Inject
    public NotificationEvent(WatchdogConfig config) {
        super(config);
    }

    @Override
    protected void fireImpl(String[] triggerValues) {
        this.eventHandler.notify(Util.processTriggerValues(this.message, triggerValues));
    }
}
