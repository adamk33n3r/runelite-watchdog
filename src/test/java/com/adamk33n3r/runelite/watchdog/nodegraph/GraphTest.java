package com.adamk33n3r.runelite.watchdog.nodegraph;

import com.adamk33n3r.nodegraph.Connection;
import com.adamk33n3r.nodegraph.Graph;
import com.adamk33n3r.nodegraph.VarInput;
import com.adamk33n3r.nodegraph.VarOutput;
import com.adamk33n3r.nodegraph.nodes.Dummy;
import com.adamk33n3r.nodegraph.nodes.Logger;
import com.adamk33n3r.nodegraph.nodes.constants.Num;
import com.adamk33n3r.nodegraph.nodes.math.Add;
import org.junit.Test;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertEquals;

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
}
