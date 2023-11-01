package com.adamk33n3r.runelite.watchdog.notifications;

import com.adamk33n3r.runelite.watchdog.WatchdogConfig;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.inject.Inject;

@NoArgsConstructor
public abstract class AudioNotification extends Notification implements IAudioNotification {
    @Getter
    @Setter
    protected int gain = 8;

    @Inject
    public AudioNotification(WatchdogConfig config) {
        super(config);
    }
}
