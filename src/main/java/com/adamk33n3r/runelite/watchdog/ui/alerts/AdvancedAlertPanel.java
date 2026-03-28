package com.adamk33n3r.runelite.watchdog.ui.alerts;

import com.adamk33n3r.runelite.watchdog.WatchdogPanel;
import com.adamk33n3r.runelite.watchdog.WatchdogPlugin;
import com.adamk33n3r.runelite.watchdog.alerts.AdvancedAlert;
import com.adamk33n3r.runelite.watchdog.ui.panels.AlertPanel;
import com.adamk33n3r.runelite.watchdog.ui.nodegraph.GraphPanel;

import net.runelite.client.ui.PluginPanel;

import javax.swing.*;
import java.awt.*;

public class AdvancedAlertPanel extends AlertPanel<AdvancedAlert> {
    public AdvancedAlertPanel(WatchdogPanel watchdogPanel, AdvancedAlert alert) {
        super(watchdogPanel, alert);
    }

    @Override
    protected void build() {
        this.addAlertDefaults()
            .addButton("Open Graph Editor", "Open the visual node graph editor for this alert", (btn, modifiers) -> {
                GraphPanel graphPanel = new GraphPanel();
                WatchdogPlugin.getInstance().getInjector().injectMembers(graphPanel);
                graphPanel.init(WatchdogPlugin.getInstance().getInjector(), this.alert.getGraph());

                JScrollPane scrollPane = new JScrollPane(graphPanel);
                scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
                scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

                GraphEditorPanel editorPanel = new GraphEditorPanel(this.watchdogPanel, scrollPane);
                this.muxer.pushState(editorPanel);
            });
    }

    /**
     * A PluginPanel that wraps the GraphPanel in a scrollable view with a back button.
     */
    private static class GraphEditorPanel extends PluginPanel {
        GraphEditorPanel(WatchdogPanel watchdogPanel, JScrollPane scrollPane) {
            super(false);
            this.setLayout(new BorderLayout());

            JButton backButton = new JButton("Back");
            backButton.addActionListener(e -> {
                WatchdogPlugin.getInstance().getAlertManager().saveAlerts();
                watchdogPanel.getMuxer().popState();
            });
            JPanel topBar = new JPanel(new BorderLayout());
            topBar.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
            topBar.add(backButton, BorderLayout.WEST);
            JLabel title = new JLabel("Graph Editor");
            title.setForeground(Color.WHITE);
            topBar.add(title, BorderLayout.CENTER);
            this.add(topBar, BorderLayout.NORTH);
            this.add(scrollPane, BorderLayout.CENTER);
        }
    }
}
