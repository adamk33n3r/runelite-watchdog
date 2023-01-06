package com.adamk33n3r.runelite.watchdog;

public enum TriggerType {
    GAME_MESSAGE(),
    IDLE(),
    RESOURCE(),
    STAT_DRAIN(),
    SOUND_FIRED(),
    // Keep this last so that people maybe won't try to use it over the chat one
    NOTIFICATION_FIRED(),
}
