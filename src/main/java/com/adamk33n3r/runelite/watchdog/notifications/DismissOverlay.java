package com.adamk33n3r.runelite.watchdog.notifications;

import com.adamk33n3r.runelite.watchdog.WatchdogPlugin;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter @Setter
@Accessors(chain = true)
public class DismissOverlay extends Notification {
    private String dismissId;

    @Override
    protected void fireImpl(String[] triggerValues) {
        WatchdogPlugin.getInstance().getNotificationOverlay().clearById(this.dismissId);
    }
}
