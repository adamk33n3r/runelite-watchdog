package com.adamk33n3r.runelite.watchdog.panels;

import com.adamk33n3r.runelite.watchdog.WatchdogPlugin;
import com.adamk33n3r.runelite.watchdog.Util;
import com.adamk33n3r.runelite.watchdog.alerts.Alert;
import com.adamk33n3r.runelite.watchdog.alerts.ChatAlert;
import com.adamk33n3r.runelite.watchdog.notifications.INotification;
import com.adamk33n3r.runelite.watchdog.panels.notifications.NotificationsPanel;
import net.runelite.api.ChatMessageType;
import net.runelite.client.ui.DynamicGridLayout;
import net.runelite.client.ui.MultiplexingPluginPanel;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.List;

public class ChatAlertPanel extends AlertPanel {
    public ChatAlertPanel(ChatAlert chatAlert, MultiplexingPluginPanel muxer, WatchdogPlugin plugin) {
//        new DefaultComboBoxModel<ChatMessageType>();
        // Can get list of alerts from plugin and then count the chat alerts to find the number
        // TODO: will also need to have an id in the parent Alert class so that we can edit alerts
        // or maybe not cause it's just editing the element in the array?
        super(chatAlert = Util.defaultArg(chatAlert, new ChatAlert("Chat Alert 1")));
        JPanel wrapper = new JPanel(new BorderLayout());
        ScrollablePanel container = new ScrollablePanel(new DynamicGridLayout(0, 1, 3, 3));
        container.setScrollableWidth(ScrollablePanel.ScrollableSizeHint.FIT);
        container.setScrollableBlockIncrement(ScrollablePanel.VERTICAL, ScrollablePanel.IncrementType.PERCENT, 10);
        JScrollPane scroll = new JScrollPane(container, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        wrapper.add(scroll, BorderLayout.CENTER);
//        container.setPreferredSize(new Dimension(PluginPanel.PANEL_WIDTH, 300));
        container.setBorder(new TitledBorder(new EtchedBorder(), "Chat Alert", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, Color.WHITE));
//        JLabel title = new JLabel("Chat Alert");
//        title.setForeground(Color.WHITE);
//        container.add(title);

        JTextField nameTextField = new JTextField(chatAlert.getName());
        container.add(PanelUtils.createLabeledComponent("Name", nameTextField));

        JComboBox<ChatMessageType> messageType = new JComboBox<>(ChatMessageType.values());
        messageType.setSelectedItem(ChatMessageType.GAMEMESSAGE);
        messageType.setRenderer((list, value, index, isSelected, cellHasFocus) -> {
//            String titleized = StringUtils.capitalize(StringUtils.lowerCase(value.name()));
            list.setToolTipText(value.name());
            return new DefaultListCellRenderer().getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        });
//        container.add(PanelUtils.createLabeledComponent("Message Type", messageType));
        JTextArea message = new JTextArea(Util.defaultArg(chatAlert.getMessage(), "Nothing interesting happens."));
        message.setLineWrap(true);
        message.setWrapStyleWord(true);
//        message.setBorder(BorderFactory.createCompoundBorder());
        message.setMargin(new Insets(4, 6, 5, 6));
        container.add(PanelUtils.createLabeledComponent("Message", message));

        NotificationsPanel notificationPanel = new NotificationsPanel(alert.getNotifications());
        notificationPanel.setBorder(new TitledBorder(new EtchedBorder(), "Notifications", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, Color.WHITE));
        container.add(notificationPanel);

        JPanel buttonPanel = new JPanel(new DynamicGridLayout(1, 2, 3, 3));
        JButton back = new JButton("Back");
        back.addActionListener(ev -> {
            muxer.popState();
        });
        buttonPanel.add(back);
        JButton save = new JButton("Save");
        ChatAlert finalChatAlert = chatAlert;
        save.addActionListener(ev -> {
            List<INotification> notificationList = notificationPanel.getNotifications();
            finalChatAlert.setName(nameTextField.getText());
            finalChatAlert.setChatMessageType(messageType.getItemAt(messageType.getSelectedIndex()));
            finalChatAlert.setMessage(message.getText());
            finalChatAlert.getNotifications().clear();
            finalChatAlert.getNotifications().addAll(notificationList);
            List<Alert> alerts = plugin.getAlerts();
            if (!alerts.contains(finalChatAlert)) {
                alerts.add(finalChatAlert);
            }
            plugin.saveAlerts(alerts);
            muxer.popState();
        });
        buttonPanel.add(save);
        wrapper.add(buttonPanel, BorderLayout.SOUTH);
        this.add(wrapper, BorderLayout.CENTER);
    }
}
