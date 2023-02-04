package com.adamk33n3r.runelite.watchdog;

public enum TriggerType {
    GAME_MESSAGE(),
    STAT_CHANGED(),
    XP_DROP(),
    SOUND_FIRED(),
    // Keep this last so that people maybe won't try to use it over the chat one
    NOTIFICATION_FIRED(),
}
