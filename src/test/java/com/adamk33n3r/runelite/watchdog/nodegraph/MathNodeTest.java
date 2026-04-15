package com.adamk33n3r.runelite.watchdog.nodegraph;

import com.adamk33n3r.nodegraph.Connection;
import com.adamk33n3r.nodegraph.nodes.constants.Num;
import com.adamk33n3r.nodegraph.nodes.math.Add;
import com.adamk33n3r.nodegraph.nodes.math.MathNode;
import org.junit.Test;

import static org.junit.Assert.*;

public class MathNodeTest {

    // 1. Add extends MathNode
    @Test
    public void test_add_is_instance_of_math_node() {
        assertTrue(new Add() instanceof MathNode);
    }

    // 2. Default values: 0 + 0 = 0
    @Test
    public void test_add_computes_sum_from_default_values() {
        Add add = new Add();
        add.process();
        assertEquals(0.0, add.getResult().getValue().doubleValue(), 0.0001);
    }

    // 3. setValue on both inputs then process
    @Test
    public void test_add_computes_sum_after_setValue() {
        Add add = new Add();
        add.getA().setValue(3);
        add.getB().setValue(4);
        add.process();
        assertEquals(7.0, add.getResult().getValue().doubleValue(), 0.0001);
    }

    // 4. Connecting Num → Add.a must not cause a StackOverflowError
    @Test
    public void test_add_connection_does_not_stack_overflow() {
        Num num = new Num();
        num.setValue(10);
        Add add = new Add();
        new Connection<>(num.getValue(), add.getA());
    }

    // 5. Wired inputs: result updates when source values change via push
    @Test
    public void test_add_recomputes_on_input_push() {
        Num numA = new Num();
        Num numB = new Num();
        numA.setValue(5);
        numB.setValue(3);

        Add add = new Add();
        new Connection<>(numA.getValue(), add.getA());
        new Connection<>(numB.getValue(), add.getB());

        // 5 + 3 = 8
        assertEquals(8.0, add.getResult().getValue().doubleValue(), 0.0001);

        // Push a new value: 10 + 3 = 13
        numA.setValue(10);
        assertEquals(13.0, add.getResult().getValue().doubleValue(), 0.0001);
    }
}
