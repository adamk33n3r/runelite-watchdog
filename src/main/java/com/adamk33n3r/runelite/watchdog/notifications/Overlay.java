package com.adamk33n3r.runelite.watchdog.notifications;

import com.adamk33n3r.runelite.watchdog.Util;
import com.adamk33n3r.runelite.watchdog.WatchdogConfig;
import com.adamk33n3r.runelite.watchdog.WatchdogPlugin;

import lombok.Getter;
import lombok.Setter;

import javax.inject.Inject;
import java.awt.Color;

@Getter
@Setter
public class Overlay extends MessageNotification {
    private Color color;
    private Color textColor;
    private boolean sticky = false;
    private int timeToLive = 5;
    private String imagePath;

    @Inject
    public Overlay(WatchdogConfig config) {
        super(config);
        this.color = config.defaultOverlayColor();
        this.textColor = config.defaultOverlayTextColor();
        this.sticky = config.defaultOverlaySticky();
        this.timeToLive = config.defaultOverlayTTL();
        this.imagePath = config.defaultOverlayImagePath();
    }

    @Override
    protected void fireImpl(String[] triggerValues) {
        WatchdogPlugin.getInstance().getNotificationOverlay()
            .add(this, Util.processTriggerValues(this.message, triggerValues));
    }

    @Override
    public void setDefaults() {
        super.setDefaults();
        this.setColor(this.watchdogConfig.defaultOverlayColor());
        this.setTextColor(this.watchdogConfig.defaultOverlayTextColor());
        this.setSticky(this.watchdogConfig.defaultOverlaySticky());
        this.setTimeToLive(this.watchdogConfig.defaultOverlayTTL());
        this.setImagePath(this.watchdogConfig.defaultOverlayImagePath());
    }
}
