package com.adamk33n3r.runelite.watchdog.nodegraph;

import com.adamk33n3r.nodegraph.nodes.math.Ceiling;
import com.adamk33n3r.nodegraph.nodes.math.Floor;
import com.adamk33n3r.nodegraph.nodes.math.MathNode;
import com.adamk33n3r.nodegraph.nodes.math.Round;
import org.junit.Test;

import static org.junit.Assert.*;

public class MathNodeRoundingTest {

    // --- Floor ---

    @Test
    public void floor_extends_math_node() {
        assertTrue(new Floor() instanceof MathNode);
    }

    @Test
    public void floor_default_value_is_zero() {
        Floor floor = new Floor();
        floor.process();
        assertEquals(0.0, floor.getResult().getValue().doubleValue(), 0.0001);
    }

    @Test
    public void floor_rounds_positive_fraction_down() {
        Floor floor = new Floor();
        floor.getValue().setValue(3.9);
        floor.process();
        assertEquals(3.0, floor.getResult().getValue().doubleValue(), 0.0001);
    }

    @Test
    public void floor_rounds_negative_fraction_down() {
        Floor floor = new Floor();
        floor.getValue().setValue(-2.1);
        floor.process();
        assertEquals(-3.0, floor.getResult().getValue().doubleValue(), 0.0001);
    }

    @Test
    public void floor_recomputes_reactively_on_value_change() {
        Floor floor = new Floor();
        floor.getValue().setValue(1.7);
        assertEquals(1.0, floor.getResult().getValue().doubleValue(), 0.0001);

        floor.getValue().setValue(4.9);
        assertEquals(4.0, floor.getResult().getValue().doubleValue(), 0.0001);
    }

    // --- Ceiling ---

    @Test
    public void ceiling_extends_math_node() {
        assertTrue(new Ceiling() instanceof MathNode);
    }

    @Test
    public void ceiling_default_value_is_zero() {
        Ceiling ceiling = new Ceiling();
        ceiling.process();
        assertEquals(0.0, ceiling.getResult().getValue().doubleValue(), 0.0001);
    }

    @Test
    public void ceiling_rounds_positive_fraction_up() {
        Ceiling ceiling = new Ceiling();
        ceiling.getValue().setValue(3.1);
        ceiling.process();
        assertEquals(4.0, ceiling.getResult().getValue().doubleValue(), 0.0001);
    }

    @Test
    public void ceiling_rounds_negative_fraction_up() {
        Ceiling ceiling = new Ceiling();
        ceiling.getValue().setValue(-2.9);
        ceiling.process();
        assertEquals(-2.0, ceiling.getResult().getValue().doubleValue(), 0.0001);
    }

    @Test
    public void ceiling_recomputes_reactively_on_value_change() {
        Ceiling ceiling = new Ceiling();
        ceiling.getValue().setValue(1.2);
        assertEquals(2.0, ceiling.getResult().getValue().doubleValue(), 0.0001);

        ceiling.getValue().setValue(5.0);
        assertEquals(5.0, ceiling.getResult().getValue().doubleValue(), 0.0001);
    }

    // --- Round ---

    @Test
    public void round_extends_math_node() {
        assertTrue(new Round() instanceof MathNode);
    }

    @Test
    public void round_default_value_is_zero() {
        Round round = new Round();
        round.process();
        assertEquals(0.0, round.getResult().getValue().doubleValue(), 0.0001);
    }

    @Test
    public void round_rounds_up_at_half() {
        Round round = new Round();
        round.getValue().setValue(2.5);
        round.process();
        assertEquals(3.0, round.getResult().getValue().doubleValue(), 0.0001);
    }

    @Test
    public void round_rounds_down_below_half() {
        Round round = new Round();
        round.getValue().setValue(2.4);
        round.process();
        assertEquals(2.0, round.getResult().getValue().doubleValue(), 0.0001);
    }

    @Test
    public void round_recomputes_reactively_on_value_change() {
        Round round = new Round();
        round.getValue().setValue(1.6);
        assertEquals(2.0, round.getResult().getValue().doubleValue(), 0.0001);

        round.getValue().setValue(1.4);
        assertEquals(1.0, round.getResult().getValue().doubleValue(), 0.0001);
    }
}
