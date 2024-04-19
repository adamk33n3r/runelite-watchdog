package com.adamk33n3r.runelite.watchdog.hub;

import com.adamk33n3r.runelite.watchdog.Util;
import com.adamk33n3r.runelite.watchdog.WatchdogConfig;
import com.adamk33n3r.runelite.watchdog.WatchdogMuxer;
import com.adamk33n3r.runelite.watchdog.ui.Icons;
import com.adamk33n3r.runelite.watchdog.ui.SearchBar;
import com.adamk33n3r.runelite.watchdog.ui.panels.PanelUtils;
import com.adamk33n3r.runelite.watchdog.ui.panels.ScrollablePanel;

import net.runelite.client.ui.DynamicGridLayout;
import net.runelite.client.ui.PluginPanel;
import net.runelite.client.ui.components.IconTextField;

import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.stream.Collectors;

@Slf4j
public class AlertHubPanel extends PluginPanel {
    private final Provider<WatchdogMuxer> muxer;
    private final AlertHubClient alertHubClient;
    private final ScheduledExecutorService executor;
    private final WatchdogConfig watchdogConfig;

    private List<AlertHubItem> alertHubItems = new ArrayList<>();
    private final IconTextField searchBar;
    private final JPanel container;
    private final JLabel loading;
    private final JScrollPane scrollPane;

    @Inject
    public AlertHubPanel(Provider<WatchdogMuxer> muxer, AlertHubClient alertHubClient, ScheduledExecutorService executor, WatchdogConfig watchdogConfig) {
        super(false);
        this.muxer = muxer;
        this.alertHubClient = alertHubClient;
        this.executor = executor;
        this.watchdogConfig = watchdogConfig;

        JButton backButton = PanelUtils.createActionButton(
            Icons.BACK,
            Icons.BACK_HOVER,
            "Back",
            (btn, modifiers) -> this.muxer.get().popState()
        );
        backButton.setPreferredSize(new Dimension(22, 16));
        backButton.setBorder(new EmptyBorder(0, 0, 0, 5));

        GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);
        this.searchBar = new SearchBar(this::updateFilter);
        Arrays.stream(AlertHubCategory.values()).map(AlertHubCategory::getName).forEach(this.searchBar.getSuggestionListModel()::addElement);

        this.container = new JPanel(new DynamicGridLayout(0, 1, 0, 5));
//        this.container.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));

        ScrollablePanel wrapper = new ScrollablePanel(new BorderLayout());
        wrapper.add(this.container, BorderLayout.NORTH);
        this.loading = new JLabel("Loading...");
        this.loading.setVisible(false);
        this.loading.setHorizontalAlignment(JLabel.CENTER);
        wrapper.add(this.loading, BorderLayout.CENTER);
        wrapper.setScrollableWidth(ScrollablePanel.ScrollableSizeHint.FIT);
        wrapper.setScrollableHeight(ScrollablePanel.ScrollableSizeHint.STRETCH);
        wrapper.setScrollableBlockIncrement(SwingConstants.VERTICAL, ScrollablePanel.IncrementType.PERCENT, 10);
        wrapper.setScrollableUnitIncrement(SwingConstants.VERTICAL, ScrollablePanel.IncrementType.PERCENT, 10);
        this.scrollPane = new JScrollPane(wrapper, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        JButton refresh = PanelUtils.createActionButton(Icons.REFRESH, Icons.REFRESH_HOVER, "Refresh", (btn, mod) -> {
            this.reloadList(true);
        });

        layout.setVerticalGroup(layout.createSequentialGroup()
            .addGap(5)
            .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(backButton, 24, 24, 24)
                .addComponent(this.searchBar, 24, 24, 24)
                .addComponent(refresh, 24, 24, 24))
            .addGap(5)
            .addComponent(this.scrollPane)
        );

        layout.setHorizontalGroup(layout.createParallelGroup()
            .addGroup(layout.createSequentialGroup()
                .addComponent(backButton)
                .addComponent(this.searchBar)
                .addComponent(refresh))
            .addGroup(layout.createSequentialGroup()
                .addGap(5)
                .addComponent(this.scrollPane)
                .addGap(5)
            )
        );

        this.reloadList(false);
    }

    public void reloadList(boolean forceDownload) {
        if (this.loading.isVisible()) {
            return;
        }

        this.container.removeAll();
        this.loading.setVisible(true);
        this.executor.submit(() -> {
            try {
                List<AlertHubClient.AlertDisplayInfo> alerts = this.alertHubClient.downloadManifest(forceDownload);
                this.reloadList(alerts);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void reloadList(List<AlertHubClient.AlertDisplayInfo> alerts) {
        SwingUtilities.invokeLater(() -> {
            this.loading.setVisible(false);
            this.alertHubItems = alerts.stream()
                .map(alertDisplayInfo -> new AlertHubItem(alertDisplayInfo, this.watchdogConfig))
                .collect(Collectors.toList());
            this.updateFilter(this.searchBar.getText());
        });
    }

    private void updateFilter(String search) {
        this.container.removeAll();
        this.alertHubItems.stream().filter(alertHubItem -> {
            AlertManifest manifest = alertHubItem.getAlertDisplayInfo().getManifest();
            return Util.searchText(search, manifest.getKeywords());
        }).forEach(this.container::add);
        this.container.revalidate();
        this.scrollPane.getVerticalScrollBar().setValue(0);
    }
}
