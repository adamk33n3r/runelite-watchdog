package com.adamk33n3r.runelite.watchdog;

import com.adamk33n3r.runelite.watchdog.ui.nodegraph.Graph;
import net.runelite.client.RuneLite;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.skin.SubstanceRuneLiteLookAndFeel;
import net.runelite.client.util.SwingUtil;

import javax.swing.*;
import java.awt.*;

public class NodeGraphLauncher {
    private static JFrame jFrame;
    public static void main(String[] args) throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            // Set some sensible swing defaults
            SwingUtil.setupDefaults();

            // Use substance look and feel
            SwingUtil.setTheme(new SubstanceRuneLiteLookAndFeel());

            // Use custom UI font
            SwingUtil.setFont(FontManager.getRunescapeFont());

            jFrame = new JFrame("Node Graph Editor");
            jFrame.setSize(new Dimension(1200, 800));
            jFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            jFrame.add(new JLabel("Loading..."));

            rebuild();

//            jFrame.pack();
            jFrame.setLocationRelativeTo(null);
            jFrame.setVisible(true);
        });
    }

    public static void rebuild() {
        jFrame.getContentPane().removeAll();

        JPanel jPanel = new JPanel(new BorderLayout());
        JButton reload = new JButton("RELOAD");
        jPanel.add(reload, BorderLayout.NORTH);
        reload.addActionListener((ev) -> {
            System.out.println("Reloading...");
            SwingUtilities.invokeLater(NodeGraphLauncher::rebuild);
        });

        Graph content = new Graph();
        JScrollPane viewport = new JScrollPane(content);
//        content.setLocation(-6000/2, -4000/2);
//        viewport.setViewPosition(new Point(content.getPreferredSize().width / 2, content.getPreferredSize().height / 2));
        jPanel.add(viewport, BorderLayout.CENTER);
        jFrame.add(jPanel);
        jFrame.revalidate();
    }
}
