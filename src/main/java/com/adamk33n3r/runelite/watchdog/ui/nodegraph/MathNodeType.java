package com.adamk33n3r.runelite.watchdog.ui.nodegraph;

import com.adamk33n3r.nodegraph.Node;
import com.adamk33n3r.nodegraph.nodes.math.*;
import com.adamk33n3r.runelite.watchdog.Displayable;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MathNodeType implements Displayable {
    ADD("Add", "Adds two numbers together", Add.class, AddNodePanel.class),
    SUBTRACT("Subtract", "Subtracts B from A", Subtract.class, SubtractNodePanel.class),
    MULTIPLY("Multiply", "Multiplies two numbers", Multiply.class, MultiplyNodePanel.class),
    DIVIDE("Divide", "Divides A by B (returns 0 on divide-by-zero)", Divide.class, DivideNodePanel.class),
    MIN("Min", "Returns the smaller of two numbers", Min.class, MinNodePanel.class),
    MAX("Max", "Returns the larger of two numbers", Max.class, MaxNodePanel.class),
    CLAMP("Clamp", "Clamps a value between Min and Max", Clamp.class, ClampNodePanel.class),
    ;

    private final String name;
    private final String tooltip;
    private final Class<? extends Node> implClass;
    private final Class<? extends NodePanel> nodePanelClass;
}
