package com.adamk33n3r.runelite.narration;

import net.runelite.api.Point;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPriority;

import javax.inject.Inject;
import java.awt.*;

public class NarrationOverlay extends OverlayPanel {
    private final NarrationPlugin plugin;
    @Inject
    private NarrationOverlay(NarrationPlugin plugin) {
        this.plugin = plugin;
        this.setLayer(OverlayLayer.ALWAYS_ON_TOP);
        this.setPriority(OverlayPriority.HIGHEST);
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        graphics.setColor(Color.CYAN);
        Point p = this.plugin.getMenuOpenPoint();
        graphics.drawRect(p.getX(), p.getY(), 50, 50);
        return super.render(graphics);
    }
}
