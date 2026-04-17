package com.adamk33n3r.runelite.watchdog.nodegraph;

import com.adamk33n3r.nodegraph.nodes.logic.LocationCompare;
import net.runelite.api.coords.WorldPoint;
import org.junit.Test;

import static org.junit.Assert.*;

public class LocationCompareProcessTest {

    @Test
    public void nullA_resultFalse() {
        LocationCompare lc = new LocationCompare();
        lc.getA().setValue(null);
        lc.getB().setValue(new WorldPoint(3200, 3200, 0));
        lc.setDistance(100);
        lc.process();
        assertFalse(lc.getResult().getValue());
    }

    @Test
    public void nullB_resultFalse() {
        LocationCompare lc = new LocationCompare();
        lc.getA().setValue(new WorldPoint(3200, 3200, 0));
        lc.getB().setValue(null);
        lc.setDistance(100);
        lc.process();
        assertFalse(lc.getResult().getValue());
    }

    @Test
    public void cardinalOnly_sameYDifferentX_withinDistance_isTrue() {
        LocationCompare lc = new LocationCompare();
        lc.getA().setValue(new WorldPoint(3200, 3200, 0));
        lc.getB().setValue(new WorldPoint(3203, 3200, 0));
        lc.setDistance(5);
        lc.setCardinalOnly(true);
        lc.process();
        assertTrue(lc.getResult().getValue());
    }

    @Test
    public void cardinalOnly_sameYDifferentX_beyondDistance_isFalse() {
        LocationCompare lc = new LocationCompare();
        lc.getA().setValue(new WorldPoint(3200, 3200, 0));
        lc.getB().setValue(new WorldPoint(3210, 3200, 0));
        lc.setDistance(5);
        lc.setCardinalOnly(true);
        lc.process();
        assertFalse(lc.getResult().getValue());
    }

    @Test
    public void cardinalOnly_sameXDifferentY_withinDistance_isTrue() {
        LocationCompare lc = new LocationCompare();
        lc.getA().setValue(new WorldPoint(3200, 3200, 0));
        lc.getB().setValue(new WorldPoint(3200, 3204, 0));
        lc.setDistance(5);
        lc.setCardinalOnly(true);
        lc.process();
        assertTrue(lc.getResult().getValue());
    }

    @Test
    public void nonCardinal_withinDistance_isTrue() {
        LocationCompare lc = new LocationCompare();
        lc.getA().setValue(new WorldPoint(3200, 3200, 0));
        lc.getB().setValue(new WorldPoint(3202, 3202, 0));
        lc.setDistance(5);
        lc.setCardinalOnly(false);
        lc.process();
        assertTrue(lc.getResult().getValue());
    }

    @Test
    public void nonCardinal_beyondDistance_isFalse() {
        LocationCompare lc = new LocationCompare();
        lc.getA().setValue(new WorldPoint(3200, 3200, 0));
        lc.getB().setValue(new WorldPoint(3210, 3210, 0));
        lc.setDistance(5);
        lc.setCardinalOnly(false);
        lc.process();
        assertFalse(lc.getResult().getValue());
    }
}
