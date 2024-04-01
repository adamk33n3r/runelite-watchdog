package com.adamk33n3r.runelite.watchdog.notifications;

import com.adamk33n3r.runelite.watchdog.WatchdogConfig;
import com.adamk33n3r.runelite.watchdog.WatchdogPlugin;

import lombok.Getter;
import lombok.Setter;

import javax.inject.Inject;
import java.awt.Color;
import java.time.Instant;

public class ScreenMarker extends Notification {
    @Getter
    private final net.runelite.client.plugins.screenmarkers.ScreenMarker screenMarker;
    @Getter @Setter
    private int displayTime = 5;

    @Inject
    public ScreenMarker(WatchdogConfig config) {
        super(config);
        this.screenMarker = new net.runelite.client.plugins.screenmarkers.ScreenMarker(
            Instant.now().toEpochMilli(),
            "",
            3,
            Color.GREEN,
            new Color(0, 255, 0, 0),
            true,
            false
        );
    }

    @Override
    protected void fireImpl(String[] triggerValues) {
        WatchdogPlugin.getInstance().getScreenMarkerUtil().addScreenMarker(this);
    }
}
