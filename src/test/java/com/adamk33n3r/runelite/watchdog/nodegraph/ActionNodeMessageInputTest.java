package com.adamk33n3r.runelite.watchdog.nodegraph;

import com.adamk33n3r.nodegraph.Graph;
import com.adamk33n3r.nodegraph.nodes.ActionNode;
import com.adamk33n3r.nodegraph.nodes.TriggerNode;
import com.adamk33n3r.nodegraph.nodes.utility.ToStringNode;
import com.adamk33n3r.runelite.watchdog.alerts.ChatAlert;
import com.adamk33n3r.runelite.watchdog.notifications.MessageNotification;
import com.adamk33n3r.runelite.watchdog.notifications.Notification;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class ActionNodeMessageInputTest {
    static class RecordingMessageNotification extends MessageNotification {
        String firedMessage;
        String[] firedTriggerValues;
        int fireCount;
        boolean shouldFire = true;

        @Override
        public boolean shouldFire() {
            return this.shouldFire;
        }

        @Override
        protected void fireImpl(String[] triggerValues, String message) {
            this.firedMessage = message;
            this.firedTriggerValues = triggerValues;
            this.fireCount++;
        }
    }

    @Test
    public void messageNotification_registersMessageInput() {
        ActionNode node = new ActionNode(new RecordingMessageNotification());

        assertTrue(node.getInputs().containsKey("Message"));
    }

    @Test
    public void nonMessageNotification_doesNotRegisterMessageInput() {
        ActionNode node = new ActionNode(Mockito.mock(Notification.class));

        assertFalse(node.getInputs().containsKey("Message"));
        assertNull(node.getMessage());
    }

    @Test
    public void messageInput_tracksConfiguredMessageWhileUnconnected() {
        RecordingMessageNotification notification = new RecordingMessageNotification();
        notification.setMessage("configured message");
        ActionNode node = new ActionNode(notification);

        notification.setMessage("edited message");

        assertEquals("edited message", node.getMessage().getValue());
    }

    @Test
    public void messageInput_connectedValueWinsOverConfiguredMessage() {
        RecordingMessageNotification notification = new RecordingMessageNotification();
        notification.setMessage("configured message");
        ActionNode action = this.connectStringSource(new Graph(), notification, "dynamic");

        assertEquals("dynamic", action.getMessage().getValue());
    }

    @Test
    public void messageInput_revertsToConfiguredMessageAfterDisconnect() {
        Graph graph = new Graph();
        RecordingMessageNotification notification = new RecordingMessageNotification();
        notification.setMessage("configured message");
        ActionNode action = this.connectStringSource(graph, notification, "dynamic");

        graph.getConnections().get(0).remove();

        assertEquals("configured message", action.getMessage().getValue());
    }

    @Test
    public void messageInput_isNotConnectedByDefault() {
        ActionNode node = new ActionNode(new RecordingMessageNotification());

        assertFalse(node.getMessage().isConnected());
    }

    private static ActionNode connectStringSource(Graph graph, RecordingMessageNotification notification, Object sourceValue) {
        ActionNode action = new ActionNode(notification);
        ToStringNode toString = new ToStringNode();
        graph.add(toString);
        graph.add(action);
        graph.connect(toString.getResult(), action.getMessage());
        toString.getValue().setValue(sourceValue);
        return action;
    }

    @Test
    public void connectedMessage_isUsedWhenFired() {
        RecordingMessageNotification notification = new RecordingMessageNotification();
        notification.setMessage("configured message");
        ActionNode action = this.connectStringSource(new Graph(), notification, 37);

        action.fire(new String[0]);

        assertEquals("37", notification.firedMessage);
    }

    @Test
    public void connectedMessage_firesExactlyOnce() {
        RecordingMessageNotification notification = new RecordingMessageNotification();
        ActionNode action = this.connectStringSource(new Graph(), notification, "dynamic");

        action.fire(new String[0]);

        assertEquals(1, notification.fireCount);
    }

    @Test
    public void connectedMessage_leavesConfiguredMessageUnmodified() {
        RecordingMessageNotification notification = new RecordingMessageNotification();
        notification.setMessage("configured message");
        ActionNode action = this.connectStringSource(new Graph(), notification, "dynamic");

        action.fire(new String[0]);

        assertEquals("configured message", notification.getMessage());
    }

    @Test
    public void unconnectedMessage_fallsBackToConfiguredMessage() {
        RecordingMessageNotification notification = new RecordingMessageNotification();
        notification.setMessage("configured message");
        ActionNode action = new ActionNode(notification);

        action.fire(new String[0]);

        assertEquals("configured message", notification.firedMessage);
    }

    @Test
    public void execChain_connectedMessage_deliversDynamicMessageAndCaptureGroups() {
        Graph graph = new Graph();
        RecordingMessageNotification notification = new RecordingMessageNotification();
        notification.setMessage("Grace progress: unknown");
        ActionNode action = this.connectStringSource(graph, notification, "Grace progress: 37%");
        TriggerNode trigger = new TriggerNode(new ChatAlert("test"));
        graph.add(trigger);
        graph.connect(trigger.getExec(), action.getExec());

        graph.executeExecChain(trigger, new String[]{"world"});

        assertEquals("Grace progress: 37%", notification.firedMessage);
        assertArrayEquals(new String[]{"world"}, notification.firedTriggerValues);
    }

    @Test
    public void fireForced_unconnectedMessage_usesConfiguredMessage() {
        RecordingMessageNotification notification = new RecordingMessageNotification();
        notification.setMessage("configured message");
        ActionNode action = new ActionNode(notification);

        action.fireForced(new String[0]);

        assertEquals("configured message", notification.firedMessage);
    }

    @Test
    public void fireForced_connectedMessage_bypassesShouldFire() {
        RecordingMessageNotification notification = new RecordingMessageNotification();
        notification.shouldFire = false;
        ActionNode action = this.connectStringSource(new Graph(), notification, "dynamic");

        action.fireForced(new String[0]);

        assertEquals("dynamic", notification.firedMessage);
        assertEquals(1, notification.fireCount);
    }

    @Test
    public void fireForced_nonMessageNotification_stillFires() {
        Notification notification = Mockito.mock(Notification.class);
        ActionNode action = new ActionNode(notification);

        action.fireForced(new String[]{"world"});

        Mockito.verify(notification).fireForced(new String[]{"world"});
    }

    @Test
    public void nonMessageNotification_stillFires() {
        Notification notification = Mockito.mock(Notification.class);
        ActionNode action = new ActionNode(notification);

        action.fire(new String[]{"world"});

        Mockito.verify(notification).fire(new String[]{"world"});
    }
}
