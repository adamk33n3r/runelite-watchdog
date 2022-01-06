package com.adamk33n3r.runelite.watchdog.notifications;

import com.adamk33n3r.runelite.watchdog.WatchdogPlugin;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TrayNotification extends NotificationWithMessage {
    @Override
    public void fire(WatchdogPlugin plugin) {
        log.info("Fire TrayNotification");
    }
}
