package com.adamk33n3r.runelite.watchdog.alerts;

import lombok.Getter;
import lombok.Setter;
import net.runelite.api.Client;
import net.runelite.api.coords.WorldPoint;

import javax.inject.Inject;

@Getter
@Setter
public class LocationAlert extends Alert {
    private WorldPoint worldPoint = new WorldPoint(3223, 3219, 0);
    private int distance;
    private boolean repeat = false;
    private boolean cardinalOnly = false;
//    private boolean showTileMarker;

    @Inject
    private transient Client client;

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
        if (this.cardinalOnly && !this.isCardinal(this.worldPoint, currentPoint)) {
            return false;
        }
        return this.worldPoint.distanceTo(currentPoint) <= this.distance;
    }

    private boolean isCardinal(WorldPoint pointA, WorldPoint pointB) {
        return pointA.getX() == pointB.getX() || pointA.getY() == pointB.getY();
    }
}
