package com.adamk33n3r.runelite.watchdog.ui.nodegraph;

import com.adamk33n3r.nodegraph.ExecSignal;
import com.adamk33n3r.nodegraph.nodes.ActionNode;
import com.adamk33n3r.runelite.watchdog.notifications.Notification;
import com.adamk33n3r.runelite.watchdog.ui.nodegraph.connections.ConnectionLine;
import com.adamk33n3r.runelite.watchdog.ui.nodegraph.connections.ConnectionPointIn;
import com.adamk33n3r.runelite.watchdog.ui.nodegraph.connections.ConnectionPointOut;
import com.adamk33n3r.runelite.watchdog.ui.nodegraph.inputs.BoolInput;
import com.adamk33n3r.runelite.watchdog.ui.nodegraph.inputs.IntegerInput;
import com.adamk33n3r.runelite.watchdog.ui.nodegraph.inputs.ViewInput;
import com.adamk33n3r.runelite.watchdog.ui.notifications.panels.NotificationContentPanel;
import com.adamk33n3r.runelite.watchdog.ui.panels.NotificationPanelFactory;

import lombok.Getter;

import javax.swing.JButton;
import java.awt.Color;

@Getter
public class ActionNodePanel extends NodePanel {
    private final ConnectionPointIn<Boolean> enabledIn;
    private final ConnectionPointIn<ExecSignal> execIn;
    private final ConnectionPointOut<ExecSignal> execOut;
//    private final ConnectionPointIn<String> alertNameIn;
//    private final ConnectionPointIn<Number> delayMillisecondsIn;
    private final ConnectionPointIn<Boolean> fireWhenFocusedIn;
    private final ConnectionPointIn<Boolean> fireWhenAfkIn;
    private final ConnectionPointIn<Number> fireWhenAfkSecondsIn;

    public ActionNodePanel(GraphPanel graphPanel, int x, int y, String name, Color color, ActionNode actionNode, NotificationPanelFactory notificationPanelFactory) {
        super(graphPanel, actionNode, x, y, name, color);
        Notification notification = actionNode.getNotification();

        this.execIn = new ConnectionPointIn<>(this, actionNode.getExec());
        this.execOut = new ConnectionPointOut<>(this, actionNode.getExecOut());
        this.items.add(new ConnectionLine<>(this.execIn, new ViewInput<>("Exec", actionNode.getExec().getValue()), this.execOut));
        this.enabledIn = new ConnectionPointIn<>(this, actionNode.getEnabled());
        this.items.add(new ConnectionLine<>(this.enabledIn, new BoolInput("Enabled", actionNode.getEnabled()), null));
//        this.alertNameIn = new ConnectionPointIn<>(this, actionNode.getAlertName());
//        this.items.add(new ConnectionLine<>(this.alertNameIn, new TextInput("Alert Name", ""), null));
//        this.delayMillisecondsIn = new ConnectionPointIn<>(this, actionNode.getDelayMilliseconds());
//        this.items.add(new ConnectionLine<>(this.delayMillisecondsIn, new NumberInput("Delay (ms)", actionNode.getDelayMilliseconds()), null));
        this.fireWhenFocusedIn = new ConnectionPointIn<>(this, actionNode.getFireWhenFocused());
        this.items.add(new ConnectionLine<>(this.fireWhenFocusedIn, new BoolInput("Focused", actionNode.getFireWhenFocused()), null));
        this.fireWhenAfkIn = new ConnectionPointIn<>(this, actionNode.getFireWhenAfk());
        this.items.add(new ConnectionLine<>(this.fireWhenAfkIn, new BoolInput("AFK", actionNode.getFireWhenAfk()), null));
        this.fireWhenAfkSecondsIn = new ConnectionPointIn<>(this, actionNode.getFireWhenAfkSeconds());
        this.items.add(new ConnectionLine<>(this.fireWhenAfkSecondsIn, new IntegerInput("AFK Seconds", actionNode.getFireWhenAfkSeconds()), null));

        // Populate type-specific controls via content panel instance
        NotificationContentPanel<?> contentPanel = notificationPanelFactory.createContentPanel(notification, this::notifyChange);
        if (contentPanel != null) {
            contentPanel.setOnRebuild(this::pack);
            this.items.add(contentPanel);
        }

        JButton testBtn = new JButton("TEST");
        testBtn.addActionListener(ev -> notification.fireForced(new String[]{}));
        this.items.add(testBtn);

        this.watchDirty(
            actionNode.getEnabled(),
            actionNode.getFireWhenFocused(),
            actionNode.getFireWhenAfk(),
            actionNode.getFireWhenAfkSeconds()
        );
        this.pack();
    }
}
