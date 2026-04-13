package com.adamk33n3r.runelite.watchdog.nodegraph;

import com.adamk33n3r.nodegraph.Connection;
import com.adamk33n3r.nodegraph.VarInput;
import com.adamk33n3r.nodegraph.VarOutput;
import com.adamk33n3r.nodegraph.nodes.constants.Bool;
import com.adamk33n3r.nodegraph.nodes.constants.Location;
import com.adamk33n3r.nodegraph.nodes.constants.Num;
import com.adamk33n3r.nodegraph.nodes.logic.BooleanGate;
import com.adamk33n3r.nodegraph.nodes.logic.Equality;
import com.adamk33n3r.nodegraph.nodes.logic.LocationCompare;
import net.runelite.api.coords.WorldPoint;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.*;

public class LogicNodeConnectionTest {

    // 1. Connecting to Equality inputs must not cause a StackOverflowError
    @Test
    public void test_equality_connection_does_not_stack_overflow() {
        Num numA = new Num();
        numA.setValue(5);
        Equality equality = new Equality();
        // This was the crash site — should complete without StackOverflowError
        new Connection<>(numA.getValue(), equality.getA());
    }

    // 2. Connecting to BooleanGate inputs must not cause a StackOverflowError
    @Test
    public void test_boolean_gate_connection_does_not_stack_overflow() {
        Bool boolA = new Bool();
        boolA.setValue(true);
        BooleanGate gate = new BooleanGate();
        // This was the crash site — should complete without StackOverflowError
        new Connection<>(boolA.getValueOut(), gate.getA());
    }

    // 3. Equality computes correctly after wiring both inputs
    @Test
    public void test_equality_computes_correctly_after_connection() {
        Num numA = new Num();
        Num numB = new Num();
        numA.setValue(5);
        numB.setValue(10);

        Equality equality = new Equality();
        new Connection<>(numA.getValue(), equality.getA());
        new Connection<>(numB.getValue(), equality.getB());

        // 5 == 10 → false
        assertFalse(equality.getResult().getValue());

        // Push new value: 10 == 10 → true
        numA.setValue(10);
        assertTrue(equality.getResult().getValue());
    }

    // 4. BooleanGate (AND) computes correctly after wiring both inputs
    @Test
    public void test_boolean_gate_computes_correctly_after_connection() {
        Bool boolA = new Bool();
        Bool boolB = new Bool();
        boolA.setValue(true);
        boolB.setValue(false);

        BooleanGate gate = new BooleanGate();
        new Connection<>(boolA.getValueOut(), gate.getA());
        new Connection<>(boolB.getValueOut(), gate.getB());

        // true AND false → false
        assertFalse(gate.getResult().getValue());

        // Push: true AND true → true
        boolB.setValue(true);
        assertTrue(gate.getResult().getValue());
    }

    // 5. receive() must NOT fire onChange — onChange is only for push-based setValue()
    @Test
    public void test_var_input_receive_does_not_fire_onChange_on_pull() {
        VarOutput<Integer> output = new VarOutput<>(null, "out", Integer.class, 42);
        VarInput<Integer> input = new VarInput<>(null, "in", Integer.class, 0);

        AtomicInteger onChangeCount = new AtomicInteger(0);
        input.onChange(v -> onChangeCount.incrementAndGet());

        // Connecting calls setValue() once (push) — onChange should fire exactly once
        new Connection<>(output, input);
        assertEquals("onChange should fire once on initial push via setValue", 1, onChangeCount.get());

        // Subsequent getValue() calls are pulls — onChange must NOT fire
        input.getValue();
        input.getValue();
        input.getValue();
        assertEquals("onChange must not fire on pull (getValue)", 1, onChangeCount.get());
    }

