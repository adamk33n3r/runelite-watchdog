package com.adamk33n3r.runelite.watchdog.alerts;

import com.adamk33n3r.runelite.watchdog.TriggerType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResourceAlert extends Alert {
    enum ResourceType {
        HP,
        PRAYER,
        RUN,
        SPECIAL,
    }

    private ResourceType resourceType = ResourceType.HP;
    private int value = 5;

    public ResourceAlert(String name) {
        super(name, TriggerType.RESOURCE);
    }
}
