package com.adamk33n3r.runelite.watchdog.nodegraph;

import com.adamk33n3r.nodegraph.Graph;
import com.adamk33n3r.nodegraph.nodes.ActionNode;
import com.adamk33n3r.nodegraph.nodes.ActionNodeFactory;
import com.adamk33n3r.nodegraph.nodes.MessageActionNode;
import com.adamk33n3r.nodegraph.nodes.utility.ToStringNode;
import com.adamk33n3r.runelite.watchdog.notifications.GameMessage;
import com.adamk33n3r.runelite.watchdog.notifications.ScreenFlash;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class MessageActionNodeTest {
    @Test
    public void factory_buildsMessageActionNodeForMessageNotifications() {
        ActionNode node = ActionNodeFactory.create(new GameMessage());

        assertTrue(node instanceof MessageActionNode);
    }

    @Test
    public void factory_buildsPlainActionNodeForOtherNotifications() {
        ActionNode node = ActionNodeFactory.create(new ScreenFlash());

        assertFalse(node instanceof MessageActionNode);
    }

    @Test
    public void seedsMessageInputFromNotification() {
        GameMessage notification = new GameMessage();
        notification.setMessage("configured message");

        MessageActionNode node = new MessageActionNode(notification);

        assertEquals("configured message", node.getMessage().getValue());
    }

    @Test
    public void seedingDoesNotClobberNotificationMessage() {
        GameMessage notification = new GameMessage();
        notification.setMessage("configured message");

        new MessageActionNode(notification);

        assertEquals("configured message", notification.getMessage());
    }

    @Test
    public void forwardsMessageInputToNotification() {
        GameMessage notification = new GameMessage();
        notification.setMessage("configured message");
        MessageActionNode node = new MessageActionNode(notification);

        node.getMessage().setValue("dynamic message");

        assertEquals("dynamic message", notification.getMessage());
    }

    @Test
    public void forwardsConnectedGraphValueToNotification() {
        GameMessage notification = new GameMessage();
        notification.setMessage("configured message");
        MessageActionNode action = new MessageActionNode(notification);
        ToStringNode source = new ToStringNode();

        Graph graph = new Graph();
        graph.add(source);
        graph.add(action);
        graph.connect(source.getResult(), action.getMessage());

        source.getResult().setValue("from the graph");

        assertEquals("from the graph", notification.getMessage());
    }

    @Test
    public void registersMessageInputForSerialization() {
        MessageActionNode node = new MessageActionNode(new GameMessage());

        assertTrue(node.getInputs().containsKey("Message"));
    }
}
