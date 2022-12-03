package com.adamk33n3r.runelite.watchdog;

import com.adamk33n3r.runelite.watchdog.alerts.*;
import com.adamk33n3r.runelite.watchdog.ui.ImportExportDialog;
import com.adamk33n3r.runelite.watchdog.ui.dropdownbutton.DropDownButtonFactory;
import com.adamk33n3r.runelite.watchdog.ui.AlertListItem;
import com.adamk33n3r.runelite.watchdog.ui.panels.AlertPanel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Skill;
import net.runelite.client.plugins.timetracking.TimeTrackingPlugin;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.DynamicGridLayout;
import net.runelite.client.ui.MultiplexingPluginPanel;
import net.runelite.client.ui.PluginPanel;
import net.runelite.client.util.ImageUtil;
import org.apache.commons.text.WordUtils;

import javax.inject.Inject;
import javax.inject.Named;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.Arrays;

@Slf4j
public class WatchdogPanel extends PluginPanel {
    @Inject
    @Named("wikiPage.soundIDs")
    private String SOUND_ID_WIKI_PAGE;

    @Getter
    private final MultiplexingPluginPanel muxer = new MultiplexingPluginPanel(this);

    @Inject
    private AlertManager alertManager;

    public static final ImageIcon ADD_ICON;
    private static final ImageIcon ADD_ICON_HOVER;
    static {
        BufferedImage addIcon = ImageUtil.loadImageResource(TimeTrackingPlugin.class, "add_icon.png");
        ADD_ICON = new ImageIcon(addIcon);
        ADD_ICON_HOVER = new ImageIcon(ImageUtil.alphaOffset(addIcon, 0.53f));
    }

    public void rebuild() {
        this.removeAll();
        this.setLayout(new BorderLayout(0, 5));
        this.setBackground(ColorScheme.DARK_GRAY_COLOR);

        JPanel newAlertPanel = new JPanel(new BorderLayout());
        JLabel title = new JLabel("Watchdog");
        title.setFont(title.getFont().deriveFont(Font.BOLD));
        title.setHorizontalAlignment(JLabel.LEFT);
        title.setForeground(Color.WHITE);
        newAlertPanel.add(title);

        JPopupMenu popupMenu = new JPopupMenu();
        ActionListener actionListener = e -> {
            JMenuItem menuItem = (JMenuItem) e.getSource();
            TriggerType tType = (TriggerType) menuItem.getClientProperty(TriggerType.class);
            this.createAlert(tType);
        };

        Arrays.stream(TriggerType.values())
            .filter(tt -> tt != TriggerType.IDLE && tt != TriggerType.RESOURCE).forEach(tType -> {
                JMenuItem c = new JMenuItem(WordUtils.capitalizeFully(tType.name().replaceAll("_", " ")));
                c.putClientProperty(TriggerType.class, tType);
                c.addActionListener(actionListener);
                popupMenu.add(c);
            });
        JButton addDropDownButton = DropDownButtonFactory.createDropDownButton(ADD_ICON, popupMenu);
        addDropDownButton.setToolTipText("Create New Alert");
        newAlertPanel.add(addDropDownButton, BorderLayout.EAST);
        this.add(newAlertPanel, BorderLayout.NORTH);

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
        Alert createdAlert = null;
        switch (triggerType) {
            case CHAT:
                createdAlert = new ChatAlert();
                break;
            case NOTIFICATION_FIRED:
                createdAlert = new NotificationFiredAlert();
                break;
            case STAT_DRAIN:
                createdAlert = new StatDrainAlert();
                break;
            case IDLE:
                createdAlert = new IdleAlert();
                break;
            case RESOURCE:
                createdAlert = new ResourceAlert();
                break;
            case SOUND_FIRED:
                createdAlert = new SoundFiredAlert();
                break;
        }

        if (createdAlert != null) {
            this.alertManager.addAlert(createdAlert);
            this.openAlert(createdAlert);
        }
    }

    private PluginPanel createPluginPanel(Alert alert) {
        if (alert instanceof ChatAlert) {
            ChatAlert chatAlert = (ChatAlert) alert;
            return AlertPanel.create(this.muxer, alert)
                .addLabel("<html>Will not trigger on<br>player chat messages</html>")
                .addAlertDefaults(alert)
                .addTextArea("Message", "The message to trigger on. Supports glob (*)", chatAlert.getMessage(), chatAlert::setMessage)
                .build();
        } else if (alert instanceof IdleAlert) {
            IdleAlert idleAlert = (IdleAlert) alert;
            return AlertPanel.create(this.muxer, alert)
                .addAlertDefaults(alert)
                .addSelect("Action", "Action to trigger alert when stop", IdleAlert.IdleAction.class, idleAlert.getIdleAction(), idleAlert::setIdleAction)
                .build();
        } else if (alert instanceof NotificationFiredAlert) {
            NotificationFiredAlert notificationFiredAlert = (NotificationFiredAlert) alert;
            return AlertPanel.create(this.muxer, alert)
                .addAlertDefaults(alert)
                .addTextArea("Message", "The notification message to trigger on. Supports glob (*)", notificationFiredAlert.getMessage(), notificationFiredAlert::setMessage)
                .build();
        } else if (alert instanceof ResourceAlert) {
            ResourceAlert resourceAlert = (ResourceAlert) alert;
            return AlertPanel.create(this.muxer, alert)
                .addAlertDefaults(alert)
                .addSelect("Resource", "The resource to trigger the alert when low", ResourceAlert.ResourceType.class, resourceAlert.getResourceType(), resourceAlert::setResourceType)
                .build();
        } else if (alert instanceof StatDrainAlert) {
            StatDrainAlert statDrainAlert = (StatDrainAlert) alert;
            return AlertPanel.create(this.muxer, alert)
                .addAlertDefaults(alert)
                .addSelect("Skill", "The skill to track", Skill.class, statDrainAlert.getSkill(), statDrainAlert::setSkill)
                .addSpinner("Drain Amount", "The difference in level to trigger the alert. Can be negative for stat gain", statDrainAlert.getDrainAmount(), statDrainAlert::setDrainAmount)
                .build();
        } else if (alert instanceof  SoundFiredAlert) {
            SoundFiredAlert soundFiredAlert = (SoundFiredAlert) alert;
            return AlertPanel.create(this.muxer, alert)
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
