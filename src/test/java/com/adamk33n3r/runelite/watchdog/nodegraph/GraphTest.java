package com.adamk33n3r.runelite.watchdog.nodegraph;

import com.adamk33n3r.nodegraph.Connection;
import com.adamk33n3r.nodegraph.Graph;
import com.adamk33n3r.nodegraph.VarInput;
import com.adamk33n3r.nodegraph.VarOutput;
import com.adamk33n3r.nodegraph.nodes.Dummy;
import com.adamk33n3r.nodegraph.nodes.Logger;
import com.adamk33n3r.nodegraph.nodes.constants.Num;
import com.adamk33n3r.nodegraph.nodes.flow.Counter;
import com.adamk33n3r.nodegraph.nodes.flow.TimerNode;
import com.adamk33n3r.nodegraph.nodes.math.Add;
import org.junit.Test;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.*;


public class GraphTest {
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(GraphTest.class);

    @Test
    public void test_vars_update() {
        VarOutput<Integer> output = new VarOutput<>(null, "output", Integer.class, 0);
        VarInput<Integer> input = new VarInput<>(null, "input", Integer.class, 0);
        new Connection<>(output, input);

        assertEquals(Integer.valueOf(0), input.getValue());
        output.setValue(5);
        assertEquals(Integer.valueOf(5), input.getValue());
    }

    @Test
    public void test_multiple_connections() {
        VarOutput<Integer> output = new VarOutput<>(null, "output", Integer.class, 0);
        VarInput<Integer> input = new VarInput<>(null, "input", Integer.class, 0);
        VarInput<Integer> input1 = new VarInput<>(null, "input1", Integer.class, 0);
        new Connection<>(output, input);
        new Connection<>(output, input1);

        assertEquals(Integer.valueOf(0), input.getValue());
        assertEquals(Integer.valueOf(0), input1.getValue());
        output.setValue(5);
        assertEquals(Integer.valueOf(5), input.getValue());
        assertEquals(Integer.valueOf(5), input1.getValue());
    }

    @Test
    public void test_connection_removal() {
        VarOutput<Integer> output = new VarOutput<>(null, "output", Integer.class, 0);
        VarInput<Integer> input = new VarInput<>(null, "input", Integer.class, 0);
        Connection<Integer> connection = new Connection<>(output, input);

        assertEquals(Integer.valueOf(0), input.getValue());
        output.setValue(5);
        assertEquals(Integer.valueOf(5), input.getValue());
        connection.remove();
        output.setValue(10);
        assertEquals(Integer.valueOf(5), input.getValue());
    }

    @Test
    public void test_add_node() {
        Graph graph = new Graph();

        Num numNodeA = new Num();
        numNodeA.setValue(5);
        graph.add(numNodeA);
        Num numNodeB = new Num();
        numNodeB.setValue(10);
        graph.add(numNodeB);

        Add add = new Add();
        graph.add(add);

        Logger<java.lang.Number> logger = new Logger<>(null);
        graph.add(logger);

        Dummy<java.lang.Number> dummy = new Dummy<>(null);
        graph.add(dummy);

        graph.connect(numNodeA.getValue(), add.getA());
        graph.connect(numNodeB.getValue(), add.getB());
        graph.connect(add.getResult(), logger.getInput());
        graph.connect(add.getResult(), dummy.getInput());

        add.process();
        logger.process();
        dummy.process();

        assertEquals(15, dummy.getOutput().getValue().intValue());
    }

    @Test
    public void test_varOutput_setValue_fires_connected_input_onChange() {
        VarOutput<Integer> output = new VarOutput<>(null, "output", Integer.class, 0);
        VarInput<Integer> input = new VarInput<>(null, "input", Integer.class, 0);
        new Connection<>(output, input);

        AtomicInteger fireCount = new AtomicInteger(0);
        AtomicInteger lastSeen = new AtomicInteger(-1);
        input.onChange(v -> {
            fireCount.incrementAndGet();
            lastSeen.set(v);
        });

        output.setValue(42);
        assertEquals(1, fireCount.get());
        assertEquals(42, lastSeen.get());

        output.setValue(99);
        assertEquals(2, fireCount.get());
        assertEquals(99, lastSeen.get());
    }

