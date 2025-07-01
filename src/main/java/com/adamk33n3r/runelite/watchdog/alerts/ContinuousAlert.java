package com.adamk33n3r.runelite.watchdog.alerts;

public abstract class ContinuousAlert extends Alert {
    public ContinuousAlert(String name) {
        super(name);
    }
    public abstract boolean shouldFire();
}
