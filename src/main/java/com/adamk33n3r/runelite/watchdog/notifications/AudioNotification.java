package com.adamk33n3r.runelite.watchdog.notifications;

import com.adamk33n3r.runelite.watchdog.WatchdogConfig;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.inject.Inject;

@Getter
@Setter
@NoArgsConstructor
@Accessors(chain = true)
public abstract class AudioNotification extends Notification implements IAudioNotification {
    protected int gain = 8;

    @Inject
    public AudioNotification(WatchdogConfig config) {
        super(config);
    }
}
