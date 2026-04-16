package com.adamk33n3r.runelite.watchdog.nodegraph;

import com.adamk33n3r.nodegraph.Graph;
import com.adamk33n3r.nodegraph.nodes.ActionNode;
import com.adamk33n3r.nodegraph.nodes.TriggerNode;
import com.adamk33n3r.nodegraph.nodes.constants.Num;
import com.adamk33n3r.nodegraph.nodes.math.Add;
import com.adamk33n3r.runelite.watchdog.alerts.AdvancedAlert;
import com.adamk33n3r.runelite.watchdog.alerts.ChatAlert;
import com.adamk33n3r.runelite.watchdog.alerts.StatChangedAlert;
import com.adamk33n3r.runelite.watchdog.notifications.Notification;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class AdvancedAlertGraphTest {

    @Test
    public void getTriggerNodesOfType_returnsChatAlertNodes() {
        Graph graph = new Graph();
        TriggerNode chatNode = new TriggerNode(new ChatAlert("chat"));
        TriggerNode statNode = new TriggerNode(new StatChangedAlert());
        graph.add(chatNode);
        graph.add(statNode);

        List<TriggerNode> result = graph.getTriggerNodesOfType(ChatAlert.class).collect(Collectors.toList());
        assertEquals(1, result.size());
        assertSame(chatNode, result.get(0));
    }

    @Test
    public void getTriggerNodesOfType_returnsEmptyWhenNoMatch() {
        Graph graph = new Graph();
        graph.add(new TriggerNode(new StatChangedAlert()));

        List<TriggerNode> result = graph.getTriggerNodesOfType(ChatAlert.class).collect(Collectors.toList());
        assertTrue(result.isEmpty());
    }

    @Test
    public void getReachableNotifications_skipsDisabledTrigger() {
        Graph graph = new Graph();
        ChatAlert alert = new ChatAlert("disabled");
        alert.setEnabled(false);
        TriggerNode trigger = new TriggerNode(alert);
        Notification mockNotif = Mockito.mock(Notification.class);
        ActionNode notifNode = new ActionNode(mockNotif);
        graph.add(trigger);
        graph.add(notifNode);
        graph.connect(trigger.getExec(), notifNode.getExec());

        List<ActionNode> result = graph.getReachableActionsFromTrigger(trigger);
        assertTrue(result.isEmpty());
    }

    @Test
    public void getReachableNotifications_skipsDisabledActionNode() {
        Graph graph = new Graph();
        TriggerNode trigger = new TriggerNode(new ChatAlert("enabled"));
        Notification mockNotif = Mockito.mock(Notification.class);
        ActionNode notifNode = new ActionNode(mockNotif);
        notifNode.getEnabled().setValue(false);
        graph.add(trigger);
        graph.add(notifNode);
        graph.connect(trigger.getExec(), notifNode.getExec());

        List<ActionNode> result = graph.getReachableActionsFromTrigger(trigger);
        assertTrue(result.isEmpty());
    }

    @Test
    public void getReachableNotifications_includesConnectedEnabledNode() {
        Graph graph = new Graph();
        TriggerNode trigger = new TriggerNode(new ChatAlert("enabled"));
        Notification mockNotif = Mockito.mock(Notification.class);
        ActionNode notifNode = new ActionNode(mockNotif);
        graph.add(trigger);
        graph.add(notifNode);
        graph.connect(trigger.getExec(), notifNode.getExec());

        List<ActionNode> result = graph.getReachableActionsFromTrigger(trigger);
        assertEquals(1, result.size());
        assertSame(notifNode, result.get(0));
    }

    @Test
    public void fireTriggerNode_propagatesCaptureGroupsAndFiresNotification() {
        AdvancedAlert adv = new AdvancedAlert("test");
        Graph graph = adv.getGraph();

        TriggerNode trigger = new TriggerNode(new ChatAlert("test"));
        Notification mockNotif = Mockito.mock(Notification.class);
        ActionNode notifNode = new ActionNode(mockNotif);
        graph.add(trigger);
        graph.add(notifNode);
        graph.connect(trigger.getExec(), notifNode.getExec());

        String[] groups = new String[]{"hello", "world"};
        adv.fireTriggerNode(trigger, groups);

        Mockito.verify(mockNotif).fire(groups);
    }

    @Test
    public void fireTriggerNode_doesNotFireUnconnectedNotification() {
        AdvancedAlert adv = new AdvancedAlert("test");
        Graph graph = adv.getGraph();

        TriggerNode trigger = new TriggerNode(new ChatAlert("test"));
        Notification mockNotif = Mockito.mock(Notification.class);
        ActionNode notifNode = new ActionNode(mockNotif);
        graph.add(trigger);
        graph.add(notifNode);
        // no connection between trigger and notifNode

        adv.fireTriggerNode(trigger, new String[0]);

        Mockito.verify(mockNotif, Mockito.never()).fire(Mockito.any());
    }

    @Test
    public void fireTriggerNode_firesMultipleConnectedNotifications() {
        AdvancedAlert adv = new AdvancedAlert("test");
        Graph graph = adv.getGraph();

        TriggerNode trigger = new TriggerNode(new ChatAlert("test"));
        Notification mockNotif1 = Mockito.mock(Notification.class);
        Notification mockNotif2 = Mockito.mock(Notification.class);
        ActionNode notifNode1 = new ActionNode(mockNotif1);
        ActionNode notifNode2 = new ActionNode(mockNotif2);
        graph.add(trigger);
        graph.add(notifNode1);
        graph.add(notifNode2);
        graph.connect(trigger.getExec(), notifNode1.getExec());
        graph.connect(trigger.getExec(), notifNode2.getExec());

        adv.fireTriggerNode(trigger, new String[]{"test"});

        Mockito.verify(mockNotif1).fire(Mockito.any());
        Mockito.verify(mockNotif2).fire(Mockito.any());
    }

    @Test
    public void process_cascadesRecursively() {
        Graph graph = new Graph();
        Num num = new Num();
        num.setValue(42);
        Add add = new Add();
        graph.add(num);
        graph.add(add);
        graph.connect(num.getValue(), add.getA());
        graph.connect(num.getValue(), add.getB());

        graph.process(num);

        assertEquals(84, add.getResult().getValue().intValue());
    }
}
