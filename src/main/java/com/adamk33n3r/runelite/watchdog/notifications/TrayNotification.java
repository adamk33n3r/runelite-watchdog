package com.adamk33n3r.runelite.watchdog.notifications;

import com.adamk33n3r.runelite.watchdog.Util;
import com.adamk33n3r.runelite.watchdog.WatchdogConfig;

import net.runelite.client.Notifier;
import net.runelite.client.config.FlashNotification;
import net.runelite.client.config.NotificationSound;
import net.runelite.client.config.RequestFocusType;

import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.inject.Inject;

@NoArgsConstructor
@Accessors(chain = true)
public class TrayNotification extends MessageNotification {
    @Inject
    protected transient Notifier notifier;

    @Inject
    public TrayNotification(WatchdogConfig config) {
        super(config);
    }

    @Override
    protected void fireImpl(String[] triggerValues) {
        this.notifier.notify(
            new net.runelite.client.config.Notification()
                .withEnabled(true)
                .withInitialized(true)
                .withOverride(true)
                .withTray(true)
                .withRequestFocus(RequestFocusType.OFF)
                .withSound(NotificationSound.OFF)
                .withFlash(FlashNotification.DISABLED)
                .withSendWhenFocused(true),
            Util.processTriggerValues(this.message, triggerValues)
        );
    }
}
