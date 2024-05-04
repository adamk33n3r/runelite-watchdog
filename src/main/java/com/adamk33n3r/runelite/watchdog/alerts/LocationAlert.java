package com.adamk33n3r.runelite.watchdog.alerts;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import net.runelite.api.coords.WorldPoint;

@Getter
@Setter
@SuperBuilder
public class LocationAlert extends Alert {
    @Builder.Default
    private WorldPoint worldPoint = new WorldPoint(3223, 3219, 0);
    private int distance;
    @Builder.Default
    private boolean repeat = false;
//    private boolean showTileMarker;

    public LocationAlert() {
        super("New Location Alert");
    }
    public LocationAlert(String name) {
        super(name);
    }

    public boolean shouldFire(WorldPoint currentPoint) {
        if (this.worldPoint == null) {
            return false;
        }
        return this.worldPoint.distanceTo(currentPoint) <= this.distance;
    }
}
