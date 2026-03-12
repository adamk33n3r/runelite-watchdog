package com.adamk33n3r.runelite.watchdog.notifications;

import com.adamk33n3r.runelite.watchdog.Displayable;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ShortestPathMode implements Displayable {
    PATH("Set Path", "Set Path"),
    CLEAR("Clear Path", "Clear Path"),
    ;

    private final String name;
    private final String tooltip;
}
