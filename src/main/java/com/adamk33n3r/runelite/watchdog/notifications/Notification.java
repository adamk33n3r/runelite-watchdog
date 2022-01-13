package com.adamk33n3r.runelite.watchdog.notifications;

import lombok.Getter;
import lombok.Setter;
import net.runelite.client.ui.ClientUI;

import javax.inject.Inject;

public abstract class Notification implements INotification {
    @Inject
    protected transient ClientUI clientUI;

    @Getter @Setter
    private boolean fireWhenFocused;

    protected boolean shouldFire() {
        return !this.clientUI.isFocused() || this.fireWhenFocused;
    }

    @Override
    public void fire() {
        if (this.shouldFire()) {
            this.fireImpl();
        }
    }
    protected abstract void fireImpl();
}
