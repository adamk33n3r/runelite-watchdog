package com.adamk33n3r.runelite.afkwarden.notifications;

import com.adamk33n3r.runelite.afkwarden.AFKWardenPlugin;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Sound implements INotification {
    // sound file
    @Override
    public void fire(AFKWardenPlugin plugin) {
        log.info("Fire Sound");
    }
}
