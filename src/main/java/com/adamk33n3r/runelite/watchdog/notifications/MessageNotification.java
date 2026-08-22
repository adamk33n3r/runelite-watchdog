package com.adamk33n3r.runelite.watchdog.notifications;

import com.adamk33n3r.runelite.watchdog.WatchdogConfig;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.inject.Inject;

@Setter
@Getter
@Accessors(chain = true)
@NoArgsConstructor
public abstract class MessageNotification extends Notification implements IMessageNotification {
    protected String message = "";

    @Inject
    public MessageNotification(WatchdogConfig config) {
        super(config);
    }

    public void fireWithMessage(String[] triggerValues, String message) {
        if (this.shouldFire()) {
            this.fireImpl(triggerValues, message);
        }
    }

    public void fireForcedWithMessage(String[] triggerValues, String message) {
        this.fireImpl(triggerValues, message);
    }

    @Override
    protected final void fireImpl(String[] triggerValues) {
        this.fireImpl(triggerValues, this.message);
    }

    /**
     * Implementations must run {@code message} through
     * {@link com.adamk33n3r.runelite.watchdog.Util#processTriggerValues(String, String[])} — nothing
     * else applies the $1 capture-group substitutions.
     */
    protected abstract void fireImpl(String[] triggerValues, String message);
}
