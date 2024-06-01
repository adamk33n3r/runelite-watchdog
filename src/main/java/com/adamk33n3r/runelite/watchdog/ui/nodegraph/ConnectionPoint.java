package com.adamk33n3r.runelite.watchdog.ui.nodegraph;

import lombok.Getter;

import javax.swing.JComponent;
import java.awt.*;

@Getter
public abstract class ConnectionPoint extends JComponent {
    private final NodePanel nodePanel;

    public ConnectionPoint(NodePanel nodePanel) {
        this.nodePanel = nodePanel;
        System.out.println("new connection point");
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Dimension size = this.getSize();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, size.width, size.height);
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(20, 20);
    }
}
