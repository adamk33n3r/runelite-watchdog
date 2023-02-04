package com.adamk33n3r.runelite.watchdog;

import com.adamk33n3r.runelite.watchdog.alerts.Alert;
import com.adamk33n3r.runelite.watchdog.alerts.ChatAlert;
import com.adamk33n3r.runelite.watchdog.alerts.NotificationFiredAlert;
import com.adamk33n3r.runelite.watchdog.alerts.SoundFiredAlert;
import com.adamk33n3r.runelite.watchdog.alerts.StatChangedAlert;
import com.adamk33n3r.runelite.watchdog.alerts.XPDropAlert;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TriggerType {
    GAME_MESSAGE("Game Message", "Game messages sent in chat", ChatAlert.class),
    STAT_CHANGED("Stat Changed", "Stat changes like boosts or drains", StatChangedAlert.class),
    XP_DROP("XP Drop", "Get an xp drop", XPDropAlert.class),
    SOUND_FIRED("Sound Fired", "When a sound effect plays", SoundFiredAlert.class),
    // Keep this last so that people maybe won't try to use it over the chat one
    NOTIFICATION_FIRED("Notification Fired", "When other plugins fire notifications", NotificationFiredAlert.class),
    ;

    private final String name;
    private final String tooltip;
    private final Class<? extends Alert> implClass;
}
