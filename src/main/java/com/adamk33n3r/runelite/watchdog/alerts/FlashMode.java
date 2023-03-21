package com.adamk33n3r.runelite.watchdog.alerts;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum FlashMode {
    FLASH("Flash", "Will flash the screen over an interval"),
    SOLID("Solid", "Will be a solid overlay on the screen")
    ;

    private final String name;
    private final String tooltip;

    @Override
    public String toString() {
        return this.name;
    }
}
