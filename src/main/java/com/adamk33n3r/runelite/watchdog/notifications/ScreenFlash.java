package com.adamk33n3r.runelite.watchdog.notifications;

import com.adamk33n3r.runelite.watchdog.WatchdogConfig;
import com.adamk33n3r.runelite.watchdog.WatchdogPlugin;
import com.adamk33n3r.runelite.watchdog.alerts.FlashMode;

import net.runelite.client.config.FlashNotification;

import lombok.Getter;
import lombok.Setter;

import javax.inject.Inject;
import java.awt.Color;

@Getter
@Setter
public class ScreenFlash extends Notification {
    private Color color;
    private FlashMode flashMode;
    private int flashDuration;

    @Deprecated
    private FlashNotification flashNotification;

    @Inject
    public ScreenFlash(WatchdogConfig config) {
        this.color = config.defaultScreenFlashColor();
        this.flashMode = config.defaultScreenFlashMode();
        this.flashDuration = config.defaultScreenFlashDuration();
    }

    @Override
    protected void fireImpl(String[] triggerValues) {
        WatchdogPlugin.getInstance().getFlashOverlay().flash(this);
    }
}
