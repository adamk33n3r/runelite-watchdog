package com.adamk33n3r.runelite.watchdog.nodegraph;

import com.adamk33n3r.nodegraph.nodes.utility.ToStringNode;
import org.junit.Test;

import static org.junit.Assert.*;

public class ToStringNodeTest {

    @Test
    public void default_result_is_empty_string() {
        ToStringNode node = new ToStringNode();
        node.process();
        assertEquals("", node.getResult().getValue());
    }

    @Test
    public void converts_integer_to_string() {
        ToStringNode node = new ToStringNode();
        node.getValue().setValue(42);
        node.process();
        assertEquals("42", node.getResult().getValue());
    }

    @Test
    public void converts_double_to_string() {
        ToStringNode node = new ToStringNode();
        node.getValue().setValue(3.14);
        node.process();
        assertEquals("3.14", node.getResult().getValue());
    }

    @Test
    public void null_value_produces_null_string() {
        ToStringNode node = new ToStringNode();
        node.getValue().setValue(null);
        node.process();
        assertEquals("null", node.getResult().getValue());
    }

    @Test
    public void recomputes_reactively_on_value_change() {
        ToStringNode node = new ToStringNode();
        node.getValue().setValue(7);
        assertEquals("7", node.getResult().getValue());

        node.getValue().setValue(99);
        assertEquals("99", node.getResult().getValue());
    }
}
