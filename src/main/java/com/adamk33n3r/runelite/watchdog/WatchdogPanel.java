package com.adamk33n3r.runelite.watchdog;

import com.adamk33n3r.runelite.watchdog.alerts.*;
import com.adamk33n3r.runelite.watchdog.ui.AlertListItem;
import com.adamk33n3r.runelite.watchdog.ui.ImportExportDialog;
import com.adamk33n3r.runelite.watchdog.ui.alerts.*;
import com.adamk33n3r.runelite.watchdog.ui.panels.AlertListPanel;
import com.adamk33n3r.runelite.watchdog.ui.panels.HistoryPanel;
import com.adamk33n3r.runelite.watchdog.ui.panels.PanelUtils;

import net.runelite.client.plugins.config.ConfigPlugin;
import net.runelite.client.plugins.info.InfoPanel;
import net.runelite.client.plugins.timetracking.TimeTrackingPlugin;
import net.runelite.client.ui.MultiplexingPluginPanel;
import net.runelite.client.ui.PluginPanel;
import net.runelite.client.ui.components.DragAndDropReorderPane;
import net.runelite.client.util.ImageUtil;
import net.runelite.client.util.LinkBrowser;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
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

    @Getter
    private final MultiplexingPluginPanel muxer = new MultiplexingPluginPanel(this);

    @Getter
    @Inject
    private Provider<HistoryPanel> historyPanelProvider;

    @Inject
    private AlertManager alertManager;

    public static final ImageIcon ADD_ICON;
    public static final ImageIcon HELP_ICON;
    public static final ImageIcon HELP_ICON_HOVER;
    public static final ImageIcon HISTORY_ICON;
    public static final ImageIcon HISTORY_ICON_HOVER;
    public static final ImageIcon DISCORD_ICON;
    public static final ImageIcon DISCORD_ICON_HOVER;
    public static final ImageIcon KOFI_ICON;
    public static final ImageIcon KOFI_ICON_HOVER;
    public static final ImageIcon CONFIG_ICON;
    public static final ImageIcon CONFIG_ICON_HOVER;
    public static final ImageIcon EXPORT_ICON = new ImageIcon(ImageUtil.loadImageResource(ConfigPlugin.class, "mdi_export.png"));
    public static final ImageIcon IMPORT_ICON = new ImageIcon(ImageUtil.loadImageResource(WatchdogPanel.class, "mdi_import.png"));

    static {
        final BufferedImage addIcon = ImageUtil.loadImageResource(TimeTrackingPlugin.class, "add_icon.png");
        ADD_ICON = new ImageIcon(addIcon);

        final BufferedImage helpIcon = ImageUtil.loadImageResource(WatchdogPanel.class, "help_icon.png");
        HELP_ICON = new ImageIcon(helpIcon);
        HELP_ICON_HOVER = new ImageIcon(ImageUtil.luminanceOffset(helpIcon, -100));

        final BufferedImage historyIcon = ImageUtil.loadImageResource(WatchdogPanel.class, "history_icon.png");
        HISTORY_ICON = new ImageIcon(ImageUtil.luminanceOffset(historyIcon, -40));
        HISTORY_ICON_HOVER = new ImageIcon(ImageUtil.luminanceOffset(historyIcon, -160));

        final BufferedImage discordIcon = ImageUtil.loadImageResource(InfoPanel.class, "discord_icon.png");
        DISCORD_ICON = new ImageIcon(discordIcon);
        DISCORD_ICON_HOVER = new ImageIcon(ImageUtil.luminanceOffset(discordIcon, -100));

        final BufferedImage kofiIcon = ImageUtil.loadImageResource(WatchdogPanel.class, "kofi_icon.png");
        KOFI_ICON = new ImageIcon(kofiIcon);
        KOFI_ICON_HOVER = new ImageIcon(ImageUtil.luminanceOffset(kofiIcon, -100));

        final BufferedImage configIcon = ImageUtil.loadImageResource(ConfigPlugin.class, "config_edit_icon.png");
        CONFIG_ICON = new ImageIcon(configIcon);
        CONFIG_ICON_HOVER = new ImageIcon(ImageUtil.luminanceOffset(configIcon, -100));
    }

    public void rebuild() {
        this.removeAll();
        this.setLayout(new BorderLayout(0, 5));

        JPanel topPanel = new JPanel(new BorderLayout());
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        titlePanel.setBorder(new EmptyBorder(5, 0, 0, 0));
        JLabel title = new JLabel(WatchdogPlugin.getInstance().getName());
        title.setFont(title.getFont().deriveFont(Font.BOLD));
        title.setHorizontalAlignment(JLabel.LEFT);
        title.setForeground(Color.WHITE);
        title.setToolTipText("Watchdog v" + PLUGIN_VERSION);
        titlePanel.add(title);
        JLabel version = new JLabel("v" + PLUGIN_VERSION);
        version.setFont(version.getFont().deriveFont(10f));
        version.setBorder(new EmptyBorder(5, 0, 0, 0));
        titlePanel.add(version);
        topPanel.add(titlePanel);

        JPanel actionButtons = new JPanel();

        JButton discordButton = PanelUtils.createActionButton(DISCORD_ICON, DISCORD_ICON_HOVER, "Discord", (btn, modifiers) -> {
            LinkBrowser.browse(DISCORD_URL);
        });
        actionButtons.add(discordButton);

        JButton kofiButton = PanelUtils.createActionButton(KOFI_ICON, KOFI_ICON_HOVER, "Buy me a coffee :)", (btn, modifiers) -> {
            LinkBrowser.browse(KOFI_URL);
        });
        kofiButton.setPreferredSize(new Dimension(17, 17));
        actionButtons.add(kofiButton);

        JButton helpButton = PanelUtils.createActionButton(HELP_ICON, HELP_ICON_HOVER, "Wiki", (btn, modifiers) -> {
            LinkBrowser.browse(HELP_URL);
        });
        actionButtons.add(helpButton);

        JButton configButton = PanelUtils.createActionButton(CONFIG_ICON, CONFIG_ICON_HOVER, "Config", (btn, modifiers) -> {
            WatchdogPlugin.getInstance().openConfiguration();
        });
        actionButtons.add(configButton);

        JButton historyButton = PanelUtils.createActionButton(HISTORY_ICON, HISTORY_ICON_HOVER, "History", (btn, modifiers) -> {
            this.muxer.pushState(this.historyPanelProvider.get());
//            System.out.println(this.alertManager.getAllEnabledAlertsOfType(InventoryAlert.class).count());
//            this.alertManager.getAllEnabledAlertsOfType(InventoryAlert.class)
//                .forEach(alert -> {
//                    List<AlertGroup> ancestors = alert.getAncestors();
//                    if (ancestors != null) {
//                        String isEnabled = ancestors.stream().allMatch(Alert::isEnabled) ? "Enabled: " : "Disabled: ";
//                        System.out.print(isEnabled);
//                        String ancestorStr = ancestors.stream().map(ancestor -> ancestor.getName() + (ancestor.isEnabled() ? "*" : "")).collect(Collectors.joining(" -> "));
//                        System.out.print(ancestorStr);
//                        System.out.print(" -> ");
//                    } else {
//                        System.out.print(alert.isEnabled() ? "Enabled: " : "Disabled: ");
//                    }
//                    System.out.print(alert.getName());
//                    System.out.println();
//                });
        });
        actionButtons.add(historyButton);

        JButton alertDropDownButton = PanelUtils.createAlertDropDownButton(createdAlert -> {
            this.alertManager.addAlert(createdAlert);
            this.openAlert(createdAlert);
        });
        actionButtons.add(alertDropDownButton);

        topPanel.add(actionButtons, BorderLayout.EAST);

        this.add(topPanel, BorderLayout.NORTH);

        DragAndDropReorderPane dragAndDropReorderPane = new DragAndDropReorderPane();
        dragAndDropReorderPane.addDragListener((c) -> {
            int pos = dragAndDropReorderPane.getPosition(c);
            AlertListItem alertListItem = (AlertListItem) c;
//            log.debug("drag listener: " + alertListItem.getAlert().getName() + " to " + pos);
            alertManager.moveAlertTo(alertListItem.getAlert(), pos);
        });

        AlertListPanel alertPanel = new AlertListPanel(this.alertManager.getAlerts(), dragAndDropReorderPane, this::rebuild);
        this.add(new JScrollPane(alertPanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER), BorderLayout.CENTER);

        JPanel importExportGroup = new JPanel(new GridLayout(1, 2, 5, 0));
        JButton importButton = new JButton("Import", IMPORT_ICON);
        importButton.setHorizontalTextPosition(SwingConstants.LEFT);
        importButton.addActionListener(ev -> {
            ImportExportDialog importExportDialog = new ImportExportDialog(
                SwingUtilities.getWindowAncestor(this),
                (json, append) -> WatchdogPlugin.getInstance().getAlertManager().importAlerts(json, this.alertManager.getAlerts(), append, true)
            );
            importExportDialog.setVisible(true);
        });
        importExportGroup.add(importButton);
        JButton exportButton = new JButton("Export", EXPORT_ICON);
        exportButton.setHorizontalTextPosition(SwingConstants.LEFT);
        exportButton.addActionListener(ev -> {
            ImportExportDialog importExportDialog = new ImportExportDialog(SwingUtilities.getWindowAncestor(this), WatchdogPlugin.getInstance().getConfig().alerts());
            importExportDialog.setVisible(true);
        });
        importExportGroup.add(exportButton);
        this.add(importExportGroup, BorderLayout.SOUTH);

        // Need this for rebuild for some reason
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
        if (alert instanceof ChatAlert) {
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
        }

        return null;
    }

    @Override
    public void onActivate() {
        this.rebuild();
    }
}
