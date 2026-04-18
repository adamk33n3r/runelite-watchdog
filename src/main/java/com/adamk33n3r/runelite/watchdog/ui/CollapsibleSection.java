package com.adamk33n3r.runelite.watchdog.ui;

import net.runelite.client.ui.ColorScheme;
import net.runelite.client.util.SwingUtil;

import javax.swing.*;
import java.awt.*;

public class CollapsibleSection extends JPanel {
    private static final String ARROW_EXPANDED = "▼ ";
    private static final String ARROW_COLLAPSED = "▶ ";

    private boolean expanded;
    private final JButton header;
    private final JComponent content;
    private final String title;
    private Runnable onExpand;

    public CollapsibleSection(String title, JComponent content, boolean expandedByDefault) {
        this.title = title;
        this.content = content;
        this.expanded = expandedByDefault;

        this.setLayout(new BorderLayout(0, 2));
        this.setBorder(BorderFactory.createEmptyBorder(4, 0, 0, 0));

        this.header = new JButton((expandedByDefault ? ARROW_EXPANDED : ARROW_COLLAPSED) + title);
        this.header.setHorizontalAlignment(SwingConstants.LEFT);
        this.header.setForeground(ColorScheme.LIGHT_GRAY_COLOR);
        SwingUtil.removeButtonDecorations(this.header);
        this.header.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        this.header.addActionListener(e -> this.toggle());

        this.add(this.header, BorderLayout.NORTH);

        content.setVisible(expandedByDefault);
        this.add(content, BorderLayout.CENTER);
    }

    public void setOnExpand(Runnable onExpand) {
        this.onExpand = onExpand;
    }

    public void collapse() {
        if (!this.expanded) return;
        this.expanded = false;
        this.content.setVisible(false);
        this.header.setText(ARROW_COLLAPSED + this.title);
    }

    private void toggle() {
        this.expanded = !this.expanded;
        this.content.setVisible(this.expanded);
        this.header.setText((this.expanded ? ARROW_EXPANDED : ARROW_COLLAPSED) + this.title);
        if (this.expanded && this.onExpand != null) {
            this.onExpand.run();
        }
        Container parent = this.getParent();
        if (parent != null) {
            parent.revalidate();
            parent.repaint();
        }
    }
}
