package com.adamk33n3r.runelite.watchdog.notifications;

import lombok.Getter;
import lombok.Setter;

public abstract class AudioNotification extends Notification implements IAudioNotification {
    @Getter
    @Setter
    protected int gain = 8;
}
