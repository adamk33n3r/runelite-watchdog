package com.adamk33n3r.runelite.afkwarden.alerts;

public class IdleAlert extends Alert {
    enum IdleAction {
        Fishing,
        Combat,
        Mining,
        Logout,
    }
    private IdleAction idleAction;

    public IdleAlert(String name) {
        super(name);
    }
}
