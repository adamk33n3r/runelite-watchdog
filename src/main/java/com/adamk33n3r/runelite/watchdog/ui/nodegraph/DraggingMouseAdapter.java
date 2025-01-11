package com.adamk33n3r.runelite.watchdog.ui.nodegraph;


import lombok.RequiredArgsConstructor;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.function.BiConsumer;

@RequiredArgsConstructor
public class DraggingMouseAdapter extends MouseAdapter {
    private final BiConsumer<Point, Point> onDrag;
    private Point onDragStartPoint;

    @Override
    public void mousePressed(MouseEvent e) {
        if (SwingUtilities.isLeftMouseButton(e) && this.onDragStartPoint == null) {
            this.onDragStartPoint = e.getPoint();
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        this.onDragStartPoint = null;
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (SwingUtilities.isLeftMouseButton(e) && this.onDragStartPoint != null) {
            this.onDrag.accept(this.onDragStartPoint, e.getPoint());
        }
    }
}
