package com.adamk33n3r.runelite.watchdog.nodegraph;

import com.adamk33n3r.nodegraph.Connection;
import com.adamk33n3r.nodegraph.VarInput;
import com.adamk33n3r.nodegraph.VarOutput;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.*;

public class VarConnectChangeTest {

    // ── VarOutput ────────────────────────────────────────────────────────────

    @Test
    public void varOutput_firesTrue_onAdd() {
        VarOutput<Integer> output = new VarOutput<>(null, "out", Integer.class, 0);
        VarInput<Integer> input = new VarInput<>(null, "in", Integer.class, 0);

        AtomicInteger trueCount = new AtomicInteger(0);
        output.onConnectChange(c -> { if (c) trueCount.incrementAndGet(); });

        new Connection<>(output, input);

        assertEquals(1, trueCount.get());
    }

    @Test
    public void varOutput_firesFalse_onRemove_whenStillConnected() {
        VarOutput<Integer> output = new VarOutput<>(null, "out", Integer.class, 0);
        VarInput<Integer> inputA = new VarInput<>(null, "inA", Integer.class, 0);
        VarInput<Integer> inputB = new VarInput<>(null, "inB", Integer.class, 0);
        Connection<Integer> connA = new Connection<>(output, inputA);
        Connection<Integer> connB = new Connection<>(output, inputB);

        AtomicInteger falseCount = new AtomicInteger(0);
        output.onConnectChange(c -> { if (!c) falseCount.incrementAndGet(); });

        // Remove one connection — output still has connB, but event must still fire
        connA.remove();

        assertEquals("should fire false even with remaining connections", 1, falseCount.get());
        assertTrue("output should still be connected", output.isConnected());
    }

    @Test
    public void varOutput_firesFalse_onRemove_whenEmpty() {
        VarOutput<Integer> output = new VarOutput<>(null, "out", Integer.class, 0);
        VarInput<Integer> input = new VarInput<>(null, "in", Integer.class, 0);
        Connection<Integer> conn = new Connection<>(output, input);

        AtomicInteger falseCount = new AtomicInteger(0);
        output.onConnectChange(c -> { if (!c) falseCount.incrementAndGet(); });

        conn.remove();

        assertEquals(1, falseCount.get());
        assertFalse(output.isConnected());
    }

    @Test
    public void varOutput_isConnected_reflectsActualState() {
        VarOutput<Integer> output = new VarOutput<>(null, "out", Integer.class, 0);
        VarInput<Integer> input = new VarInput<>(null, "in", Integer.class, 0);

        assertFalse(output.isConnected());
        Connection<Integer> conn = new Connection<>(output, input);
        assertTrue(output.isConnected());
        conn.remove();
        assertFalse(output.isConnected());
    }

    // ── VarInput ─────────────────────────────────────────────────────────────

    @Test
    public void varInput_firesTrue_onAdd() {
        VarOutput<Integer> output = new VarOutput<>(null, "out", Integer.class, 0);
        VarInput<Integer> input = new VarInput<>(null, "in", Integer.class, 0);

        AtomicInteger trueCount = new AtomicInteger(0);
        input.onConnectChange(c -> { if (c) trueCount.incrementAndGet(); });

        new Connection<>(output, input);

        assertEquals(1, trueCount.get());
    }

    @Test
    public void varInput_firesFalse_onRemove_whenStillConnected() {
        VarOutput<Integer> outputA = new VarOutput<>(null, "outA", Integer.class, 0);
        VarOutput<Integer> outputB = new VarOutput<>(null, "outB", Integer.class, 0);
        VarInput<Integer> input = new VarInput<>(null, "in", Integer.class, 0);
        input.setAllowMultipleConnections(true);
        Connection<Integer> connA = new Connection<>(outputA, input);
        Connection<Integer> connB = new Connection<>(outputB, input);

        AtomicInteger falseCount = new AtomicInteger(0);
        input.onConnectChange(c -> { if (!c) falseCount.incrementAndGet(); });

        connA.remove();

        assertEquals("should fire false even with remaining connections", 1, falseCount.get());
        assertTrue("input should still be connected", input.isConnected());
    }

    @Test
    public void varInput_firesFalse_onRemove_whenEmpty() {
        VarOutput<Integer> output = new VarOutput<>(null, "out", Integer.class, 0);
        VarInput<Integer> input = new VarInput<>(null, "in", Integer.class, 0);
        Connection<Integer> conn = new Connection<>(output, input);

        AtomicInteger falseCount = new AtomicInteger(0);
        input.onConnectChange(c -> { if (!c) falseCount.incrementAndGet(); });

        conn.remove();

        assertEquals(1, falseCount.get());
        assertFalse(input.isConnected());
    }

    @Test
    public void varInput_isConnected_reflectsActualState() {
        VarOutput<Integer> output = new VarOutput<>(null, "out", Integer.class, 0);
        VarInput<Integer> input = new VarInput<>(null, "in", Integer.class, 0);

        assertFalse(input.isConnected());
        Connection<Integer> conn = new Connection<>(output, input);
        assertTrue(input.isConnected());
        conn.remove();
        assertFalse(input.isConnected());
    }
}
