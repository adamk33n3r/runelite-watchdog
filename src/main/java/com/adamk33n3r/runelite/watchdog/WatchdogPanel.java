package com.adamk33n3r.runelite.watchdog;

import com.adamk33n3r.runelite.watchdog.alerts.*;
import com.adamk33n3r.runelite.watchdog.hub.AlertHubPanel;
import com.adamk33n3r.runelite.watchdog.ui.Icons;
import com.adamk33n3r.runelite.watchdog.ui.ImportExportDialog;
import com.adamk33n3r.runelite.watchdog.ui.MessagePickerDialog;
import com.adamk33n3r.runelite.watchdog.ui.alerts.*;
import com.adamk33n3r.runelite.watchdog.ui.panels.AlertListPanel;
import com.adamk33n3r.runelite.watchdog.ui.panels.HistoryPanel;
import com.adamk33n3r.runelite.watchdog.ui.panels.PanelUtils;
import com.adamk33n3r.runelite.watchdog.ui.panels.ToolsPanel;

import net.runelite.api.Client;
import net.runelite.api.MessageNode;
import net.runelite.client.events.NotificationFired;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.PluginPanel;
import net.runelite.client.util.LinkBrowser;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.Arrays;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Singleton
public class WatchdogPanel extends PluginPanel {
    @Inject
    @Named("watchdog.helpURL")
    private String HELP_URL;

    @Inject
    @Named("watchdog.discordURL")
    private String DISCORD_URL;

    @Inject
    @Named("watchdog.kofiURL")
    private String KOFI_URL;

    @Inject
    @Named("watchdog.pluginVersion")
    private String PLUGIN_VERSION;

    @Inject
    @Named("watchdog.pluginVersionFull")
    private String PLUGIN_VERSION_FULL;

    @Inject
    @Named("VERSION_PHASE")
    private String PLUGIN_VERSION_PHASE;

    @Getter
    private final WatchdogMuxer muxer = new WatchdogMuxer(this);

    @Getter
    @Inject
    private Provider<HistoryPanel> historyPanelProvider;

    @Inject
    private Provider<ToolsPanel> toolsPanelProvider;

    @Inject
    private Provider<AlertHubPanel> alertHubPanelProvider;

    @Inject
    private AlertManager alertManager;

    @Inject
    private WatchdogConfig watchdogConfig;

    @Inject
    private Client client;

    @Inject
    private OkHttpClient httpClient;

    private AlertListPanel alertListPanel;

    public WatchdogPanel() {
        super(false);
    }

