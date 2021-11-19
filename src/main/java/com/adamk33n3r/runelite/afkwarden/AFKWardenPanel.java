package com.adamk33n3r.runelite.afkwarden;

import com.adamk33n3r.runelite.afkwarden.alerts.Alert;
import com.adamk33n3r.runelite.afkwarden.dropdownbutton.DropDownButtonFactory;
import com.adamk33n3r.runelite.afkwarden.panels.ChatAlertPanel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.client.plugins.timetracking.TimeTrackingPlugin;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.DynamicGridLayout;
import net.runelite.client.ui.MultiplexingPluginPanel;
import net.runelite.client.ui.PluginPanel;
import net.runelite.client.util.ImageUtil;
import net.runelite.client.util.SwingUtil;
import org.apache.commons.lang3.StringUtils;

import javax.inject.Inject;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

@Slf4j
public class AFKWardenPanel extends PluginPanel {
    @Getter
    private MultiplexingPluginPanel muxer;
    @Inject
    private Client client;
    @Inject
    private AFKWardenPlugin plugin;

//    private Map<ChatMessageType, String> messageTypeToStringMap = new HashMap<>() {{
//        put(ChatMessageType.CLAN_MESSAGE)
//    }};

    private ChatMonitorFrame chatMonitorFrame;

    private static final ImageIcon ADD_ICON;
    private static final ImageIcon ADD_ICON_HOVER;
    static {
        BufferedImage addIcon = ImageUtil.loadImageResource(TimeTrackingPlugin.class, "add_icon.png");
        ADD_ICON = new ImageIcon(addIcon);
        ADD_ICON_HOVER = new ImageIcon(ImageUtil.alphaOffset(addIcon, 0.53f));
    }

    @Inject
    public void init() {
        log.info("panel init");
        this.muxer = new MultiplexingPluginPanel(this);
        this.rebuild();
    }

    public void rebuild() {
        this.removeAll();
//        this.setLayout(new DynamicGridLayout(0, 1, 3, 3));
        this.setLayout(new BorderLayout(0, 5));
        this.setBackground(ColorScheme.DARK_GRAY_COLOR);

        JPanel newAlertPanel = new JPanel(new BorderLayout());
        JLabel title = new JLabel("AFK Warden");
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
        for (TriggerType tType : TriggerType.values()) {
            JMenuItem c = new JMenuItem(StringUtils.capitalize(StringUtils.lowerCase(tType.name())));
            c.putClientProperty(TriggerType.class, tType);
            c.addActionListener(actionListener);
            popupMenu.add(c);
        }
        JButton inspector = new JButton("Inspector");
        this.chatMonitorFrame = new ChatMonitorFrame();
        inspector.addActionListener(ev -> {
            this.chatMonitorFrame.open();
        });
        newAlertPanel.add(inspector);
        JButton addDropDownButton = DropDownButtonFactory.createDropDownButton(ADD_ICON, popupMenu, true);
        addDropDownButton.setToolTipText("Create New Alert");
        newAlertPanel.add(addDropDownButton, BorderLayout.EAST);
        this.add(newAlertPanel, BorderLayout.NORTH);

        JPanel alertPanel = new JPanel(new GridLayout(0, 1, 3, 3));
        for (Alert alert : this.plugin.getAlerts()) {
            log.info(alert.toString());
            final JButton alertButton = new JButton(alert.getName());
            alertButton.setPreferredSize(new Dimension(10, 100));
            alertPanel.add(alertButton);
        }
        this.add(new JScrollPane(alertPanel), BorderLayout.CENTER);
    }

    private void createAlert(TriggerType triggerType) {
        switch (triggerType) {
            case CHAT:
                // TODO: look into using DI for chat alert panel. Will need capability to send existing alert
                this.muxer.pushState(new ChatAlertPanel(this.muxer, this.plugin));
                break;
            case IDLE:
                break;
        }
    }
}
