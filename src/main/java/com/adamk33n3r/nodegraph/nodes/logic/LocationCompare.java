package com.adamk33n3r.nodegraph.nodes.logic;

import com.adamk33n3r.nodegraph.Node;
import com.adamk33n3r.nodegraph.VarInput;
import com.adamk33n3r.nodegraph.VarOutput;
import lombok.Getter;
import net.runelite.api.coords.WorldPoint;

@Getter
public class LocationCompare extends Node {
    private final VarInput<WorldPoint> a = new VarInput<>(this, "A", WorldPoint.class, new WorldPoint(0, 0, 0));
    private final VarInput<WorldPoint> b = new VarInput<>(this, "B", WorldPoint.class, new WorldPoint(0, 0, 0));
    private final VarOutput<Boolean> result = new VarOutput<>(this, "Result", Boolean.class, false);
    private final VarInput<Number> distance = new VarInput<>(this, "Distance", Number.class, 0);
    private final VarInput<Boolean> cardinalOnly = new VarInput<>(this, "Cardinal Only", Boolean.class, false);

    public LocationCompare() {
        this.a.onChange(val -> this.process());
        this.b.onChange(val -> this.process());
        // Config vars self-process so they behave consistently with a and b
        this.distance.onChange(val -> this.process());
        this.cardinalOnly.onChange(val -> this.process());

        reg(this.a);
        reg(this.b);
        reg(this.distance);
        reg(this.cardinalOnly);
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
        if (this.cardinalOnly.getValue() && pointA.getX() != pointB.getX() && pointA.getY() != pointB.getY()) {
            this.result.setValue(false);
            return;
        }
        this.result.setValue(pointA.distanceTo(pointB) <= this.distance.getValue().intValue());
    }
}
