package com.adamk33n3r.runelite.watchdog.ui.nodegraph.connections;


import javax.swing.JComponent;
import java.awt.*;
import java.awt.geom.CubicCurve2D;

public class Connection extends JComponent {
    protected static final int END_SIZE = 10;
    protected static final int BOUNDS_OFFSET = 40;
    private static final Color EXEC_COLOR = new Color(255, 165, 50);

    protected Point start;
    protected Point end;
    protected boolean exec = false;

    public Connection(Point start, Point end) {
        this.start = start;
        this.end = end;
        this.recalculateBounds();
    }

    public void setExec(boolean exec) {
        this.exec = exec;
    }

    private static final Color DATA_COLOR = new Color(220, 220, 220);

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        int startX = this.start.x;
        startX += 10;
        int startY = this.start.y;
        int endX = this.end.x;
        endX -= 2;
        int endY = this.end.y;

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g.translate(BOUNDS_OFFSET, BOUNDS_OFFSET);

        Color lineColor = this.exec ? EXEC_COLOR : DATA_COLOR;
        int lineWidth = this.exec ? 5 : 4;

        CubicCurve2D.Double curve = new CubicCurve2D.Double(
            // +2 to offset to be on the edge
            startX+2, startY,
            startX + 100, startY,
            endX - 100, endY,
            endX-10, endY);

        // Black border pass — drawn 2px wider underneath
        g2.setColor(Color.BLACK);
        g2.setStroke(new BasicStroke(lineWidth + 2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.draw(curve);

        // Colored fill pass on top
        g2.setColor(lineColor);
        g2.setStroke(new BasicStroke(lineWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.draw(curve);

        // Start cap — black border then colored fill
        int arcDiameter = 20;
        g2.setColor(Color.BLACK);
        g2.setStroke(new BasicStroke(5, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.fillArc(startX - (arcDiameter + 4) / 2, startY - (arcDiameter + 4) / 2, arcDiameter + 4, arcDiameter + 4, 90, -180);
        g2.setColor(lineColor);
        g2.fillArc(startX - arcDiameter / 2, startY - arcDiameter / 2, arcDiameter, arcDiameter, 90, -180);

        // Arrowhead — black inflated polygon first, then colored polygon on top
        int[] arrowXs = new int[]{endX - 10, endX - 10, endX};
        int[] arrowYs = new int[]{endY - 10, endY + 10, endY};
        int[] arrowXsBorder = new int[]{endX - 12, endX - 12, endX + 3};
        int[] arrowYsBorder = new int[]{endY - 14, endY + 14, endY};
        g2.setColor(Color.BLACK);
        g2.fillPolygon(arrowXsBorder, arrowYsBorder, 3);
        g2.setColor(lineColor);
        g2.fillPolygon(arrowXs, arrowYs, 3);

//        g2.setColor(Color.BLACK);
//        g2.drawPolygon(new int[]{endX - 12, endX-12, endX}, new int[]{endY-12, endY+12, endY}, 3);
//        g2.setColor(lineColor);
//        g2.fillPolygon(new int[]{endX - 10, endX-10, endX}, new int[]{endY-10, endY+10, endY}, 3);

        g.translate(-BOUNDS_OFFSET, -BOUNDS_OFFSET);
    }

    public void recalculateBounds() {
        this.setBounds(
            Math.min(this.start.x, this.end.x) - BOUNDS_OFFSET,
            Math.min(this.start.y, this.end.y) - BOUNDS_OFFSET,
            Math.abs(this.start.x - this.end.x) + BOUNDS_OFFSET * 2,
            Math.abs(this.start.y - this.end.y) + BOUNDS_OFFSET * 2
        );
    }
}
