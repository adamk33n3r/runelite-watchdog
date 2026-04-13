package com.adamk33n3r.runelite.watchdog.nodegraph;

import com.adamk33n3r.nodegraph.nodes.ContinuousTriggerNode;
import com.adamk33n3r.nodegraph.nodes.NotificationNode;
import com.adamk33n3r.nodegraph.nodes.TriggerNode;
import com.adamk33n3r.nodegraph.nodes.constants.Bool;
import com.adamk33n3r.nodegraph.nodes.constants.Num;
import com.adamk33n3r.nodegraph.nodes.math.Add;
import com.adamk33n3r.runelite.watchdog.alerts.ChatAlert;
import com.adamk33n3r.runelite.watchdog.alerts.ContinuousAlert;
import com.adamk33n3r.runelite.watchdog.notifications.Notification;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class NodeVarRegistrationTest {

    @Test
    public void triggerNode_registersAllInputs() {
        TriggerNode node = new TriggerNode(new ChatAlert("test"));
        assertTrue(node.getInputs().containsKey("Enabled"));
        assertTrue(node.getInputs().containsKey("Capture Groups In"));
    }

    @Test
    public void triggerNode_registersAllOutputs() {
        TriggerNode node = new TriggerNode(new ChatAlert("test"));
        assertTrue(node.getOutputs().containsKey("Exec"));
        assertTrue(node.getOutputs().containsKey("Enabled Out"));
    }

    @Test
    public void notificationNode_registersAllInputs() {
        Notification mockNotif = Mockito.mock(Notification.class);
        NotificationNode node = new NotificationNode(mockNotif);
        assertTrue(node.getInputs().containsKey("Enabled"));
        assertTrue(node.getInputs().containsKey("Fire When Focused"));
        assertTrue(node.getInputs().containsKey("Fire When AFK"));
        assertTrue(node.getInputs().containsKey("Exec"));
    }

    @Test
    public void notificationNode_hasNoOutputs() {
        Notification mockNotif = Mockito.mock(Notification.class);
        NotificationNode node = new NotificationNode(mockNotif);
        assertTrue(node.getOutputs().isEmpty());
    }

    @Test
    public void bool_registersValueInAndOut() {
        Bool node = new Bool();
        assertTrue(node.getInputs().containsKey("Value"));
        assertTrue(node.getOutputs().containsKey("Value"));
    }

    @Test
    public void num_registersValueOut_noInputs() {
        Num node = new Num();
        assertTrue(node.getInputs().isEmpty());
        assertTrue(node.getOutputs().containsKey("Value"));
    }

    @Test
    public void add_registersABAndResult() {
        Add node = new Add();
        assertTrue(node.getInputs().containsKey("A"));
        assertTrue(node.getInputs().containsKey("B"));
        assertTrue(node.getOutputs().containsKey("Result"));
    }

    @Test
    public void continuousTriggerNode_registersIsTriggeredOutput() {
        ContinuousAlert alert = Mockito.mock(ContinuousAlert.class);
        ContinuousTriggerNode node = new ContinuousTriggerNode(alert);
        assertTrue(node.getOutputs().containsKey("Is Triggered"));
        // Also inherits TriggerNode outputs
        assertTrue(node.getOutputs().containsKey("Exec"));
    }

    @Test
    public void nodes_haveUniqueIds() {
        TriggerNode a = new TriggerNode(new ChatAlert("a"));
        TriggerNode b = new TriggerNode(new ChatAlert("b"));
        assertNotEquals(a.getId(), b.getId());
    }

    @Test
    public void varNames_matchLookupKeys() {
        TriggerNode node = new TriggerNode(new ChatAlert("test"));
        // The key in the map must equal the var's own getName()
        node.getInputs().forEach((key, var) -> assertEquals(key, var.getName()));
        node.getOutputs().forEach((key, var) -> assertEquals(key, var.getName()));
    }
}
