package com.adamk33n3r.runelite.watchdog.notifications;

import com.adamk33n3r.runelite.watchdog.NotificationType;

import net.runelite.client.ui.ClientUI;

import lombok.Getter;
import lombok.Setter;

import javax.inject.Inject;
import java.util.Arrays;

public abstract class Notification implements INotification {
    @Inject
    protected transient ClientUI clientUI;

    @Getter @Setter
    private boolean fireWhenFocused = true;

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

    public NotificationType getType() {
        return Arrays.stream(NotificationType.values())
            .filter(nType -> nType.getImplClass() == this.getClass())
            .findFirst()
            .orElse(null);
    }
}
