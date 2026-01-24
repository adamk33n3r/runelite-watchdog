package com.adamk33n3r.runelite.watchdog.alerts;

import com.adamk33n3r.runelite.watchdog.Displayable;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum StatChangedMode implements Displayable {
    RELATIVE("Relative", "Relative to the player's current level. Negative values will be treated as a drain, positive values as a boost"),
    ABSOLUTE("Absolute", "Absolute level"),
    PERCENTAGE("Percentage", "Percentage of the player's current level"),
    ;

    private final String name;
    private final String tooltip;
}
