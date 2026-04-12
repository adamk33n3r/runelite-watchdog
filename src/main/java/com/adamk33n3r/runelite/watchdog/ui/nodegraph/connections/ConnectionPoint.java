package com.adamk33n3r.runelite.watchdog.ui.nodegraph.connections;

import com.adamk33n3r.runelite.watchdog.ui.nodegraph.NodePanel;
import lombok.Getter;

import javax.swing.JComponent;
import java.awt.*;

@Getter
public abstract class ConnectionPoint extends JComponent {
    protected static final Color EXEC_COLOR = new Color(255, 165, 50);
    public static final Color DISCONNECTED_COLOR = new Color(75, 75, 75);
    public static final Color CONNECTED_DATA_COLOR = new Color(210, 210, 210);

    private final NodePanel nodePanel;
    private final boolean exec;
    /** true = right-pointing (output), false = left-pointing (input) */
    private final boolean arrowRight;
    private final Dimension size = new Dimension(20, 20);

    public ConnectionPoint(NodePanel nodePanel, boolean exec, boolean arrowRight) {
        this.nodePanel = nodePanel;
        this.exec = exec;
        this.arrowRight = arrowRight;
        this.setBackground(DISCONNECTED_COLOR);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        boolean connected = !DISCONNECTED_COLOR.equals(this.getBackground());

        if (this.exec) {
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
            g2.setColor(connected ? EXEC_COLOR : DISCONNECTED_COLOR);
            g2.fillPolygon(xs, ys, 3);
            g2.setColor(Color.BLACK);
            g2.setStroke(new BasicStroke(1f));
            g2.drawPolygon(xs, ys, 3);
        } else {
            int margin = 3;
            int d = this.size.width - margin * 2;
            if (connected) {
                g2.setColor(CONNECTED_DATA_COLOR);
                g2.fillOval(margin, margin, d, d);
                g2.setColor(new Color(30, 30, 30));
                g2.setStroke(new BasicStroke(1f));
                g2.drawOval(margin, margin, d, d);
            } else {
                g2.setColor(DISCONNECTED_COLOR);
                g2.setStroke(new BasicStroke(2f));
                g2.drawOval(margin, margin, d, d);
            }
        }
    }

    @Override
    public Dimension getPreferredSize() {
        return this.size;
    }
}
