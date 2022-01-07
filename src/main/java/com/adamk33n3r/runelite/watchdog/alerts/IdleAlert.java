package com.adamk33n3r.runelite.watchdog.alerts;

import lombok.Getter;
import lombok.Setter;

public class IdleAlert extends Alert {
    public enum IdleAction {
        FISHING,
        COMBAT,
        MINING,
        LOGOUT,
    }
    @Getter
    @Setter
    private IdleAction idleAction = IdleAction.MINING;

    public IdleAlert() {
        super("New Idle Alert");
    }

    public IdleAlert(String name) {
        super(name);
    }
}
