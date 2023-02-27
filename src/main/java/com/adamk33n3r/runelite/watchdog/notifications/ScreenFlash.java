package com.adamk33n3r.runelite.watchdog.notifications;

import com.adamk33n3r.runelite.watchdog.WatchdogConfig;
import com.adamk33n3r.runelite.watchdog.WatchdogPlugin;

import net.runelite.client.config.FlashNotification;

import lombok.Getter;
import lombok.Setter;

import javax.inject.Inject;
import java.awt.Color;

@Getter
@Setter
public class ScreenFlash extends Notification {
    private Color color;
    private FlashNotification flashNotification;

    @Inject
    public ScreenFlash(WatchdogConfig config) {
        this.color = config.defaultScreenFlashColor();
        this.flashNotification = config.defaultScreenFlashType();
    }

    @Override
    protected void fireImpl(String[] triggerValues) {
        WatchdogPlugin.getInstance().getFlashOverlay().flash(this);
    }
}