    @Test
    public void test_varOutput_setValue_doesnt_fire_after_connection_removed() {
        VarOutput<Integer> output = new VarOutput<>(null, "output", Integer.class, 0);
        VarInput<Integer> input = new VarInput<>(null, "input", Integer.class, 0);
        Connection<Integer> connection = new Connection<>(output, input);

        AtomicInteger fireCount = new AtomicInteger(0);
        input.onChange(v -> fireCount.incrementAndGet());

        output.setValue(1);
        assertEquals("should fire before removal", 1, fireCount.get());

        connection.remove();

        output.setValue(2);
        assertEquals("must NOT fire after connection removed", 1, fireCount.get());
    }

    @Test
    public void varOutput_send_doesNotPushToStaleInput_afterOverwrite() {
        Graph graph = new Graph();
        Num outA = new Num();
        Num outB = new Num();
        Add inX = new Add();
        graph.add(outA);
        graph.add(outB);
        graph.add(inX);

        graph.connect(outA.getValue(), inX.getA());
        // outB overwrites outA on inX.a
        graph.connect(outB.getValue(), inX.getA());

        outA.setValue(42);
        // inX.a should NOT be 42 (outA→inX was disconnected)
        assertNotEquals(42.0, inX.getA().getValue().doubleValue(), 0.001);
    }

    @Test
    public void varOutput_isConnected_reflectsGraphTruth() {
        Graph graph = new Graph();
        Num outA = new Num();
        Add inX = new Add();
        graph.add(outA);
        graph.add(inX);

        graph.connect(outA.getValue(), inX.getA());
        assertTrue(outA.getValue().isConnected());

        graph.disconnect(outA.getValue(), inX.getA());
        assertFalse(outA.getValue().isConnected());
    }

    @Test
    public void varOutput_send_fromOrphanedOutput_doesNotCrash() {
        Graph graph = new Graph();
        Num num = new Num();
        graph.add(num);
        graph.remove(num);
        // Should not throw
        num.setValue(99);
    }

    @Test
    public void test_cycle_allows_back_edge_into_counter_reset() {
        Graph graph = new Graph();
        Counter counter1 = new Counter();
        Counter counter2 = new Counter();
        graph.add(counter1);
        graph.add(counter2);

        assertTrue(graph.connect(counter1.getExecOut(), counter2.getExec()));
        assertTrue(graph.connect(counter2.getExecOut(), counter1.getReset()));
    }

    @Test
    public void test_cycle_allows_direct_self_loop_into_reset() {
        Graph graph = new Graph();
        Counter counter = new Counter();
        graph.add(counter);

        assertTrue(graph.connect(counter.getExecOut(), counter.getReset()));
    }

    @Test
    public void test_cycle_blocks_back_edge_into_counter_main_exec() {
        Graph graph = new Graph();
        Counter counter1 = new Counter();
        Counter counter2 = new Counter();
        graph.add(counter1);
        graph.add(counter2);

        assertTrue(graph.connect(counter1.getExecOut(), counter2.getExec()));
        assertFalse(graph.connect(counter2.getExecOut(), counter1.getExec()));
    }

    @Test
    public void test_cycle_allows_back_edge_into_timer_reset() {
        Graph graph = new Graph();
        TimerNode timer = new TimerNode();
        Counter counter = new Counter();
        graph.add(timer);
        graph.add(counter);

        assertTrue(graph.connect(timer.getExecOut(), counter.getExec()));
        assertTrue(graph.connect(counter.getExecOut(), timer.getReset()));
    }

    @Test
    public void test_cycle_allows_unrelated_connection_when_reset_edge_exists() {
        Graph graph = new Graph();
        Counter counter1 = new Counter();
        Counter counter2 = new Counter();
        graph.add(counter1);
        graph.add(counter2);

        assertTrue(graph.connect(counter2.getExecOut(), counter1.getReset()));
        // Without the in-walk terminal filter, the existing reset edge would generate a false-positive cycle
        assertTrue(graph.connect(counter1.getExecOut(), counter2.getExec()));
    }

    @Test
    public void test_cycle_blocks_pure_exec_cycle_without_reset() {
        Graph graph = new Graph();
        Counter counter1 = new Counter();
        Counter counter2 = new Counter();
        Counter counter3 = new Counter();
        graph.add(counter1);
        graph.add(counter2);
        graph.add(counter3);

        assertTrue(graph.connect(counter1.getExecOut(), counter2.getExec()));
        assertTrue(graph.connect(counter2.getExecOut(), counter3.getExec()));
        assertFalse(graph.connect(counter3.getExecOut(), counter1.getExec()));
    }
}
