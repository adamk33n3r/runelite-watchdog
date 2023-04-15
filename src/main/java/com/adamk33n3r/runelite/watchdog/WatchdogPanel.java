package com.adamk33n3r.runelite.watchdog;

import com.adamk33n3r.runelite.watchdog.alerts.Alert;
import com.adamk33n3r.runelite.watchdog.alerts.ChatAlert;
import com.adamk33n3r.runelite.watchdog.alerts.NotificationFiredAlert;
import com.adamk33n3r.runelite.watchdog.alerts.SoundFiredAlert;
import com.adamk33n3r.runelite.watchdog.alerts.StatChangedAlert;
import com.adamk33n3r.runelite.watchdog.alerts.XPDropAlert;
import com.adamk33n3r.runelite.watchdog.ui.AlertListItem;
import com.adamk33n3r.runelite.watchdog.ui.ImportExportDialog;
import com.adamk33n3r.runelite.watchdog.ui.dropdownbutton.DropDownButtonFactory;
import com.adamk33n3r.runelite.watchdog.ui.panels.AlertPanel;
import com.adamk33n3r.runelite.watchdog.ui.panels.HistoryPanel;
import com.adamk33n3r.runelite.watchdog.ui.panels.PanelUtils;

import net.runelite.api.Skill;
import net.runelite.client.plugins.config.ConfigPlugin;
import net.runelite.client.plugins.info.InfoPanel;
import net.runelite.client.plugins.timetracking.TimeTrackingPlugin;
import net.runelite.client.ui.DynamicGridLayout;
import net.runelite.client.ui.MultiplexingPluginPanel;
import net.runelite.client.ui.PluginPanel;
import net.runelite.client.util.ImageUtil;
import net.runelite.client.util.LinkBrowser;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.text.WordUtils;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

@Slf4j
public class WatchdogPanel extends PluginPanel {
    @Inject
    @Named("watchdog.wikiPage.soundIDs")
    private String SOUND_ID_WIKI_PAGE;

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
    public static final ImageIcon REGEX_ICON;
    public static final ImageIcon REGEX_ICON_HOVER;
    public static final ImageIcon REGEX_SELECTED_ICON;
    public static final ImageIcon REGEX_SELECTED_ICON_HOVER;

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

