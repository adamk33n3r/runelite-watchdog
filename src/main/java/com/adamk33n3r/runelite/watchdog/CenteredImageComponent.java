package com.adamk33n3r.runelite.watchdog;

import net.runelite.client.ui.overlay.components.LayoutableRenderableEntity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;


@RequiredArgsConstructor
@Setter
public class CenteredImageComponent implements LayoutableRenderableEntity {

    private final BufferedImage image;

    @Getter
    private final Rectangle bounds = new Rectangle();

    private Point preferredLocation = new Point();
    private Dimension preferredSize = new Dimension();

    @Override
    public Dimension render(Graphics2D graphics) {
        if (image == null) {
            return null;
        }

        graphics.drawImage(image, preferredLocation.x + preferredSize.width / 2 - this.image.getWidth() / 2, preferredLocation.y, null);
        final Dimension dimension = new Dimension(image.getWidth(), image.getHeight());
        bounds.setLocation(preferredLocation);
        bounds.setSize(dimension);
        return dimension;
    }
}
