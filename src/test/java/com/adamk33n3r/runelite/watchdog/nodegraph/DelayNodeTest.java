package com.adamk33n3r.runelite.watchdog.nodegraph;

import com.adamk33n3r.nodegraph.Graph;
import com.adamk33n3r.nodegraph.nodes.ActionNode;
import com.adamk33n3r.nodegraph.nodes.flow.DelayNode;
import com.adamk33n3r.nodegraph.nodes.TriggerNode;
import com.adamk33n3r.runelite.watchdog.alerts.ChatAlert;
import com.adamk33n3r.runelite.watchdog.notifications.Notification;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class DelayNodeTest {

    @Test
    public void delayMs_defaultsToZero() {
        DelayNode delay = new DelayNode();
        assertEquals(0, delay.getDelayMs().getValue().intValue());
    }

    @Test
    public void execIn_allowsMultipleConnections() {
        DelayNode delay = new DelayNode();
        assertTrue(delay.getExec().isAllowMultipleConnections());
    }

    @Test
    public void execOut_propagatesSignalWhenDelayIsZero() throws InterruptedException {
        Graph graph = new Graph();
        TriggerNode trigger = new TriggerNode(new ChatAlert("test"));
        DelayNode delay = new DelayNode();
        delay.getDelayMs().setValue(0);
        Notification mockNotif = Mockito.mock(Notification.class);
        ActionNode action = new ActionNode(mockNotif);

        graph.add(trigger);
        graph.add(delay);
        graph.add(action);
        graph.connect(trigger.getExec(), delay.getExec());
        graph.connect(delay.getExecOut(), action.getExec());

        graph.executeExecChain(trigger, new String[]{"val"});

        Mockito.verify(mockNotif).fire(new String[]{"val"});
    }

    @Test
    public void execOut_propagatesSignalAfterDelay() throws InterruptedException {
        Graph graph = new Graph();
        TriggerNode trigger = new TriggerNode(new ChatAlert("test"));
        DelayNode delay = new DelayNode();
        delay.getDelayMs().setValue(100);
        Notification mockNotif = Mockito.mock(Notification.class);
        ActionNode action = new ActionNode(mockNotif);

        CountDownLatch latch = new CountDownLatch(1);
        Mockito.doAnswer(inv -> {
            latch.countDown();
            return null;
        }).when(mockNotif).fire(Mockito.any());

        graph.add(trigger);
        graph.add(delay);
        graph.add(action);
        graph.connect(trigger.getExec(), delay.getExec());
        graph.connect(delay.getExecOut(), action.getExec());

        graph.executeExecChain(trigger, new String[]{"val"});

        // Should not have fired yet immediately
        Mockito.verify(mockNotif, Mockito.never()).fire(Mockito.any());

        // Should fire within 500ms
        assertTrue("Action should fire after delay", latch.await(500, TimeUnit.MILLISECONDS));
        Mockito.verify(mockNotif).fire(new String[]{"val"});
    }
}
