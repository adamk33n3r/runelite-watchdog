package com.adamk33n3r.runelite.watchdog.ui.nodegraph;

import com.adamk33n3r.runelite.watchdog.NotificationType;
import com.adamk33n3r.runelite.watchdog.nodegraph.Node;
import com.adamk33n3r.runelite.watchdog.nodegraph.Var;
import lombok.Getter;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public abstract class ConnectionPoint extends JComponent {
    @Getter
    private NodePanel nodePanel;

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
