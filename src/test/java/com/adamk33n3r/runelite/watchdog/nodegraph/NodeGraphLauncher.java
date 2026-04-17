package com.adamk33n3r.runelite.watchdog.nodegraph;

import com.adamk33n3r.nodegraph.nodes.ActionNode;
import com.adamk33n3r.nodegraph.nodes.TriggerNode;
import com.adamk33n3r.nodegraph.nodes.constants.Bool;
import com.adamk33n3r.nodegraph.nodes.constants.Num;
import com.adamk33n3r.runelite.watchdog.alerts.ChatAlert;
import com.adamk33n3r.runelite.watchdog.alerts.SpawnedAlert;
import com.adamk33n3r.runelite.watchdog.notifications.ScreenFlash;
import com.adamk33n3r.runelite.watchdog.notifications.TextToSpeech;
import com.adamk33n3r.runelite.watchdog.ui.nodegraph.ActionNodePanel;
import com.adamk33n3r.runelite.watchdog.ui.nodegraph.AlertNodePanel;
import com.adamk33n3r.runelite.watchdog.ui.nodegraph.GraphPanel;
import com.adamk33n3r.runelite.watchdog.ui.nodegraph.variables.BoolNodePanel;
import com.adamk33n3r.runelite.watchdog.ui.nodegraph.variables.NumberNodePanel;
import com.google.inject.Guice;
import com.google.inject.Injector;

import net.runelite.api.Client;
import net.runelite.client.*;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.ui.laf.RuneLiteLAF;
import okhttp3.OkHttpClient;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.applet.Applet;
import java.awt.*;

public class NodeGraphLauncher {
    private static final Logger log = LoggerFactory.getLogger(NodeGraphLauncher.class);
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
            log.info("Reloading...");
            SwingUtilities.invokeLater(NodeGraphLauncher::rebuild);
        });

        GraphPanel content = injector.getInstance(GraphPanel.class);
        content.init();

//        Graph content = new Graph();
        JScrollPane viewport = new JScrollPane(content);
//        content.setLocation(-6000/2, -4000/2);
//        viewport.setViewPosition(new Point(content.getPreferredSize().width / 2, content.getPreferredSize().height / 2));
        jPanel.add(viewport, BorderLayout.CENTER);
        jFrame.add(jPanel);
        jFrame.revalidate();
    }

    private static void setUpExample1(GraphPanel panel) {
        ChatAlert alert = new ChatAlert("Test Chat Alert");
        alert.setMessage("This is a test message");
        ScreenFlash screenFlash = new ScreenFlash();
        TextToSpeech tts = new TextToSpeech();
        tts.setDelayMilliseconds(37);

        SpawnedAlert spawnedAlert = new SpawnedAlert("Spawned Alert");
        spawnedAlert.setPattern("Henry");
        spawnedAlert.setSpawnedDespawned(SpawnedAlert.SpawnedDespawned.SPAWNED);
        spawnedAlert.setSpawnedType(SpawnedAlert.SpawnedType.NPC);

        TriggerNode triggerNode = new TriggerNode(alert);
        panel.getGraph().add(triggerNode);
        ActionNode notificationNode = new ActionNode(tts);
        panel.getGraph().add(notificationNode);
        ActionNode screenFlashNode = new ActionNode(screenFlash);
        panel.getGraph().add(screenFlashNode);
        Bool boolNode = new Bool();
        boolNode.setValue(false);
        panel.getGraph().add(boolNode);
        Num numNode = new Num();
        numNode.setValue(27);
        panel.getGraph().add(numNode);

        AlertNodePanel test = new AlertNodePanel(panel, 425, 165, "Test", java.awt.Color.RED, triggerNode, injector.getInstance(com.adamk33n3r.runelite.watchdog.ui.panels.AlertPanelContentFactory.class));
        panel.add(test, GraphPanel.NODE_LAYER);
        ActionNodePanel testTEST = new ActionNodePanel(panel, 815, 365, "Text to Speech", java.awt.Color.PINK, notificationNode, injector.getInstance(com.adamk33n3r.runelite.watchdog.ui.panels.NotificationPanelFactory.class));
        panel.add(testTEST, GraphPanel.NODE_LAYER);
        ActionNodePanel screenFlashNodePanel = new ActionNodePanel(panel, 815, 65, "Screen Flash", java.awt.Color.PINK, screenFlashNode, injector.getInstance(com.adamk33n3r.runelite.watchdog.ui.panels.NotificationPanelFactory.class));
        panel.add(screenFlashNodePanel, GraphPanel.NODE_LAYER);
        BoolNodePanel boolNodePanel = new BoolNodePanel(panel, boolNode, 15, 15, "Bool Node", java.awt.Color.CYAN);
        panel.add(boolNodePanel, GraphPanel.NODE_LAYER);
        NumberNodePanel numNodePanel = new NumberNodePanel(panel, numNode, 15, 215, "Num Node", java.awt.Color.CYAN);
        panel.add(numNodePanel, GraphPanel.NODE_LAYER);

        panel.connect(test.getExecOut(), testTEST.getExecIn());
        panel.connect(test.getExecOut(), screenFlashNodePanel.getExecIn());
        panel.connect(boolNodePanel.getBoolValueOut(), screenFlashNodePanel.getEnabledIn());
    }

    private static Applet getApplet() {return null;}

    private static Injector getInjector() {
        final OkHttpClient okHttpClient = new OkHttpClient.Builder().build();
        final RuntimeConfigLoader runtimeConfigLoader = new RuntimeConfigLoader(okHttpClient);

//        Applet app = getApplet();
//        if (app instanceof Client) {
//            return null;
//        }
        return Guice.createInjector(new RuneLiteModule(
            okHttpClient,
            () -> Mockito.mock(Client.class),
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
