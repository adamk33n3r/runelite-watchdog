package com.adamk33n3r.runelite.watchdog;

import net.runelite.client.ui.overlay.components.ComponentConstants;
import net.runelite.client.ui.overlay.components.LayoutableRenderableEntity;
import net.runelite.client.ui.overlay.components.TextComponent;
import net.runelite.client.util.Text;

import com.google.common.base.MoreObjects;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.awt.*;

/**
 * Adapted from LineComponent and TitleComponent
 */
@Setter
@Builder
public class WrappedTitleComponent implements LayoutableRenderableEntity {
    @Builder.Default
    private String text = "";
    @Builder.Default
    private Color color = Color.WHITE;
    private Font font;

    @Builder.Default
    private Point preferredLocation = new Point();

    @Builder.Default
    private Dimension preferredSize = new Dimension(ComponentConstants.STANDARD_WIDTH, 0);

    @Builder.Default
    @Getter
    private Rectangle bounds = new Rectangle();

    private final int LINE_GAP = 2;
    private final int WIDTH_PADDING = 20;
    private final int HEIGHT_PADDING = 8;
    private final int TEXT_PADDING = 20;

    @Override
    public Dimension render(Graphics2D graphics) {
        final Font font = MoreObjects.firstNonNull(this.font, graphics.getFont());
        final FontMetrics fontMetrics = graphics.getFontMetrics(font);
        final int fmHeight = fontMetrics.getHeight();
        final int baseX = preferredLocation.x;
        final int baseY = preferredLocation.y + fmHeight;
        int x = baseX;
        int y = baseY;
        final int smallWidth = this.preferredSize.width;
        final int fullWidth = getLineWidth(this.text, fontMetrics);
        final TextComponent textComponent = new TextComponent();

        if (smallWidth < fullWidth) {
            final String[] splitLines = lineBreakText(this.text, smallWidth, fontMetrics);

            for (final String text : splitLines) {
                textComponent.setPosition(new Point(x + ((smallWidth - fontMetrics.stringWidth(text) + TEXT_PADDING) / 2), y + LINE_GAP));
                textComponent.setText(text);
                textComponent.setColor(this.color);
                textComponent.setFont(font);
                textComponent.render(graphics);

                y += fmHeight;
            }

            final Dimension dimension = new Dimension(this.preferredSize.width + WIDTH_PADDING, y - baseY + HEIGHT_PADDING);
            this.bounds.setLocation(preferredLocation);
            this.bounds.setSize(dimension);
            return dimension;
        }

        if (!this.text.isEmpty()) {
            textComponent.setPosition(new Point(x + ((smallWidth - fontMetrics.stringWidth(this.text) + TEXT_PADDING) / 2), y + LINE_GAP));
            textComponent.setText(this.text);
            textComponent.setColor(this.color);
            textComponent.setFont(font);
            textComponent.render(graphics);
        }

        y += fmHeight;

        final Dimension dimension = new Dimension(this.preferredSize.width + WIDTH_PADDING, y - baseY + HEIGHT_PADDING);
        this.bounds.setLocation(this.preferredLocation);
        this.bounds.setSize(dimension);
        return dimension;
    }

    private static int getLineWidth(final String line, final FontMetrics metrics) {
        return metrics.stringWidth(Text.removeTags(line));
    }

    private static String[] lineBreakText(String text, int maxWidth, FontMetrics metrics) {
        final String[] words = text.split(" ");

        if (words.length == 0) {
            return new String[0];
        }

        final StringBuilder wrapped = new StringBuilder(words[0]);
        int spaceLeft = maxWidth - metrics.stringWidth(wrapped.toString());

        for (int i = 1; i < words.length; i++) {
            final String word = words[i];
            final int wordLen = metrics.stringWidth(word);
            final int spaceWidth = metrics.stringWidth(" ");

            if (wordLen + spaceWidth > spaceLeft) {
                wrapped.append("\n").append(word);
                spaceLeft = maxWidth - wordLen;
            } else {
                wrapped.append(" ").append(word);
                spaceLeft -= spaceWidth + wordLen;
            }
        }

        return wrapped.toString().split("\n");
    }
}
