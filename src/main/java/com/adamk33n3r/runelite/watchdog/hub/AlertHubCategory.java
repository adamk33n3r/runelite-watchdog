package com.adamk33n3r.runelite.watchdog.hub;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AlertHubCategory {
    COMBAT("Combat", "Combat", "/skill_icons_small/combat.png"),
    SKILLING("Skilling", "Skilling", "/skill_icons_small/mining.png"),
    BOSSES("Bosses", "Bosses", "/skill_icons_small/slayer.png"),
    AFK("AFK", "AFK", "/skill_icons_small/fishing.png"),
    MISC("Misc", "Misc", "/net/runelite/client/plugins/hiscore/activities/clue_scroll_all.png")
    ;

    private final String name;
    private final String tooltip;
    private final String icon;
}