    // 6. Chained logic nodes: Num → Equality → BooleanGate connects without overflow and produces correct result
    @Test
    public void test_chained_logic_nodes_do_not_overflow() {
        Num numA = new Num();
        Num numB = new Num();
        numA.setValue(5);
        numB.setValue(5);

        // Equality: 5 == 5 → true
        Equality equality = new Equality();
        new Connection<>(numA.getValue(), equality.getA());
        new Connection<>(numB.getValue(), equality.getB());

        Bool constTrue = new Bool();
        constTrue.setValue(true);

        // BooleanGate: true AND (5 == 5) → true AND true → true
        BooleanGate gate = new BooleanGate();
        new Connection<>(equality.getResult(), gate.getA());
        new Connection<>(constTrue.getValueOut(), gate.getB());

        assertTrue(gate.getResult().getValue());

        // Now make equality false: 5 == 10 → false; gate: false AND true → false
        numB.setValue(10);
        assertFalse(gate.getResult().getValue());
    }

    // 7. LocationCompare: same point at distance 0 → true
    @Test
    public void test_locationCompare_samePoint_isTrue() {
        LocationCompare lc = new LocationCompare();
        lc.getA().setValue(new WorldPoint(3200, 3200, 0));
        lc.getB().setValue(new WorldPoint(3200, 3200, 0));
        lc.setDistance(0);
        lc.process();
        assertTrue(lc.getResult().getValue());
    }

    // 8. LocationCompare: points far apart at distance 0 → false
    @Test
    public void test_locationCompare_outOfRange_isFalse() {
        LocationCompare lc = new LocationCompare();
        lc.getA().setValue(new WorldPoint(3200, 3200, 0));
        lc.getB().setValue(new WorldPoint(3210, 3210, 0));
        lc.setDistance(0);
        lc.process();
        assertFalse(lc.getResult().getValue());
    }

    // 9. LocationCompare: diagonal points with cardinalOnly → false
    @Test
    public void test_locationCompare_cardinalOnly_diagonal_isFalse() {
        LocationCompare lc = new LocationCompare();
        lc.getA().setValue(new WorldPoint(3200, 3200, 0));
        lc.getB().setValue(new WorldPoint(3201, 3201, 0));
        lc.setDistance(10);
        lc.setCardinalOnly(true);
        lc.process();
        assertFalse(lc.getResult().getValue());
    }

    // 10. LocationCompare: cardinal directions pass with cardinalOnly
    @Test
    public void test_locationCompare_cardinalOnly_sameX_isTrue() {
        LocationCompare lc = new LocationCompare();
        lc.getA().setValue(new WorldPoint(3200, 3200, 0));
        lc.getB().setValue(new WorldPoint(3200, 3203, 0));
        lc.setDistance(5);
        lc.setCardinalOnly(true);
        lc.process();
        assertTrue(lc.getResult().getValue());
    }

    // 11. LocationCompare: points within specified distance → true
    @Test
    public void test_locationCompare_withinDistance_isTrue() {
        LocationCompare lc = new LocationCompare();
        lc.getA().setValue(new WorldPoint(3200, 3200, 0));
        lc.getB().setValue(new WorldPoint(3203, 3204, 0));
        lc.setDistance(5);
        lc.process();
        assertTrue(lc.getResult().getValue());
    }

    // 12. Connecting Location outputs to LocationCompare inputs does not stack overflow
    @Test
    public void test_locationCompare_connection_does_not_stack_overflow() {
        Location locA = new Location();
        locA.setValue(new WorldPoint(3200, 3200, 0));
        LocationCompare lc = new LocationCompare();
        new Connection<>(locA.getValue(), lc.getA());
    }

    // 13. LocationCompare auto-recomputes when input changes via connection
    @Test
    public void test_locationCompare_recomputes_on_push() {
        Location locA = new Location();
        Location locB = new Location();
        locA.setValue(new WorldPoint(3200, 3200, 0));
        locB.setValue(new WorldPoint(3200, 3200, 0));

        LocationCompare lc = new LocationCompare();
        lc.setDistance(0);
        new Connection<>(locA.getValue(), lc.getA());
        new Connection<>(locB.getValue(), lc.getB());

        // Same point → true
        assertTrue(lc.getResult().getValue());

        // Push new location → different point → false
        locB.setValue(new WorldPoint(3205, 3205, 0));
        assertFalse(lc.getResult().getValue());
    }
}
