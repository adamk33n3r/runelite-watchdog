package com.adamk33n3r.runelite.watchdog.notifications;

import com.adamk33n3r.runelite.watchdog.WatchdogPlugin;

public interface INotification {
    void fire(WatchdogPlugin plugin);
}
