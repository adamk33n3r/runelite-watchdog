package com.adamk33n3r.runelite.watchdog.ui.nodegraph.connections;

import com.adamk33n3r.runelite.watchdog.ui.nodegraph.NodePanel;
import lombok.Getter;

import javax.swing.JComponent;
import java.awt.*;

@Getter
public abstract class ConnectionPoint extends JComponent {
    protected static final Color EXEC_COLOR = new Color(255, 165, 50);

    private final NodePanel nodePanel;
    private final boolean exec;
    /** true = right-pointing (output), false = left-pointing (input) */
    private final boolean arrowRight;
    private final Dimension size = new Dimension(20, 20);

    public ConnectionPoint(NodePanel nodePanel, boolean exec, boolean arrowRight) {
        this.nodePanel = nodePanel;
        this.exec = exec;
        this.arrowRight = arrowRight;
        this.setBackground(Color.RED);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (this.exec) {
            // Amber when connected, red when disconnected
            boolean connected = !Color.RED.equals(this.getBackground());
            g2.setColor(connected ? EXEC_COLOR : Color.RED);
            int w = this.size.width;
            int h = this.size.height;
            int mid = h / 2;
            int[] xs, ys;
            if (this.arrowRight) {
                xs = new int[]{0, w, 0};
                ys = new int[]{0, mid, h};
            } else {
                xs = new int[]{w, 0, w};
                ys = new int[]{0, mid, h};
            }
            g2.fillPolygon(xs, ys, 3);
        } else {
            g2.setColor(this.getBackground());
            g2.fillRect(0, 0, this.size.width, this.size.height);
        }
    }

    @Override
    public Dimension getPreferredSize() {
        return this.size;
    }
}
