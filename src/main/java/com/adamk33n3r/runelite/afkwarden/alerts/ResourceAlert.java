package com.adamk33n3r.runelite.afkwarden.alerts;

public class ResourceAlert extends Alert {
    enum ResourceType {
        HP,
        PRAYER,
        RUN,
        SPECIAL,
    }

    private ResourceType resourceType;

    public ResourceAlert(String name) {
        super(name);
    }
}
