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
    public void fire(String[] triggerValues) {
        if (this.shouldFire()) {
            this.fireImpl(triggerValues);
        }
    }

    public void fireForced(String[] triggerValues) {
        this.fireImpl(triggerValues);
    }

    protected abstract void fireImpl(String[] triggerValues);
}
