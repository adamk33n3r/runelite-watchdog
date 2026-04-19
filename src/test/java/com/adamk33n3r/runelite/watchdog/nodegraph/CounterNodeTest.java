package com.adamk33n3r.runelite.watchdog.nodegraph;

import com.adamk33n3r.nodegraph.Graph;
import com.adamk33n3r.nodegraph.nodes.ActionNode;
import com.adamk33n3r.nodegraph.nodes.TriggerNode;
import com.adamk33n3r.nodegraph.nodes.constants.Num;
import com.adamk33n3r.nodegraph.nodes.flow.Counter;
import com.adamk33n3r.nodegraph.nodes.logic.Equality;
import com.adamk33n3r.runelite.watchdog.alerts.AdvancedAlert;
import com.adamk33n3r.runelite.watchdog.alerts.ChatAlert;
import com.adamk33n3r.runelite.watchdog.notifications.Notification;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class CounterNodeTest {

    private static final String[] NO_GROUPS = new String[0];

    @Test
    public void incrementsOnEachExec() throws InterruptedException {
        AdvancedAlert adv = new AdvancedAlert("test");
        Graph graph = adv.getGraph();

        TriggerNode trigger = new TriggerNode(new ChatAlert("test"));
        Counter counter = new Counter();

        graph.add(trigger);
        graph.add(counter);
        graph.connect(trigger.getExec(), counter.getExec());

        assertEquals(0, counter.getCount().getValue().intValue());

        adv.fireTriggerNode(trigger, NO_GROUPS);
        adv.fireTriggerNode(trigger, NO_GROUPS);
        adv.fireTriggerNode(trigger, NO_GROUPS);
        Thread.sleep(100);

        assertEquals(3, counter.getCount().getValue().intValue());
    }

    @Test
    public void resetZeroesCount() throws InterruptedException {
        AdvancedAlert adv = new AdvancedAlert("test");
        Graph graph = adv.getGraph();

        TriggerNode trigger = new TriggerNode(new ChatAlert("trigger"));
        TriggerNode resetTrigger = new TriggerNode(new ChatAlert("reset-trigger"));
        Counter counter = new Counter();

        graph.add(trigger);
        graph.add(resetTrigger);
        graph.add(counter);
        graph.connect(trigger.getExec(), counter.getExec());
        graph.connect(resetTrigger.getExec(), counter.getReset());

        adv.fireTriggerNode(trigger, NO_GROUPS);
        adv.fireTriggerNode(trigger, NO_GROUPS);
        adv.fireTriggerNode(trigger, NO_GROUPS);
        adv.fireTriggerNode(trigger, NO_GROUPS);
        adv.fireTriggerNode(trigger, NO_GROUPS);
        Thread.sleep(50);
        assertEquals(5, counter.getCount().getValue().intValue());

        adv.fireTriggerNode(resetTrigger, NO_GROUPS);
        Thread.sleep(50);
        assertEquals(0, counter.getCount().getValue().intValue());
    }

    @Test
    public void userUseCase_fireEvery10th() throws InterruptedException {
        AdvancedAlert adv = new AdvancedAlert("test");
        Graph graph = adv.getGraph();

        TriggerNode trigger = new TriggerNode(new ChatAlert("xp-drop"));
        Counter counter = new Counter();
        Num threshold = new Num();
        threshold.setValue(10);
        Equality equality = new Equality();
        Notification mockNotif = Mockito.mock(Notification.class);
        ActionNode action = new ActionNode(mockNotif);

        graph.add(trigger);
        graph.add(counter);
        graph.add(threshold);
        graph.add(equality);
        graph.add(action);

        graph.connect(trigger.getExec(), counter.getExec());
        graph.connect(counter.getExecOut(), action.getExec());
        graph.connect(counter.getCountOut(), equality.getA());
        graph.connect(threshold.getValue(), equality.getB());
        graph.connect(equality.getResult(), action.getEnabled());

        // Fires 1–9: count never equals 10
        for (int i = 0; i < 9; i++) {
            adv.fireTriggerNode(trigger, NO_GROUPS);
        }
        Thread.sleep(100);
        verify(mockNotif, never()).fire(any());

        // 10th fire: count == 10, sound should fire exactly once
        adv.fireTriggerNode(trigger, NO_GROUPS);
        Thread.sleep(100);
        verify(mockNotif, times(1)).fire(any());

        // 11th fire: count == 11, sound should NOT fire again
        adv.fireTriggerNode(trigger, NO_GROUPS);
        Thread.sleep(100);
        verify(mockNotif, times(1)).fire(any());
    }

    @Test
    public void resetInputIsMultiConnection() {
        Graph graph = new Graph();
        TriggerNode trigger1 = new TriggerNode(new ChatAlert("a"));
        TriggerNode trigger2 = new TriggerNode(new ChatAlert("b"));
        Counter counter = new Counter();

        graph.add(trigger1);
        graph.add(trigger2);
        graph.add(counter);

        assertTrue(graph.connect(trigger1.getExec(), counter.getReset()));
        assertTrue(graph.connect(trigger2.getExec(), counter.getReset()));
    }
}
