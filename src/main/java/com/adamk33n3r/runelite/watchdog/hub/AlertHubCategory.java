package com.adamk33n3r.runelite.watchdog.hub;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AlertHubCategory {
    COMBAT("Combat", "Combat"),
    SKILLING("Skilling", "Skilling"),
    BOSSES("Bosses", "Bosses"),
    DROPS("Drops", "Drops"),
    AFK("AFK", "AFK"),
    ;

    private final String name;
    private final String tooltip;
}
