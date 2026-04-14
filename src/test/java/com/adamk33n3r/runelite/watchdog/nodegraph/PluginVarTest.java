package com.adamk33n3r.runelite.watchdog.nodegraph;

import com.adamk33n3r.nodegraph.Connection;
import com.adamk33n3r.nodegraph.VarInput;
import com.adamk33n3r.nodegraph.nodes.constants.PluginVar;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.*;

public class PluginVarTest {

    @Test
    public void test_setValue_true_reflects_in_getValue() {
        PluginVar node = new PluginVar();
        node.setValue(true);
        assertTrue(node.getValueOut().getValue());
    }

    @Test
    public void test_setValue_false_reflects_in_getValue() {
        PluginVar node = new PluginVar();
        node.setValue(true);
        node.setValue(false);
        assertFalse(node.getValueOut().getValue());
    }

    @Test
    public void test_onChange_fires_via_connected_input() {
        PluginVar node = new PluginVar();
        VarInput<Boolean> watcher = new VarInput<>(null, "watcher", Boolean.class, false);
        new Connection<>(node.getValueOut(), watcher);

        AtomicBoolean lastSeen = new AtomicBoolean(false);
        AtomicInteger fireCount = new AtomicInteger(0);
        watcher.onChange(v -> {
            lastSeen.set(v);
            fireCount.incrementAndGet();
        });

        node.setValue(true);
        assertEquals(1, fireCount.get());
        assertTrue(lastSeen.get());

        node.setValue(false);
        assertEquals(2, fireCount.get());
        assertFalse(lastSeen.get());
    }

    @Test
    public void test_onChange_not_fired_after_connection_removed() {
        PluginVar node = new PluginVar();
        VarInput<Boolean> watcher = new VarInput<>(null, "watcher", Boolean.class, false);
        Connection<Boolean> conn = new Connection<>(node.getValueOut(), watcher);

        AtomicInteger fireCount = new AtomicInteger(0);
        watcher.onChange(v -> fireCount.incrementAndGet());

        node.setValue(true);
        assertEquals("should fire before removal", 1, fireCount.get());

        conn.remove();

        node.setValue(false);
        assertEquals("must NOT fire after connection removed", 1, fireCount.get());
    }
}