        final BufferedImage regexIcon = ImageUtil.loadImageResource(AlertPanel.class, "regex_icon.png");
        final BufferedImage regexIconSelected = ImageUtil.loadImageResource(AlertPanel.class, "regex_icon_selected.png");
        REGEX_ICON = new ImageIcon(ImageUtil.luminanceOffset(regexIcon, -80));
        REGEX_ICON_HOVER = new ImageIcon(ImageUtil.luminanceOffset(regexIcon, -120));
        REGEX_SELECTED_ICON = new ImageIcon(regexIconSelected);
        REGEX_SELECTED_ICON_HOVER = new ImageIcon(ImageUtil.luminanceOffset(regexIconSelected, -80));
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
        titlePanel.add(title);
        JLabel version = new JLabel("v"+PLUGIN_VERSION);
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
        });
        actionButtons.add(historyButton);

        JPopupMenu popupMenu = new JPopupMenu();
        ActionListener actionListener = e -> {
            JMenuItem menuItem = (JMenuItem) e.getSource();
            TriggerType tType = (TriggerType) menuItem.getClientProperty(TriggerType.class);
            this.createAlert(tType);
        };

        Arrays.stream(TriggerType.values())
            .forEach(tType -> {
                JMenuItem c = new JMenuItem(WordUtils.capitalizeFully(tType.name().replaceAll("_", " ")));
                c.setToolTipText(tType.getTooltip());
                c.putClientProperty(TriggerType.class, tType);
                c.addActionListener(actionListener);
                popupMenu.add(c);
            });
        JButton addDropDownButton = DropDownButtonFactory.createDropDownButton(ADD_ICON, popupMenu);
        addDropDownButton.setToolTipText("Create New Alert");
        actionButtons.add(addDropDownButton);

        topPanel.add(actionButtons, BorderLayout.EAST);

        this.add(topPanel, BorderLayout.NORTH);

        JPanel alertPanel = new JPanel(new BorderLayout());
        JPanel alertWrapperWrapper = new JPanel(new DynamicGridLayout(0, 1, 3, 3));
        alertPanel.add(alertWrapperWrapper, BorderLayout.NORTH);
        for (Alert alert : this.alertManager.getAlerts()) {
            AlertListItem alertListItem = new AlertListItem(this, this.alertManager, alert);
            alertWrapperWrapper.add(alertListItem);
        }
        this.add(new JScrollPane(alertPanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER), BorderLayout.CENTER);

        JPanel importExportGroup = new JPanel(new GridLayout(1, 2, 5, 0));
        JButton importButton = new JButton("Import");
        importButton.addActionListener(ev -> {
            ImportExportDialog importExportDialog = new ImportExportDialog(SwingUtilities.getWindowAncestor(this));
            importExportDialog.setVisible(true);
        });
        importExportGroup.add(importButton);
        JButton exportButton = new JButton("Export");
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

    private void createAlert(TriggerType triggerType) {
        Alert createdAlert = WatchdogPlugin.getInstance().getInjector().getInstance(triggerType.getImplClass());
        this.alertManager.addAlert(createdAlert);
        this.openAlert(createdAlert);
    }

    private boolean isPatternInvalid(String pattern, boolean isRegex) {
        try {
            Pattern.compile(isRegex ? pattern : Util.createRegexFromGlob(pattern));
            return false;
        } catch (PatternSyntaxException ex) {
            JLabel errorLabel = new JLabel("<html>" + ex.getMessage().replaceAll("\n", "<br/>").replaceAll(" ", "&nbsp;") + "</html>");
            errorLabel.setFont(new Font("Monospaced", Font.PLAIN, 12));
            JOptionPane.showMessageDialog(this, errorLabel, "Error in regex/pattern", JOptionPane.ERROR_MESSAGE);
            return true;
        }
    }

    private PluginPanel createPluginPanel(Alert alert) {
        if (alert instanceof ChatAlert) {
            ChatAlert chatAlert = (ChatAlert) alert;
            return AlertPanel.create(this.muxer, alert)
                .addAlertDefaults(alert)
                .addInputGroupWithSuffix(
                    PanelUtils.createTextArea("Enter the message to trigger on...", "The message to trigger on. Supports glob (*)", chatAlert.getMessage(), msg -> {
                        // Check pattern compile
                        if (this.isPatternInvalid(msg, chatAlert.isRegexEnabled()))
                            return;
                        chatAlert.setMessage(msg);
                        this.alertManager.saveAlerts();
                    }),
                    PanelUtils.createToggleActionButton(
                        REGEX_SELECTED_ICON,
                        REGEX_SELECTED_ICON_HOVER,
                        REGEX_ICON,
                        REGEX_ICON_HOVER,
                        "Disable regex",
                        "Enable regex",
                        chatAlert.isRegexEnabled(),
                        (btn, modifiers) -> {
                            chatAlert.setRegexEnabled(btn.isSelected());
                            this.alertManager.saveAlerts();
                        }
                    )
                )
                .addLabel("<html><i>Note: Will not trigger on<br>player chat messages</i></html>")
                .build();
        } else if (alert instanceof NotificationFiredAlert) {
            NotificationFiredAlert notificationFiredAlert = (NotificationFiredAlert) alert;
            return AlertPanel.create(this.muxer, alert)
                .addAlertDefaults(alert)
                .addInputGroupWithSuffix(
                    PanelUtils.createTextArea("Enter the message to trigger on...", "The message to trigger on. Supports glob (*)", notificationFiredAlert.getMessage(), msg -> {
                        if (this.isPatternInvalid(msg, notificationFiredAlert.isRegexEnabled()))
                            return;
                        notificationFiredAlert.setMessage(msg);
                        this.alertManager.saveAlerts();
                    }),
                    PanelUtils.createToggleActionButton(
                        REGEX_SELECTED_ICON,
                        REGEX_SELECTED_ICON_HOVER,
                        REGEX_ICON,
                        REGEX_ICON_HOVER,
                        "Disable regex",
                        "Enable regex",
                        notificationFiredAlert.isRegexEnabled(),
                        (btn, modifiers) -> {
                            notificationFiredAlert.setRegexEnabled(btn.isSelected());
                            this.alertManager.saveAlerts();
                        }
                    )
                )
                .build();
        } else if (alert instanceof StatChangedAlert) {
            StatChangedAlert statChangedAlert = (StatChangedAlert) alert;
            return AlertPanel.create(this.muxer, alert)
                .addAlertDefaults(alert)
                .addSelect("Skill", "The skill to track", Skill.class, statChangedAlert.getSkill(), statChangedAlert::setSkill)
                .addSpinner("Changed Amount", "The difference in level to trigger the alert. Can be positive for boost and negative for drain", statChangedAlert.getChangedAmount(), statChangedAlert::setChangedAmount)
                .build();
        } else if (alert instanceof XPDropAlert) {
            XPDropAlert xpDropAlert = (XPDropAlert) alert;
            return AlertPanel.create(this.muxer, alert)
                .addAlertDefaults(alert)
                .addSelect("Skill", "The skill to track", Skill.class, xpDropAlert.getSkill(), xpDropAlert::setSkill)
                .addSpinner("Gained Amount", "How much xp needed to trigger this alert", xpDropAlert.getGainedAmount(), xpDropAlert::setGainedAmount)
                .build();
        } else if (alert instanceof  SoundFiredAlert) {
            SoundFiredAlert soundFiredAlert = (SoundFiredAlert) alert;
            return AlertPanel.create(this.muxer, alert)
                .addAlertDefaults(alert)
                .addRichTextPane("<html>Go to <a href='" + SOUND_ID_WIKI_PAGE + "'>this wiki page</a> to get a list<br>of sound ids</html>")
                .addSpinner("Sound ID", "The ID of the sound", soundFiredAlert.getSoundID(), soundFiredAlert::setSoundID, 0, 99999, 1)
                .build();
        }

        return null;
    }

    @Override
    public void onActivate() {
        this.rebuild();
    }
}
