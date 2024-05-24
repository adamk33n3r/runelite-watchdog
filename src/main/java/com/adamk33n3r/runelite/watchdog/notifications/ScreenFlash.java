package com.adamk33n3r.runelite.watchdog.notifications;

import com.adamk33n3r.runelite.watchdog.WatchdogConfig;
import com.adamk33n3r.runelite.watchdog.WatchdogPlugin;
import com.adamk33n3r.runelite.watchdog.alerts.FlashMode;

import net.runelite.client.config.FlashNotification;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.inject.Inject;
import java.awt.Color;

import static com.adamk33n3r.runelite.watchdog.WatchdogConfig.DEFAULT_NOTIFICATION_COLOR;

@Getter
@Setter
@NoArgsConstructor
@Accessors(chain = true)
public class ScreenFlash extends Notification {
    private Color color = DEFAULT_NOTIFICATION_COLOR;
    private FlashMode flashMode = FlashMode.FLASH;
    private int flashDuration = 2;

    @Deprecated
    private FlashNotification flashNotification;

    @Inject
    public ScreenFlash(WatchdogConfig config) {
        super(config);
        this.color = config.defaultScreenFlashColor();
        this.flashMode = config.defaultScreenFlashMode();
        this.flashDuration = config.defaultScreenFlashDuration();
    }

    @Override
    protected void fireImpl(String[] triggerValues) {
        WatchdogPlugin.getInstance().getFlashOverlay().flash(this);
    }

    @Override
    public void setDefaults() {
        super.setDefaults();
        this.setFlashDuration(this.watchdogConfig.defaultScreenFlashDuration());
        this.setFlashMode(this.watchdogConfig.defaultScreenFlashMode());
        this.setColor(this.watchdogConfig.defaultScreenFlashColor());
    }
}
