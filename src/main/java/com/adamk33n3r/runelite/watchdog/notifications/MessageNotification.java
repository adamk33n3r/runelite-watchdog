package com.adamk33n3r.runelite.watchdog.notifications;

import lombok.Getter;
import lombok.Setter;

public abstract class MessageNotification extends Notification implements IMessageNotification {
    @Getter
    @Setter
    protected String message = "Hey! Wake up!";
}
