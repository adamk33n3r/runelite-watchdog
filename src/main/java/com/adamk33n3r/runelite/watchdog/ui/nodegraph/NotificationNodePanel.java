package com.adamk33n3r.runelite.watchdog.ui.nodegraph;

import com.adamk33n3r.nodegraph.nodes.NotificationNode;
import com.adamk33n3r.runelite.watchdog.notifications.Notification;
import com.adamk33n3r.runelite.watchdog.ui.nodegraph.connections.ConnectionLine;
import com.adamk33n3r.runelite.watchdog.ui.nodegraph.connections.ConnectionPointIn;
import com.adamk33n3r.runelite.watchdog.ui.nodegraph.inputs.BoolInput;
import com.adamk33n3r.runelite.watchdog.ui.nodegraph.inputs.NumberInput;
import com.adamk33n3r.runelite.watchdog.ui.nodegraph.inputs.TextInput;
import com.adamk33n3r.runelite.watchdog.ui.nodegraph.inputs.ViewInput;
import com.adamk33n3r.runelite.watchdog.ui.panels.NotificationPanelFactory;

import lombok.Getter;

import javax.swing.JButton;
import java.awt.Color;

@Getter
public class NotificationNodePanel extends NodePanel {
    private final ConnectionPointIn<Boolean> enabledIn;
    private final ConnectionPointIn<String[]> captureGroupsIn;
    private final ConnectionPointIn<String> alertNameIn;
    private final ConnectionPointIn<Number> delayMillisecondsIn;

    public NotificationNodePanel(GraphPanel graphPanel, int x, int y, String name, Color color, NotificationNode notificationNode, NotificationPanelFactory notificationPanelFactory) {
        super(graphPanel, notificationNode, x, y, name, color);
        Notification notification = notificationNode.getNotification();

        this.enabledIn = new ConnectionPointIn<>(this, notificationNode.getEnabled());
        this.items.add(new ConnectionLine<>(this.enabledIn, new BoolInput("Enabled", notificationNode.getEnabled()), null));
        this.captureGroupsIn = new ConnectionPointIn<>(this, notificationNode.getCaptureGroups());
        this.items.add(new ConnectionLine<>(this.captureGroupsIn, new ViewInput<>("Capture Groups", notificationNode.getCaptureGroups().getValue()), null));
        this.alertNameIn = new ConnectionPointIn<>(this, notificationNode.getAlertName());
        this.items.add(new ConnectionLine<>(this.alertNameIn, new TextInput("Alert Name", ""), null));
        this.delayMillisecondsIn = new ConnectionPointIn<>(this, notificationNode.getDelayMilliseconds());
        this.items.add(new ConnectionLine<>(this.delayMillisecondsIn, new NumberInput("Delay (ms)", notificationNode.getNotification().getDelayMilliseconds()), null));

        JButton testBtn = new JButton("TEST");
        testBtn.addActionListener((ev) -> notification.fireForced(new String[]{}));
        this.items.add(testBtn);

        // Populate type-specific controls via factory — no if/else chains here
        notificationPanelFactory.populateContent(notification, this.items, this::notifyChange);

        this.pack();
    }
}
