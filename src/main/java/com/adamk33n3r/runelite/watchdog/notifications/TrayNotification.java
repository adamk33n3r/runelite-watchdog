package com.adamk33n3r.runelite.watchdog.notifications;

import com.adamk33n3r.runelite.watchdog.TrayNotifier;
import com.adamk33n3r.runelite.watchdog.Util;
import com.adamk33n3r.runelite.watchdog.WatchdogConfig;

import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import java.awt.TrayIcon;

@Slf4j
@NoArgsConstructor
@Accessors(chain = true)
public class TrayNotification extends MessageNotification {
    @Inject
    protected transient TrayNotifier trayNotifier;

    @Inject
    public TrayNotification(WatchdogConfig config) {
        super(config);
    }

    @Override
    protected void fireImpl(String[] triggerValues) {
        this.trayNotifier.notify(
                "Watchdog",
                Util.processTriggerValues(this.message, triggerValues),
                TrayIcon.MessageType.NONE);
    }
}
