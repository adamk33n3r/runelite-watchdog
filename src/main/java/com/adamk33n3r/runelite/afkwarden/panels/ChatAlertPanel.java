package com.adamk33n3r.runelite.afkwarden.panels;

import com.adamk33n3r.runelite.afkwarden.AFKWardenPlugin;
import com.adamk33n3r.runelite.afkwarden.alerts.Alert;
import com.adamk33n3r.runelite.afkwarden.alerts.ChatAlert;
import com.adamk33n3r.runelite.afkwarden.notifications.INotification;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.client.ui.DynamicGridLayout;
import net.runelite.client.ui.MultiplexingPluginPanel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ChatAlertPanel extends AlertPanel {
    public ChatAlertPanel(MultiplexingPluginPanel muxer, AFKWardenPlugin plugin) {
//        new DefaultComboBoxModel<ChatMessageType>();
        this.setLayout(new BorderLayout());
        this.setBorder(new EmptyBorder(10, 10, 10, 10));
        JPanel container = new JPanel(new DynamicGridLayout(0, 1, 3, 3));
        container.setBorder(new TitledBorder(new EtchedBorder(), "Chat Alert", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, Color.WHITE));
//        JLabel title = new JLabel("Chat Alert");
//        title.setForeground(Color.WHITE);
//        container.add(title);

        // Can get list of alerts from plugin and then count the chat alerts to find the number
        // TODO: will also need to have an id in the parent Alert class so that we can edit alerts
        JTextField nameTextField = new JTextField("Chat Alert 1");
        container.add(PanelUtils.createLabeledComponent("Name", nameTextField));

        JComboBox<ChatMessageType> messageType = new JComboBox<>(ChatMessageType.values());
        messageType.setSelectedItem(ChatMessageType.GAMEMESSAGE);
        messageType.setRenderer((list, value, index, isSelected, cellHasFocus) -> {
//            String titleized = StringUtils.capitalize(StringUtils.lowerCase(value.name()));
            list.setToolTipText(value.name());
            return new DefaultListCellRenderer().getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        });
        container.add(PanelUtils.createLabeledComponent("Message Type", messageType));
        JTextArea message = new JTextArea("Nothing interesting happens.");
        message.setLineWrap(true);
        message.setWrapStyleWord(true);
//        message.setBorder(BorderFactory.createCompoundBorder());
        message.setMargin(new Insets(4, 6, 5, 6));
        container.add(PanelUtils.createLabeledComponent("Message", message));

        JPanel notifications = new JPanel();
        notifications.setBorder(new TitledBorder(new EtchedBorder(), "Notifications", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, Color.WHITE));
        NotificationPanel notificationPanel = new NotificationPanel();
        notifications.add(notificationPanel);
        container.add(notifications);

        JPanel buttonPanel = new JPanel(new DynamicGridLayout(1, 2, 3, 3));
        JButton back = new JButton("Back");
        back.addActionListener(ev -> {
            muxer.popState();
        });
        buttonPanel.add(back);
        JButton save = new JButton("Save");
        save.addActionListener(ev -> {
            List<INotification> notificationList = Arrays.stream(notifications.getComponents())
                .filter(comp -> comp instanceof NotificationPanel)
                .map(comp -> ((NotificationPanel) comp).getNotification())
                .collect(Collectors.toList());
            List<Alert> alerts = plugin.getAlerts();
            ChatAlert chatAlert = new ChatAlert(nameTextField.getText());
            chatAlert.setChatMessageType(messageType.getItemAt(messageType.getSelectedIndex()));
            chatAlert.setMessage(message.getText());
            chatAlert.getNotifications().addAll(notificationList);
            alerts.add(chatAlert);
            plugin.saveAlerts(alerts);
            muxer.popState();
        });
        buttonPanel.add(save);
        container.add(buttonPanel);
        this.add(container, BorderLayout.NORTH);
    }
}
