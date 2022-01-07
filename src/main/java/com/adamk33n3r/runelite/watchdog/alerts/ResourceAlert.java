package com.adamk33n3r.runelite.watchdog.alerts;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResourceAlert extends Alert {
    public enum ResourceType {
        HITPOINTS,
        PRAYER,
        RUN,
        SPECIAL,
    }

    private ResourceType resourceType = ResourceType.HITPOINTS;
    private int value = 5;

    public ResourceAlert() {
        super("New Resource Alert");
    }

    public ResourceAlert(String name) {
        super(name);
    }
}
