package com.adamk33n3r.runelite.watchdog.ui.nodegraph;

import com.adamk33n3r.nodegraph.nodes.ActionNode;
import com.adamk33n3r.nodegraph.nodes.MessageActionNode;
import com.adamk33n3r.runelite.watchdog.notifications.GameMessage;
import com.adamk33n3r.runelite.watchdog.ui.nodegraph.connections.ConnectionLine;
import com.adamk33n3r.runelite.watchdog.ui.nodegraph.connections.ConnectionPointIn;
import com.adamk33n3r.runelite.watchdog.ui.nodegraph.inputs.TextInput;
import com.adamk33n3r.runelite.watchdog.ui.notifications.panels.NotificationContentPanel;
import com.adamk33n3r.runelite.watchdog.ui.panels.NotificationPanelFactory;

import lombok.Getter;

import java.awt.Color;

/**
 * An {@link ActionNodePanel} that puts the notification's message on its own connection line, so the
 * field sits next to the pin that can drive it from the graph.
 */
@Getter
public class MessageActionNodePanel extends ActionNodePanel {
    private static final String MESSAGE_TOOLTIP = "The message this action fires with. Connect the pin to supply it from the graph instead.";

    private ConnectionPointIn<String> messageIn;
    private TextInput messageInput;

    public MessageActionNodePanel(GraphPanel graphPanel, int x, int y, String name, Color color, MessageActionNode actionNode, NotificationPanelFactory notificationPanelFactory) {
        super(graphPanel, x, y, name, color, actionNode, notificationPanelFactory);
        this.watchDirty(actionNode.getMessage());
    }

    @Override
    protected void addTypeSpecificRows(ActionNode actionNode) {
        MessageActionNode messageActionNode = (MessageActionNode) actionNode;
        boolean supportsFormattingTags = messageActionNode.getNotification() instanceof GameMessage;

        this.messageIn = new ConnectionPointIn<>(this, messageActionNode.getMessage());
        this.messageInput = new TextInput(
            supportsFormattingTags ? "Enter your formatted message..." : "Enter your message...",
            MESSAGE_TOOLTIP,
            messageActionNode.getMessage()
        );
        this.items.add(new ConnectionLine<>(this.messageIn, this.messageInput, null));
    }

    @Override
    protected void configureContentPanel(NotificationContentPanel<?> contentPanel) {
        contentPanel.setMessageFieldHidden(true);
    }
}
