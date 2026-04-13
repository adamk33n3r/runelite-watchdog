package com.adamk33n3r.nodegraph.nodes.constants;

import com.adamk33n3r.nodegraph.VarOutput;
import com.adamk33n3r.nodegraph.nodes.VariableNode;
import lombok.Getter;
import net.runelite.api.coords.WorldPoint;

@Getter
public class Location extends VariableNode {
    private final VarOutput<WorldPoint> value = new VarOutput<>(this, "Value", WorldPoint.class, new WorldPoint(0, 0, 0));

    public Location() {
        reg(this.value);
    }

    public void setValue(WorldPoint worldPoint) {
        this.value.setValue(worldPoint);
    }
}
