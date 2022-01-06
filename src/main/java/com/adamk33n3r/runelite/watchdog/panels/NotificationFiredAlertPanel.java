package com.adamk33n3r.runelite.watchdog.panels;

import com.adamk33n3r.runelite.watchdog.Util;
import com.adamk33n3r.runelite.watchdog.WatchdogPlugin;
import com.adamk33n3r.runelite.watchdog.alerts.Alert;
import com.adamk33n3r.runelite.watchdog.alerts.NotificationFiredAlert;
import com.adamk33n3r.runelite.watchdog.notifications.INotification;
import com.adamk33n3r.runelite.watchdog.panels.notifications.NotificationsPanel;
import net.runelite.client.ui.DynamicGridLayout;
import net.runelite.client.ui.MultiplexingPluginPanel;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.List;

public class NotificationFiredAlertPanel extends AlertPanel {
    public NotificationFiredAlertPanel(NotificationFiredAlert notificationFiredAlert, MultiplexingPluginPanel muxer, WatchdogPlugin plugin) {
        super(notificationFiredAlert = Util.defaultArg(notificationFiredAlert, new NotificationFiredAlert("Notification Fired Alert 1")));
        JPanel wrapper = new JPanel(new BorderLayout());
        ScrollablePanel container = new ScrollablePanel(new DynamicGridLayout(0, 1, 3, 3));
        container.setScrollableWidth(ScrollablePanel.ScrollableSizeHint.FIT);
        container.setScrollableBlockIncrement(ScrollablePanel.VERTICAL, ScrollablePanel.IncrementType.PERCENT, 10);
        JScrollPane scroll = new JScrollPane(container, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        wrapper.add(scroll, BorderLayout.CENTER);
        container.setBorder(new TitledBorder(new EtchedBorder(), "Notification Fired Alert", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, Color.WHITE));


        JTextField nameTextField = new JTextField(notificationFiredAlert.getName());
        container.add(PanelUtils.createLabeledComponent("Name", nameTextField));

        JTextArea message = new JTextArea(Util.defaultArg(notificationFiredAlert.getMessage(), "Nothing interesting happens."));
        message.setLineWrap(true);
        message.setWrapStyleWord(true);
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
        NotificationFiredAlert finalNotificationFiredAlert = notificationFiredAlert;
        save.addActionListener(ev -> {
            java.util.List<INotification> notificationList = notificationPanel.getNotifications();
            finalNotificationFiredAlert.setName(nameTextField.getText());
            finalNotificationFiredAlert.setMessage(message.getText());
            finalNotificationFiredAlert.getNotifications().clear();
            finalNotificationFiredAlert.getNotifications().addAll(notificationList);
            List<Alert> alerts = plugin.getAlerts();
            if (!alerts.contains(finalNotificationFiredAlert)) {
                alerts.add(finalNotificationFiredAlert);
            }
            plugin.saveAlerts(alerts);
            muxer.popState();
        });
        buttonPanel.add(save);
        wrapper.add(buttonPanel, BorderLayout.SOUTH);
        this.add(wrapper, BorderLayout.CENTER);
    }
}
