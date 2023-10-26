package com.adamk33n3r.runelite.watchdog.ui.nodegraph;

import java.awt.*;

public abstract class AcceptsConnectionNode extends Node implements IAcceptDropNode {
    public AcceptsConnectionNode(Graph graph, int x, int y, String name, Color color) {
        super(graph, x, y, name, color);
    }
}
