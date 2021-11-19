package com.adamk33n3r.runelite.afkwarden.panels;

import com.adamk33n3r.runelite.afkwarden.notifications.GameMessage;
import com.adamk33n3r.runelite.afkwarden.notifications.INotification;
import lombok.Getter;
import net.runelite.api.Client;

import javax.swing.*;

public class NotificationPanel extends JPanel {
    @Getter
    private final GameMessage notification;
    public NotificationPanel() {
        this.notification = new GameMessage();
        this.notification.message = "Hey! Wake up!";
        JTextField notificationMessage = new JTextField("Hey! Wake up!");
        notificationMessage.addActionListener(ev -> {
            ((GameMessage)this.notification).message = notificationMessage.getText();
        });
        this.add(PanelUtils.createLabeledComponent("Message", notificationMessage));
    }
}
