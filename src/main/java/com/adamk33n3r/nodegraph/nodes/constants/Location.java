package com.adamk33n3r.nodegraph.nodes.constants;

import com.adamk33n3r.nodegraph.VarInput;
import com.adamk33n3r.nodegraph.VarOutput;
import lombok.Getter;
import net.runelite.api.coords.WorldPoint;

@Getter
public class Location extends VariableNode {
    private final VarInput<WorldPoint> value = new VarInput<>(this, "Value", WorldPoint.class, new WorldPoint(0, 0, 0));
    private final VarOutput<WorldPoint> valueOut = new VarOutput<>(this, "Value", WorldPoint.class, new WorldPoint(0, 0, 0));

    public Location() {
        this.value.onChange(val -> this.process());

        reg(this.value);
        reg(this.valueOut);
    }

    public void setValue(WorldPoint worldPoint) {
        this.value.setValue(worldPoint);
    }

    @Override
    public void process() {
        this.valueOut.setValue(this.value.getValue());
    }
}
