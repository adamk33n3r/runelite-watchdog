package com.adamk33n3r.runelite.watchdog.ui.panels;

import com.adamk33n3r.runelite.watchdog.WatchdogMuxer;
import com.adamk33n3r.runelite.watchdog.WatchdogPlugin;
import com.adamk33n3r.runelite.watchdog.ui.Icons;
import com.adamk33n3r.runelite.watchdog.ui.StretchedStackedLayout;

import net.runelite.client.ui.PluginPanel;

import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.Dimension;
import java.io.File;
import java.io.IOException;

@Slf4j
@Singleton
public class ToolsPanel extends PluginPanel {
    @Inject
    private Provider<HistoryPanel> historyPanelProvider;
    private final Provider<WatchdogMuxer> muxer;

    @Inject
    public ToolsPanel(Provider<WatchdogMuxer> muxer) {
        super(false);
        this.muxer = muxer;

        this.setLayout(new BorderLayout());

        JButton backButton = PanelUtils.createActionButton(
            Icons.BACK,
            Icons.BACK_HOVER,
            "Back",
            (btn, modifiers) -> this.muxer.get().popState()
        );
        backButton.setPreferredSize(new Dimension(22, 16));
        backButton.setBorder(new EmptyBorder(0, 0, 0, 5));

        JPanel nameGroup = new JPanel(new BorderLayout());
        this.add(nameGroup, BorderLayout.NORTH);
        nameGroup.setBorder(new EmptyBorder(10, 5, 10, 5));
        nameGroup.add(new JLabel("Tools"), BorderLayout.CENTER);
        nameGroup.add(backButton, BorderLayout.WEST);

        JPanel tools = new JPanel(new StretchedStackedLayout(3));

        tools.add(PanelUtils.createButton("Alert History", "Alert History", (btn, mods) -> {
            this.muxer.get().pushState(this.historyPanelProvider.get());
        }));

        tools.add(PanelUtils.createButton("Clear All Processing Alerts", "Clear All Processing Alerts", (btn, mods) -> {
            WatchdogPlugin.getInstance().stopAllAlerts();
        }));
        tools.add(PanelUtils.createButton("Stop All Queued Sounds", "Stop All Queued Sounds", (btn, mods) -> {
            WatchdogPlugin.getInstance().getSoundPlayer().stop();
        }));
        tools.add(PanelUtils.createButton("Dismiss All Overlays", "Dismiss All Overlays", (btn, mods) -> {
            WatchdogPlugin.getInstance().getNotificationOverlay().clear();
        }));
        tools.add(PanelUtils.createButton("Dismiss All Screen Markers", "Dismiss All Screen Markers", (btn, mods) -> {
            WatchdogPlugin.getInstance().getScreenMarkerUtil().removeAllMarkers();
        }));
        tools.add(PanelUtils.createButton("Dismiss All Object Markers", "Dismiss All Object Markers", (btn, mods) -> {
            WatchdogPlugin.getInstance().getObjectMarkerManager().removeAllMarkers();
        }));
        tools.add(PanelUtils.createButton("Reload All Alerts from Profile", "This will reload all alerts from disk", (btn, mods) -> {
            WatchdogPlugin.getInstance().getAlertManager().loadAlerts();
        }));
        if (Desktop.isDesktopSupported()) {
            tools.add(PanelUtils.createButton("Open Backups Folder", "Opens the alert backup folder in your file explorer", (btn, mods) -> {
                File backupDir = WatchdogPlugin.getInstance().getAlertBackupManager().getBackupDir();
                if (!backupDir.exists()) {
                    backupDir.mkdirs();
                }
                try {
                    Desktop.getDesktop().open(backupDir);
                } catch (IOException e) {
                    log.warn("Failed to open backup folder", e);
                }
            }));
        }
        this.add(tools);
    }
}
