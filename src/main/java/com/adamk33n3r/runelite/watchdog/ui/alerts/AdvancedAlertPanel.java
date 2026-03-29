package com.adamk33n3r.runelite.watchdog.ui.alerts;

import com.adamk33n3r.runelite.watchdog.WatchdogPanel;
import com.adamk33n3r.runelite.watchdog.WatchdogPlugin;
import com.adamk33n3r.runelite.watchdog.alerts.AdvancedAlert;
import com.adamk33n3r.runelite.watchdog.ui.panels.AlertPanel;
import com.adamk33n3r.runelite.watchdog.ui.nodegraph.GraphPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class AdvancedAlertPanel extends AlertPanel<AdvancedAlert> {
    private JFrame graphEditorFrame;

    public AdvancedAlertPanel(WatchdogPanel watchdogPanel, AdvancedAlert alert) {
        super(watchdogPanel, alert);
    }

    @Override
    protected void onBack() {
        if (this.graphEditorFrame != null && this.graphEditorFrame.isDisplayable()) {
            this.graphEditorFrame.dispose();
        }
    }

    @Override
    protected void build() {
        this.addAlertDefaults()
            .addButton("Open Graph Editor", "Open the visual node graph editor for this alert", (btn, modifiers) -> {
                if (this.graphEditorFrame != null && this.graphEditorFrame.isDisplayable()) {
                    this.graphEditorFrame.toFront();
                    this.graphEditorFrame.requestFocus();
                    return;
                }

                GraphPanel graphPanel = new GraphPanel();
                WatchdogPlugin.getInstance().getInjector().injectMembers(graphPanel);
                graphPanel.setOnChange(() -> WatchdogPlugin.getInstance().getAlertManager().saveAlerts());
                graphPanel.init(WatchdogPlugin.getInstance().getInjector(), this.alert.getGraph());

                JScrollPane scrollPane = new JScrollPane(graphPanel);
                scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
                scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

                this.graphEditorFrame = new JFrame("Graph Editor - " + this.alert.getName());
                this.graphEditorFrame.setSize(new Dimension(1200, 800));
                this.graphEditorFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
                this.graphEditorFrame.setLayout(new BorderLayout());
                this.graphEditorFrame.add(scrollPane, BorderLayout.CENTER);
                this.graphEditorFrame.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosing(WindowEvent e) {
                        WatchdogPlugin.getInstance().getAlertManager().saveAlerts();
                    }
                });
                this.graphEditorFrame.setLocationRelativeTo(null);
                this.graphEditorFrame.setVisible(true);
            });
    }
}
