package com.adamk33n3r.runelite.watchdog;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum NotificationCategory implements Displayable {
    AUDIO("Audio", "Sound, text to speech, etc"),
    TEXT("Text", "Game message, overhead, etc"), // Game message, overhead, tray
    OVERLAY("Overlay", "Screen marker, flash, popup, etc"), // Overlay, popup, screen flash, screen marker
    ADVANCED("Advanced", "Dismissals, notification event, plugin message, etc"), // Dismissals, request focus, notification event, plugin message
    ;

    private final String name;
    private final String tooltip;
}
