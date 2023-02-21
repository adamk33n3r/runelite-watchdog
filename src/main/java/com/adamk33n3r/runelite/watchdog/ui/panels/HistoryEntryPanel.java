package com.adamk33n3r.runelite.watchdog.ui.panels;

import com.adamk33n3r.runelite.watchdog.Util;
import com.adamk33n3r.runelite.watchdog.alerts.Alert;
import com.adamk33n3r.runelite.watchdog.notifications.MessageNotification;

import net.runelite.client.ui.DynamicGridLayout;

import lombok.Getter;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.border.EtchedBorder;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class HistoryEntryPanel extends JPanel {
    @Getter
    private final Alert alert;

    public HistoryEntryPanel(Alert alert, String[] triggerValues) {
        super(new DynamicGridLayout(0, 1, 3, 3));
        this.alert = alert;
        this.setBorder(new EtchedBorder());

        JLabel alertType = new JLabel(alert.getType().getName());
        this.add(alertType);
        JLabel alertName = new JLabel(alert.getName());
        this.add(alertName);
        alert.getNotifications().stream()
            .filter(notification -> notification instanceof MessageNotification)
            .map(notification -> (MessageNotification) notification)
            .forEach(notification -> {
                String message = Util.processTriggerValues(notification.getMessage(), triggerValues);
                JTextArea wrappingTextArea = new JTextArea(notification.getType().getName() + ": " + message);
                wrappingTextArea.setLineWrap(true);
                wrappingTextArea.setWrapStyleWord(true);
                wrappingTextArea.setOpaque(false);
                wrappingTextArea.setEditable(false);
                wrappingTextArea.setFocusable(false);
                this.add(wrappingTextArea);
            });
        String formattedTime = DateTimeFormatter.ofPattern("HH:mm:ss").withZone(ZoneId.systemDefault()).format(Instant.now());
        this.add(new JLabel(formattedTime));
    }
}
