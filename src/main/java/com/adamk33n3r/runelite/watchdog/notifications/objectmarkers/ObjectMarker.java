package com.adamk33n3r.runelite.watchdog.notifications.objectmarkers;

import com.adamk33n3r.runelite.watchdog.WatchdogConfig;
import com.adamk33n3r.runelite.watchdog.notifications.Notification;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.runelite.api.ObjectComposition;
import net.runelite.api.TileObject;

import javax.inject.Inject;
import java.awt.*;

@Getter
@Setter
@Accessors(chain = true)
@NoArgsConstructor
public class ObjectMarker extends Notification {
    static final int HF_HULL = 0x1;
    static final int HF_OUTLINE = 0x2;
    static final int HF_CLICKBOX = 0x4;
    static final int HF_TILE = 0x8;

    private ObjectPoint objectPoint;
    private Color borderColor = Color.YELLOW;
    private Color fillColor;
    private int displayTime = 5;
    private boolean sticky = false;
    private String id;
    // highlight options
    private boolean hull = true;
    private boolean outline;
    private boolean clickbox;
    private boolean tile;
    private double borderWidth = 2.0d;
    private int outlineFeather;

    @Inject
    private transient ObjectMarkerManager objectMarkerManager;

    private transient TileObject tileObject;
    private transient ObjectComposition composition;

    @Inject
    public ObjectMarker(WatchdogConfig config) {
        super(config);
        this.setDefaults();
    }

    @Override
    protected void fireImpl(String[] triggerValues) {
        if (this.objectPoint == null) {
            return;
        }
        this.objectMarkerManager.showObjectMarker(this);
    }

    @Override
    public void setDefaults() {
        super.setDefaults();

        this.borderColor = this.watchdogConfig.defaultObjectMarkerBorderColor();
        this.fillColor = this.watchdogConfig.defaultObjectMarkerFillColor();
        this.hull = this.watchdogConfig.defaultObjectMarkerHull();
        this.outline = this.watchdogConfig.defaultObjectMarkerOutline();
        this.clickbox = this.watchdogConfig.defaultObjectMarkerClickbox();
        this.tile = this.watchdogConfig.defaultObjectMarkerTile();
        this.borderWidth = this.watchdogConfig.defaultObjectMarkerBorderThickness();
        this.outlineFeather = this.watchdogConfig.defaultObjectMarkerFeather();
        this.displayTime = this.watchdogConfig.defaultObjectMarkerDisplayTime();
        this.sticky = this.watchdogConfig.defaultObjectMarkerSticky();
    }

    public byte getFlags() {
        return (byte)((this.hull ? HF_HULL : 0) |
            (this.outline ? HF_OUTLINE : 0) |
            (this.clickbox ? HF_CLICKBOX : 0) |
            (this.tile ? HF_TILE : 0));
    }

    public String toString() {
        return "ObjectMarker{" +
            "objectPoint=" + objectPoint +
            '}';
    }
}
