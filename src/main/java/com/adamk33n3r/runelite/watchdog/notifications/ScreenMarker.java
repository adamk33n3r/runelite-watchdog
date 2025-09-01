package com.adamk33n3r.runelite.watchdog.notifications;

import com.adamk33n3r.runelite.watchdog.WatchdogConfig;
import com.adamk33n3r.runelite.watchdog.WatchdogPlugin;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.inject.Inject;
import java.awt.Color;
import java.time.Instant;

@Getter
@Accessors(chain = true)
public class ScreenMarker extends Notification {
    private net.runelite.client.plugins.screenmarkers.ScreenMarker screenMarker;
    @Setter
    private int displayTime = 5;
    @Setter
    private boolean sticky = false;
    @Setter
    private String id;

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
        this.setDefaults();
    }

    public ScreenMarker setScreenMarkerProperties(String name, Color color, Color fill, int borderThickness) {
        this.screenMarker.setId(Instant.now().toEpochMilli());
        this.screenMarker.setName(name);
        this.screenMarker.setBorderThickness(borderThickness);
        this.screenMarker.setColor(color);
        this.screenMarker.setFill(fill);
        this.screenMarker.setLabelled(name != null && !name.isEmpty());
        return this;
    }

    @Override
    protected void fireImpl(String[] triggerValues) {
        WatchdogPlugin.getInstance().getScreenMarkerUtil().addScreenMarker(this);
    }

    @Override
    public void setDefaults() {
        super.setDefaults();

        this.screenMarker.setColor(this.watchdogConfig.defaultScreenMarkerBorderColor());
        this.screenMarker.setFill(this.watchdogConfig.defaultScreenMarkerFillColor());
        this.screenMarker.setBorderThickness(this.watchdogConfig.defaultScreenMarkerBorderThickness());
        this.displayTime = this.watchdogConfig.defaultScreenMarkerDisplayTime();
        this.sticky = this.watchdogConfig.defaultScreenMarkerSticky();
    }
}