    public void rebuild() {
        this.removeAll();
        this.setLayout(new BorderLayout(0, 3));
        this.setBorder(new EmptyBorder(0, 5, 0, 5));
        this.setBackground(ColorScheme.DARK_GRAY_COLOR);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(new EmptyBorder(5, 0, 0, 0));
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 3));
        JLabel title = new JLabel(WatchdogPlugin.getInstance().getName());
        title.setFont(title.getFont().deriveFont(Font.BOLD));
        title.setHorizontalAlignment(JLabel.LEFT);
        title.setForeground(Color.WHITE);
        if (WatchdogPlugin.getInstance().isInBannedArea()) {
            title.setForeground(Color.RED);
            String tooltip = "You are in a banned area. Watchdog is disabled in the following areas:\n";
            tooltip += Arrays.stream(Region.values()).map(Region::name).collect(Collectors.joining(", "));
            title.setToolTipText(tooltip);
        } else {
            title.setForeground(Color.WHITE);
            boolean isPreRelease = !PLUGIN_VERSION_PHASE.equals("release") && !PLUGIN_VERSION_PHASE.isEmpty();
            title.setToolTipText("Watchdog v" + (isPreRelease ? PLUGIN_VERSION_FULL : PLUGIN_VERSION));
        }
        titlePanel.add(title);

        JLabel version = new JLabel("v"+PLUGIN_VERSION);
        version.setFont(version.getFont().deriveFont(Font.BOLD, 10f));
        version.setBorder(new EmptyBorder(5, 0, 0, 0));
        titlePanel.add(version);
        topPanel.add(titlePanel);

        JPanel actionButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));

        JButton discordButton = PanelUtils.createActionButton(Icons.DISCORD, Icons.DISCORD_HOVER, "Discord", (btn, modifiers) -> {
            LinkBrowser.browse(DISCORD_URL);
        });
        actionButtons.add(discordButton);

        JButton kofiButton = PanelUtils.createActionButton(Icons.KOFI, Icons.KOFI_HOVER, "Buy me a coffee :)", (btn, modifiers) -> {
            LinkBrowser.browse(KOFI_URL);
        });
        kofiButton.setPreferredSize(new Dimension(17, 17));
        actionButtons.add(kofiButton);

        JButton helpButton = PanelUtils.createActionButton(Icons.HELP, Icons.HELP_HOVER, "Wiki", (btn, modifiers) -> {
            LinkBrowser.browse(HELP_URL);
        });
        actionButtons.add(helpButton);

        JButton configButton = PanelUtils.createActionButton(Icons.CONFIG, Icons.CONFIG_HOVER, "Config", (btn, modifiers) -> {
            WatchdogPlugin.getInstance().openConfiguration();
        });
        actionButtons.add(configButton);

        JButton historyButton = PanelUtils.createActionButton(Icons.TOOLS, Icons.TOOLS_HOVER, "Tools", (btn, modifiers) -> {
            this.muxer.pushState(this.toolsPanelProvider.get());
        });
        actionButtons.add(historyButton);

        JButton alertDropDownButton = PanelUtils.createAlertDropDownButton(createdAlert -> {
            this.alertManager.addAlert(createdAlert, false);
            this.openAlert(createdAlert);
        });
        JPanel addAlertWrapper = new JPanel(new BorderLayout());
        addAlertWrapper.setBorder(new EmptyBorder(0, 5, 0, 0));
        addAlertWrapper.add(alertDropDownButton);
        actionButtons.add(addAlertWrapper);

        topPanel.add(actionButtons, BorderLayout.EAST);

        this.add(topPanel, BorderLayout.NORTH);

        this.alertListPanel = new AlertListPanel(this.alertManager.getAlerts(), null, this::rebuild);
        this.add(this.alertListPanel, BorderLayout.CENTER);

        JPanel importExportGroup = new JPanel(new GridLayout(1, 2, 5, 0));
        JButton importButton = new JButton("Import", Icons.IMPORT);
        importButton.setHorizontalTextPosition(SwingConstants.LEFT);
        importButton.addActionListener(ev -> {
            ImportExportDialog importExportDialog = new ImportExportDialog(
                SwingUtilities.getWindowAncestor(this),
                (json, append) -> this.alertManager.importAlerts(json, this.alertManager.getAlerts(), append, true, this.watchdogConfig.overrideImportsWithDefaults())
            );
            importExportDialog.setVisible(true);
        });
        importExportGroup.add(importButton);
        JButton exportButton = new JButton("Export", Icons.EXPORT);
        exportButton.setHorizontalTextPosition(SwingConstants.LEFT);
        exportButton.addActionListener(ev -> {
            ImportExportDialog importExportDialog = new ImportExportDialog(SwingUtilities.getWindowAncestor(this), this.alertManager.getAlerts());
            importExportDialog.setVisible(true);
        });
        importExportGroup.add(exportButton);

        JPanel bottomPanel = new JPanel(new GridLayout(0, 1, 3, 3));
        bottomPanel.add(importExportGroup);
        JButton hubButton = new JButton("Alert Hub", Icons.DOWNLOAD);
        hubButton.setHorizontalTextPosition(SwingConstants.LEFT);
        hubButton.addActionListener(ev -> {
            AlertHubPanel alertHubPanel = this.alertHubPanelProvider.get();
            this.muxer.pushState(alertHubPanel);
        });
        bottomPanel.add(hubButton);
        this.add(bottomPanel, BorderLayout.SOUTH);

        this.revalidate();
    }

    public void openAlert(Alert alert) {
        PluginPanel panel = this.createPluginPanel(alert);
        if (panel != null) {
            this.muxer.pushState(panel);
        } else {
            log.error(String.format("Tried to open an alert of type %s that doesn't have a panel.", alert.getClass()));
        }
    }

    private PluginPanel createPluginPanel(Alert alert) {
        if (alert instanceof PlayerChatAlert) {
            return new PlayerChatAlertPanel(this, (PlayerChatAlert) alert);
        } else if (alert instanceof ChatAlert) {
            return new GameMessageAlertPanel(this, (ChatAlert) alert);
        } else if (alert instanceof NotificationFiredAlert) {
            return new NotificationFiredAlertPanel(this, (NotificationFiredAlert) alert);
        } else if (alert instanceof StatChangedAlert) {
            return new StatChangedAlertPanel(this, (StatChangedAlert) alert);
        } else if (alert instanceof XPDropAlert) {
            return new XPDropAlertPanel(this, (XPDropAlert) alert);
        } else if (alert instanceof SoundFiredAlert) {
            return new SoundFiredAlertPanel(this, (SoundFiredAlert) alert);
        } else if (alert instanceof SpawnedAlert) {
            return new SpawnedAlertPanel(this, (SpawnedAlert) alert);
        } else if (alert instanceof InventoryAlert) {
            return new InventoryAlertPanel(this, (InventoryAlert) alert);
        } else if (alert instanceof AlertGroup) {
            return new AlertGroupPanel(this, (AlertGroup) alert);
        } else if (alert instanceof LocationAlert) {
            return new LocationAlertPanel(this, (LocationAlert) alert, this.client);
        }

        return null;
    }

    @Override
    public void onActivate() {
        this.rebuild();
    }

    @Override
    public void onDeactivate() {
        WatchdogPlugin.getInstance().getScreenMarkerUtil().finishCreation(true);
    }

    public void scrollToBottom() {
        JScrollBar scrollBar = this.alertListPanel.getScrollPane().getVerticalScrollBar();
        scrollBar.setValue(scrollBar.getMaximum());
    }

    public void pickMessage(Consumer<String> callback, Predicate<MessageNode> filter) {
        Stream<MessageNode> messageStream = WatchdogPlugin.getInstance().getMessageQueue().stream();
        if (filter != null) {
            messageStream = messageStream.filter(filter);
        }
        MessagePickerDialog messagePickerDialog = new MessagePickerDialog(
            SwingUtilities.getWindowAncestor(this),
            messageStream.map(MessageNode::getValue),
            callback
        );
        messagePickerDialog.setVisible(true);
    }

    public void pickNotification(Consumer<String> callback) {
        MessagePickerDialog messagePickerDialog = new MessagePickerDialog(
            SwingUtilities.getWindowAncestor(this),
            WatchdogPlugin.getInstance().getNotificationsQueue().stream()
                .map(NotificationFired::getMessage),
            callback
        );
        messagePickerDialog.setVisible(true);
    }
}
