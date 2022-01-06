package com.adamk33n3r.runelite.watchdog.alerts;

import com.adamk33n3r.runelite.watchdog.TriggerType;

public class IdleAlert extends Alert {
    enum IdleAction {
        Fishing,
        Combat,
        Mining,
        Logout,
    }
    private IdleAction idleAction;

    public IdleAlert(String name) {
        super(name, TriggerType.IDLE);
    }
}
