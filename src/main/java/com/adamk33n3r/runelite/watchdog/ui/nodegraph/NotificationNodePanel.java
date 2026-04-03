package com.adamk33n3r.runelite.watchdog.ui.nodegraph;

import com.adamk33n3r.nodegraph.ExecSignal;
import com.adamk33n3r.nodegraph.nodes.NotificationNode;
import com.adamk33n3r.runelite.watchdog.notifications.Notification;
import com.adamk33n3r.runelite.watchdog.ui.nodegraph.connections.ConnectionLine;
import com.adamk33n3r.runelite.watchdog.ui.nodegraph.connections.ConnectionPointIn;
import com.adamk33n3r.runelite.watchdog.ui.nodegraph.inputs.BoolInput;
import com.adamk33n3r.runelite.watchdog.ui.nodegraph.inputs.NumberInput;
import com.adamk33n3r.runelite.watchdog.ui.nodegraph.inputs.ViewInput;
import com.adamk33n3r.runelite.watchdog.ui.notifications.panels.NotificationContentPanel;
import com.adamk33n3r.runelite.watchdog.ui.panels.NotificationPanelFactory;

import lombok.Getter;

import javax.swing.JButton;
import java.awt.Color;

@Getter
public class NotificationNodePanel extends NodePanel {
    private final ConnectionPointIn<Boolean> enabledIn;
    private final ConnectionPointIn<ExecSignal> execIn;
//    private final ConnectionPointIn<String> alertNameIn;
//    private final ConnectionPointIn<Number> delayMillisecondsIn;
    private final ConnectionPointIn<Boolean> fireWhenFocusedIn;
    private final ConnectionPointIn<Boolean> fireWhenAfkIn;
    private final ConnectionPointIn<Number> fireWhenAfkSecondsIn;

    public NotificationNodePanel(GraphPanel graphPanel, int x, int y, String name, Color color, NotificationNode notificationNode, NotificationPanelFactory notificationPanelFactory) {
        super(graphPanel, notificationNode, x, y, name, color);
        Notification notification = notificationNode.getNotification();

        this.execIn = new ConnectionPointIn<>(this, notificationNode.getExec());
        this.items.add(new ConnectionLine<>(this.execIn, new ViewInput<>("Exec", notificationNode.getExec().getValue()), null));
        this.enabledIn = new ConnectionPointIn<>(this, notificationNode.getEnabled());
        this.items.add(new ConnectionLine<>(this.enabledIn, new BoolInput("Enabled", notificationNode.getEnabled()), null));
//        this.alertNameIn = new ConnectionPointIn<>(this, notificationNode.getAlertName());
//        this.items.add(new ConnectionLine<>(this.alertNameIn, new TextInput("Alert Name", ""), null));
//        this.delayMillisecondsIn = new ConnectionPointIn<>(this, notificationNode.getDelayMilliseconds());
//        this.items.add(new ConnectionLine<>(this.delayMillisecondsIn, new NumberInput("Delay (ms)", notificationNode.getDelayMilliseconds()), null));
        this.fireWhenFocusedIn = new ConnectionPointIn<>(this, notificationNode.getFireWhenFocused());
        this.items.add(new ConnectionLine<>(this.fireWhenFocusedIn, new BoolInput("Focused", notificationNode.getFireWhenFocused()), null));
        this.fireWhenAfkIn = new ConnectionPointIn<>(this, notificationNode.getFireWhenAfk());
        this.items.add(new ConnectionLine<>(this.fireWhenAfkIn, new BoolInput("AFK", notificationNode.getFireWhenAfk()), null));
        this.fireWhenAfkSecondsIn = new ConnectionPointIn<>(this, notificationNode.getFireWhenAfkSeconds());
        this.items.add(new ConnectionLine<>(this.fireWhenAfkSecondsIn, new NumberInput("AFK Seconds", notificationNode.getFireWhenAfkSeconds()), null));

        JButton testBtn = new JButton("TEST");
        testBtn.addActionListener(ev -> notification.fireForced(new String[]{}));
        this.items.add(testBtn);

        // Populate type-specific controls via content panel instance
        NotificationContentPanel<?> contentPanel = notificationPanelFactory.createContentPanel(notification, this::notifyChange);
        if (contentPanel != null) {
            contentPanel.setOnRebuild(this::pack);
            this.items.add(contentPanel);
        }

        this.pack();
    }
}
