package com.adamk33n3r.runelite.watchdog.nodegraph;

import com.adamk33n3r.nodegraph.Graph;
import com.adamk33n3r.nodegraph.nodes.constants.Num;
import com.adamk33n3r.nodegraph.nodes.math.*;
import org.junit.Test;

import static org.junit.Assert.*;

public class MathNodeExtendedTest {

    // ── Subtract ─────────────────────────────────────────────────────────────

    @Test
    public void subtract_computesExpected_fromDefaultValues() {
        Subtract sub = new Subtract();
        sub.process();
        assertEquals(0.0, sub.getResult().getValue().doubleValue(), 0.0001);
    }

    @Test
    public void subtract_computesAfterSetValue() {
        Subtract sub = new Subtract();
        sub.getA().setValue(10);
        sub.getB().setValue(3);
        sub.process();
        assertEquals(7.0, sub.getResult().getValue().doubleValue(), 0.0001);
    }

    @Test
    public void subtract_recomputesOnInputPush() {
        Num a = new Num(); a.setValue(10);
        Num b = new Num(); b.setValue(3);
        Subtract sub = new Subtract();
        new com.adamk33n3r.nodegraph.Connection<>(a.getValue(), sub.getA());
        new com.adamk33n3r.nodegraph.Connection<>(b.getValue(), sub.getB());
        assertEquals(7.0, sub.getResult().getValue().doubleValue(), 0.0001);
        a.setValue(20);
        assertEquals(17.0, sub.getResult().getValue().doubleValue(), 0.0001);
    }

    // ── Multiply ─────────────────────────────────────────────────────────────

    @Test
    public void multiply_computesExpected_fromDefaultValues() {
        Multiply mul = new Multiply();
        mul.process();
        assertEquals(0.0, mul.getResult().getValue().doubleValue(), 0.0001);
    }

    @Test
    public void multiply_computesAfterSetValue() {
        Multiply mul = new Multiply();
        mul.getA().setValue(4);
        mul.getB().setValue(5);
        mul.process();
        assertEquals(20.0, mul.getResult().getValue().doubleValue(), 0.0001);
    }

    @Test
    public void multiply_recomputesOnInputPush() {
        Num a = new Num(); a.setValue(3);
        Num b = new Num(); b.setValue(4);
        Multiply mul = new Multiply();
        new com.adamk33n3r.nodegraph.Connection<>(a.getValue(), mul.getA());
        new com.adamk33n3r.nodegraph.Connection<>(b.getValue(), mul.getB());
        assertEquals(12.0, mul.getResult().getValue().doubleValue(), 0.0001);
        a.setValue(5);
        assertEquals(20.0, mul.getResult().getValue().doubleValue(), 0.0001);
    }

    // ── Divide ───────────────────────────────────────────────────────────────

    @Test
    public void divide_computesExpected_fromDefaultValues() {
        Divide div = new Divide();
        div.getA().setValue(0);
        div.getB().setValue(1);
        div.process();
        assertEquals(0.0, div.getResult().getValue().doubleValue(), 0.0001);
    }

    @Test
    public void divide_computesAfterSetValue() {
        Divide div = new Divide();
        div.getA().setValue(10);
        div.getB().setValue(4);
        div.process();
        assertEquals(2.5, div.getResult().getValue().doubleValue(), 0.0001);
    }

    @Test
    public void divide_byZero_returnsZero() {
        Divide div = new Divide();
        div.getA().setValue(10);
        div.getB().setValue(0);
        div.process();
        assertEquals(0.0, div.getResult().getValue().doubleValue(), 0.0001);
    }

    @Test
    public void divide_recomputesOnInputPush() {
        Num a = new Num(); a.setValue(10);
        Num b = new Num(); b.setValue(2);
        Divide div = new Divide();
        new com.adamk33n3r.nodegraph.Connection<>(a.getValue(), div.getA());
        new com.adamk33n3r.nodegraph.Connection<>(b.getValue(), div.getB());
        assertEquals(5.0, div.getResult().getValue().doubleValue(), 0.0001);
        a.setValue(20);
        assertEquals(10.0, div.getResult().getValue().doubleValue(), 0.0001);
    }

    // ── Min ──────────────────────────────────────────────────────────────────

    @Test
    public void min_computesExpected() {
        Min min = new Min();
        min.getA().setValue(3);
        min.getB().setValue(7);
        min.process();
        assertEquals(3.0, min.getResult().getValue().doubleValue(), 0.0001);
    }

    @Test
    public void min_recomputesOnInputPush() {
        Num a = new Num(); a.setValue(5);
        Num b = new Num(); b.setValue(2);
        Min min = new Min();
        new com.adamk33n3r.nodegraph.Connection<>(a.getValue(), min.getA());
        new com.adamk33n3r.nodegraph.Connection<>(b.getValue(), min.getB());
        assertEquals(2.0, min.getResult().getValue().doubleValue(), 0.0001);
        a.setValue(1);
        assertEquals(1.0, min.getResult().getValue().doubleValue(), 0.0001);
    }

    // ── Max ──────────────────────────────────────────────────────────────────

    @Test
    public void max_computesExpected() {
        Max max = new Max();
        max.getA().setValue(3);
        max.getB().setValue(7);
        max.process();
        assertEquals(7.0, max.getResult().getValue().doubleValue(), 0.0001);
    }

    @Test
    public void max_recomputesOnInputPush() {
        Num a = new Num(); a.setValue(5);
        Num b = new Num(); b.setValue(2);
        Max max = new Max();
        new com.adamk33n3r.nodegraph.Connection<>(a.getValue(), max.getA());
        new com.adamk33n3r.nodegraph.Connection<>(b.getValue(), max.getB());
        assertEquals(5.0, max.getResult().getValue().doubleValue(), 0.0001);
        b.setValue(9);
        assertEquals(9.0, max.getResult().getValue().doubleValue(), 0.0001);
    }

    // ── Clamp ─────────────────────────────────────────────────────────────────

    @Test
    public void clamp_valueInRange() {
        Clamp clamp = new Clamp();
        clamp.getValue().setValue(5);
        clamp.getMin().setValue(0);
        clamp.getMax().setValue(10);
        clamp.process();
        assertEquals(5.0, clamp.getResult().getValue().doubleValue(), 0.0001);
    }

    @Test
    public void clamp_valueBelowMin() {
        Clamp clamp = new Clamp();
        clamp.getValue().setValue(-5);
        clamp.getMin().setValue(0);
        clamp.getMax().setValue(10);
        clamp.process();
        assertEquals(0.0, clamp.getResult().getValue().doubleValue(), 0.0001);
    }

    @Test
    public void clamp_valueAboveMax() {
        Clamp clamp = new Clamp();
        clamp.getValue().setValue(15);
        clamp.getMin().setValue(0);
        clamp.getMax().setValue(10);
        clamp.process();
        assertEquals(10.0, clamp.getResult().getValue().doubleValue(), 0.0001);
    }

    @Test
    public void clamp_minGreaterThanMax_swaps() {
        Clamp clamp = new Clamp();
        clamp.getValue().setValue(5);
        clamp.getMin().setValue(10);
        clamp.getMax().setValue(0);
        clamp.process();
        // min and max swapped: effective min=0, max=10 → value=5 in range
        assertEquals(5.0, clamp.getResult().getValue().doubleValue(), 0.0001);
    }
}
