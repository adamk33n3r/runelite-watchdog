package com.adamk33n3r.nodegraph.nodes.logic;

import com.adamk33n3r.nodegraph.Node;
import com.adamk33n3r.nodegraph.VarInput;
import com.adamk33n3r.nodegraph.VarOutput;
import lombok.Getter;
import lombok.Setter;
import net.runelite.api.coords.WorldPoint;

@Getter
@Setter
public class LocationCompare extends Node {
    private final VarInput<WorldPoint> a = new VarInput<>(this, "A", WorldPoint.class, new WorldPoint(0, 0, 0));
    private final VarInput<WorldPoint> b = new VarInput<>(this, "B", WorldPoint.class, new WorldPoint(0, 0, 0));
    private final VarOutput<Boolean> result = new VarOutput<>(this, "Result", Boolean.class, false);
    private int distance = 0;
    private boolean cardinalOnly = false;

    public LocationCompare() {
        this.a.onChange(val -> this.process());
        this.b.onChange(val -> this.process());

        reg(this.a);
        reg(this.b);
        reg(this.result);
    }

    @Override
    public void process() {
        WorldPoint pointA = this.a.getValue();
        WorldPoint pointB = this.b.getValue();
        if (pointA == null || pointB == null) {
            this.result.setValue(false);
            return;
        }
        if (this.cardinalOnly && pointA.getX() != pointB.getX() && pointA.getY() != pointB.getY()) {
            this.result.setValue(false);
            return;
        }
        this.result.setValue(pointA.distanceTo(pointB) <= this.distance);
    }
}
