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
}
