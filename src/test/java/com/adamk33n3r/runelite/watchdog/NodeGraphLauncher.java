package com.adamk33n3r.runelite.watchdog;

import com.adamk33n3r.runelite.watchdog.ui.nodegraph.Graph;
import com.google.inject.Guice;
import com.google.inject.Injector;
import net.runelite.client.*;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.ui.laf.RuneLiteLAF;
import okhttp3.OkHttpClient;

import javax.swing.*;
import java.awt.*;

public class NodeGraphLauncher {
    private static JFrame jFrame;
    private static Injector injector;
    public static void main(String[] args) throws Exception {
        injector = getInjector();
        injector.getInstance(ConfigManager.class).load();

        SwingUtilities.invokeAndWait(() -> {
            RuneLiteLAF.setup();

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

        Graph content = injector.getInstance(Graph.class);

//        Graph content = new Graph();
        JScrollPane viewport = new JScrollPane(content);
//        content.setLocation(-6000/2, -4000/2);
//        viewport.setViewPosition(new Point(content.getPreferredSize().width / 2, content.getPreferredSize().height / 2));
        jPanel.add(viewport, BorderLayout.CENTER);
        jFrame.add(jPanel);
        jFrame.revalidate();
    }

    private static Injector getInjector() {
        final OkHttpClient okHttpClient = new OkHttpClient.Builder().build();
        final RuntimeConfigLoader runtimeConfigLoader = new RuntimeConfigLoader(okHttpClient);

        return Guice.createInjector(new RuneLiteModule(
            okHttpClient,
            () -> null,
            runtimeConfigLoader,
            true,
            false,
            false,
            RuneLite.DEFAULT_SESSION_FILE,
            null,
            false,
            false
        ));
    }
}
