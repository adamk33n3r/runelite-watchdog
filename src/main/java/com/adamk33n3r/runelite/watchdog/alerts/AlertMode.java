package com.adamk33n3r.runelite.watchdog.alerts;

import com.adamk33n3r.runelite.watchdog.Displayable;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AlertMode implements Displayable {
    QUEUE("Queue", "Allow multiple instances to run simultaneously"),
    RESTART("Restart", "Cancel any running instances before starting a new one"),
    ;

    private final String name;
    private final String tooltip;
}
