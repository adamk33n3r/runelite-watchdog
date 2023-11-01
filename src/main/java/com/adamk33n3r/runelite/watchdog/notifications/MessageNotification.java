package com.adamk33n3r.runelite.watchdog.notifications;

import com.adamk33n3r.runelite.watchdog.WatchdogConfig;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.inject.Inject;

@NoArgsConstructor
public abstract class MessageNotification extends Notification implements IMessageNotification {
    @Getter
    @Setter
    protected String message = "";

    @Inject
    public MessageNotification(WatchdogConfig config) {
        super(config);
    }
}
