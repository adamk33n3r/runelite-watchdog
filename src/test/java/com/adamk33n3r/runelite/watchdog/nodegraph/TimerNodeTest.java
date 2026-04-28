package com.adamk33n3r.runelite.watchdog.nodegraph;

import com.adamk33n3r.nodegraph.Graph;
import com.adamk33n3r.nodegraph.nodes.ActionNode;
import com.adamk33n3r.nodegraph.nodes.TriggerNode;
import com.adamk33n3r.nodegraph.nodes.flow.TimerNode;
import com.adamk33n3r.runelite.watchdog.alerts.ChatAlert;
import com.adamk33n3r.runelite.watchdog.notifications.Notification;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class TimerNodeTest {

    private static final String[] NO_GROUPS = new String[0];
    private static final int DURATION_MS = 100;

    // ── Defaults and structural checks ──────────────────────────────────────

    @Test
    public void durationMs_defaultsTo1000() {
        TimerNode timer = new TimerNode();
        assertEquals(1000, timer.getDurationMs().getValue().intValue());
    }

    @Test
    public void pulse_defaultsToFalse() {
        TimerNode timer = new TimerNode();
        assertFalse(timer.getPulse().getValue());
    }

    @Test
    public void execIn_disallowsMultipleConnections() {
        TimerNode timer = new TimerNode();
        assertFalse(timer.getExec().isAllowMultipleConnections());
    }

    @Test
    public void resetIn_allowsMultipleConnections() {
        TimerNode timer = new TimerNode();
        assertTrue(timer.getReset().isAllowMultipleConnections());
    }

    // ── Single-shot (pulse=false) behaviour ─────────────────────────────────

    @Test
    public void execOut_firesOnceAfterDuration_whenPulseFalse() throws InterruptedException {
        Graph graph = new Graph();
        TriggerNode trigger = triggerNode();
        TimerNode timer = timerNode(DURATION_MS, false);
        Notification mockNotif = Mockito.mock(Notification.class);
        ActionNode action = new ActionNode(mockNotif);

        graph.add(trigger);
        graph.add(timer);
        graph.add(action);
        graph.connect(trigger.getExec(), timer.getExec());
        graph.connect(timer.getExecOut(), action.getExec());

        CountDownLatch fired = new CountDownLatch(1);
        Mockito.doAnswer(inv -> { fired.countDown(); return null; }).when(mockNotif).fire(Mockito.any());

        graph.executeExecChain(trigger, NO_GROUPS);

        assertTrue("execOut should fire within 500ms", fired.await(500, TimeUnit.MILLISECONDS));
        Thread.sleep(DURATION_MS * 2);
        verify(mockNotif, times(1)).fire(Mockito.any());
    }

    @Test
    public void execOut_doesNotFireBeforeDuration() throws InterruptedException {
        Graph graph = new Graph();
        TriggerNode trigger = triggerNode();
        TimerNode timer = timerNode(DURATION_MS, false);
        Notification mockNotif = Mockito.mock(Notification.class);
        ActionNode action = new ActionNode(mockNotif);

        graph.add(trigger);
        graph.add(timer);
        graph.add(action);
        graph.connect(trigger.getExec(), timer.getExec());
        graph.connect(timer.getExecOut(), action.getExec());

        graph.executeExecChain(trigger, NO_GROUPS);

        Thread.sleep(DURATION_MS / 2);
        verify(mockNotif, never()).fire(Mockito.any());

        Thread.sleep(DURATION_MS + 50);
        verify(mockNotif, times(1)).fire(Mockito.any());
    }

    @Test
    public void reset_cancelsPendingExec() throws InterruptedException {
        Graph graph = new Graph();
        TriggerNode trigger = triggerNode();
        TriggerNode resetTrigger = triggerNode();
        TimerNode timer = timerNode(DURATION_MS, false);
        Notification mockNotif = Mockito.mock(Notification.class);
        ActionNode action = new ActionNode(mockNotif);

        graph.add(trigger);
        graph.add(resetTrigger);
        graph.add(timer);
        graph.add(action);
        graph.connect(trigger.getExec(), timer.getExec());
        graph.connect(resetTrigger.getExec(), timer.getReset());
        graph.connect(timer.getExecOut(), action.getExec());

        graph.executeExecChain(trigger, NO_GROUPS);
        Thread.sleep(DURATION_MS / 2);
        graph.executeExecChain(resetTrigger, NO_GROUPS);

        Thread.sleep(DURATION_MS * 3);
        verify(mockNotif, never()).fire(Mockito.any());
    }

    @Test
    public void exec_whileRunning_restartsCountdown() throws InterruptedException {
        Graph graph = new Graph();
        TriggerNode trigger = triggerNode();
        TimerNode timer = timerNode(DURATION_MS, false);
        Notification mockNotif = Mockito.mock(Notification.class);
        ActionNode action = new ActionNode(mockNotif);

        graph.add(trigger);
        graph.add(timer);
        graph.add(action);
        graph.connect(trigger.getExec(), timer.getExec());
        graph.connect(timer.getExecOut(), action.getExec());

        CountDownLatch fired = new CountDownLatch(1);
        Mockito.doAnswer(inv -> { fired.countDown(); return null; }).when(mockNotif).fire(Mockito.any());

        long t0 = System.currentTimeMillis();
        graph.executeExecChain(trigger, NO_GROUPS);
        Thread.sleep(DURATION_MS / 2); // restart before first would have fired
        graph.executeExecChain(trigger, NO_GROUPS);

        assertTrue("execOut should fire within 500ms of second Exec", fired.await(500, TimeUnit.MILLISECONDS));
        long elapsed = System.currentTimeMillis() - t0;
        // Should fire ~DURATION_MS + DURATION_MS/2 after t0, NOT at DURATION_MS
        assertTrue("Should not fire before restart delay elapses", elapsed >= (long) DURATION_MS);
        verify(mockNotif, times(1)).fire(Mockito.any());
    }

    @Test
    public void pulseOut_silentWhenPulseFalse() throws InterruptedException {
        Graph graph = new Graph();
        TriggerNode trigger = triggerNode();
        TriggerNode resetTrigger = triggerNode();
        TimerNode timer = timerNode(DURATION_MS, false);
        Notification execNotif = Mockito.mock(Notification.class);
        Notification pulseNotif = Mockito.mock(Notification.class);
        ActionNode execAction = new ActionNode(execNotif);
        ActionNode pulseAction = new ActionNode(pulseNotif);

        graph.add(trigger);
        graph.add(resetTrigger);
        graph.add(timer);
        graph.add(execAction);
        graph.add(pulseAction);
        graph.connect(trigger.getExec(), timer.getExec());
        graph.connect(resetTrigger.getExec(), timer.getReset());
        graph.connect(timer.getExecOut(), execAction.getExec());
        graph.connect(timer.getPulseOut(), pulseAction.getExec());

        CountDownLatch execFired = new CountDownLatch(1);
        Mockito.doAnswer(inv -> { execFired.countDown(); return null; }).when(execNotif).fire(Mockito.any());

        graph.executeExecChain(trigger, NO_GROUPS);
        assertTrue("execOut should fire", execFired.await(500, TimeUnit.MILLISECONDS));
        graph.executeExecChain(resetTrigger, NO_GROUPS);

        Thread.sleep(DURATION_MS * 3);
        verify(pulseNotif, never()).fire(Mockito.any());
    }

    // ── Pulse mode behaviour ─────────────────────────────────────────────────

    @Test
    public void pulseMode_firesExecOutOnceThenPulseOutRepeatedly() throws InterruptedException {
        Graph graph = new Graph();
        TriggerNode trigger = triggerNode();
        TriggerNode resetTrigger = triggerNode();
        TimerNode timer = timerNode(DURATION_MS, true);
        Notification execNotif = Mockito.mock(Notification.class);
        Notification pulseNotif = Mockito.mock(Notification.class);
        ActionNode execAction = new ActionNode(execNotif);
        ActionNode pulseAction = new ActionNode(pulseNotif);

        graph.add(trigger);
        graph.add(resetTrigger);
        graph.add(timer);
        graph.add(execAction);
        graph.add(pulseAction);
        graph.connect(trigger.getExec(), timer.getExec());
        graph.connect(resetTrigger.getExec(), timer.getReset());
        graph.connect(timer.getExecOut(), execAction.getExec());
        graph.connect(timer.getPulseOut(), pulseAction.getExec());

        int pulseCount = 3;
        CountDownLatch pulseLatch = new CountDownLatch(pulseCount);
        Mockito.doAnswer(inv -> { pulseLatch.countDown(); return null; }).when(pulseNotif).fire(Mockito.any());

        graph.executeExecChain(trigger, NO_GROUPS);
        // allow time for execOut fire + pulseCount pulse ticks
        assertTrue("pulseOut should fire at least " + pulseCount + " times", pulseLatch.await(1000, TimeUnit.MILLISECONDS));

        graph.executeExecChain(resetTrigger, NO_GROUPS);
        Thread.sleep(50);

        verify(execNotif, times(1)).fire(Mockito.any());
    }

    @Test
    public void pulseMode_pulseOutDoesNotFireBeforeFirstCompletion() throws InterruptedException {
        Graph graph = new Graph();
        TriggerNode trigger = triggerNode();
        TriggerNode resetTrigger = triggerNode();
        TimerNode timer = timerNode(DURATION_MS, true);
        Notification pulseNotif = Mockito.mock(Notification.class);
        ActionNode pulseAction = new ActionNode(pulseNotif);

        graph.add(trigger);
        graph.add(resetTrigger);
        graph.add(timer);
        graph.add(pulseAction);
        graph.connect(trigger.getExec(), timer.getExec());
        graph.connect(resetTrigger.getExec(), timer.getReset());
        graph.connect(timer.getPulseOut(), pulseAction.getExec());

        graph.executeExecChain(trigger, NO_GROUPS);
        Thread.sleep(DURATION_MS / 2);
        verify(pulseNotif, never()).fire(Mockito.any());

        graph.executeExecChain(resetTrigger, NO_GROUPS);
    }

    @Test
    public void resetDuringPulse_stopsFurtherFires() throws InterruptedException {
        Graph graph = new Graph();
        TriggerNode trigger = triggerNode();
        TriggerNode resetTrigger = triggerNode();
        TimerNode timer = timerNode(DURATION_MS, true);
        Notification execNotif = Mockito.mock(Notification.class);
        Notification pulseNotif = Mockito.mock(Notification.class);
        ActionNode execAction = new ActionNode(execNotif);
        ActionNode pulseAction = new ActionNode(pulseNotif);

        graph.add(trigger);
        graph.add(resetTrigger);
        graph.add(timer);
        graph.add(execAction);
        graph.add(pulseAction);
        graph.connect(trigger.getExec(), timer.getExec());
        graph.connect(resetTrigger.getExec(), timer.getReset());
        graph.connect(timer.getExecOut(), execAction.getExec());
        graph.connect(timer.getPulseOut(), pulseAction.getExec());

        // Wait for the first pulseOut tick before resetting
        CountDownLatch firstPulse = new CountDownLatch(1);
        Mockito.doAnswer(inv -> { firstPulse.countDown(); return null; }).when(pulseNotif).fire(Mockito.any());

        graph.executeExecChain(trigger, NO_GROUPS);
        assertTrue("First pulse should fire", firstPulse.await(500, TimeUnit.MILLISECONDS));

        // Reset immediately after the first pulse tick
        graph.executeExecChain(resetTrigger, NO_GROUPS);
        int countAfterReset = Mockito.mockingDetails(pulseNotif).getInvocations().size();

        Thread.sleep(DURATION_MS * 4);
        int countAfterWait = Mockito.mockingDetails(pulseNotif).getInvocations().size();
        assertEquals("No additional pulse fires after reset", countAfterReset, countAfterWait);
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private static TriggerNode triggerNode() {
        return new TriggerNode(new ChatAlert("test"));
    }

    private static TimerNode timerNode(int durationMs, boolean pulse) {
        TimerNode timer = new TimerNode();
        timer.getDurationMs().setValue(durationMs);
        timer.getPulse().setValue(pulse);
        return timer;
    }
}
