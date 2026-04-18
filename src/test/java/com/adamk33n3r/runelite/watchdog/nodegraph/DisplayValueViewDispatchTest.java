package com.adamk33n3r.runelite.watchdog.nodegraph;

import com.adamk33n3r.nodegraph.ExecSignal;
import com.adamk33n3r.runelite.watchdog.InventoryItemData;
import com.adamk33n3r.runelite.watchdog.ui.nodegraph.displayview.DisplayValueView;
import com.adamk33n3r.runelite.watchdog.ui.nodegraph.displayview.InventoryGridView;

import net.runelite.api.coords.WorldPoint;

import org.junit.Test;

import javax.swing.*;

import static org.junit.Assert.*;

/**
 * Tests the type-dispatch logic in {@link DisplayValueView#buildView(Object)} without
 * exercising any live Swing layout — just checks what component type is produced.
 */
public class DisplayValueViewDispatchTest {

    private final DisplayValueView view = new DisplayValueView(null /* no ItemManager in tests */);

    @Test
    public void null_produces_italic_label() {
        JComponent c = this.view.buildView(null);
        assertInstanceOf(JLabel.class, c);
    }

    @Test
    public void boolean_true_produces_label_with_checkmark() {
        JComponent c = this.view.buildView(Boolean.TRUE);
        assertTrue(c instanceof JLabel);
        assertEquals("\u2714", ((JLabel) c).getText());
    }

    @Test
    public void boolean_false_produces_label_with_cross() {
        JComponent c = this.view.buildView(Boolean.FALSE);
        assertTrue(c instanceof JLabel);
        assertEquals("\u2716", ((JLabel) c).getText());
    }

    @Test
    public void integer_produces_label() {
        JComponent c = this.view.buildView(42);
        assertInstanceOf(JLabel.class, c);
        assertEquals("42", ((JLabel) c).getText());
    }

    @Test
    public void large_integer_is_formatted_with_separators() {
        JComponent c = this.view.buildView(1_000_000);
        assertInstanceOf(JLabel.class, c);
        // The label text should contain a thousands separator
        assertTrue(((JLabel) c).getText().contains(",") || ((JLabel) c).getText().contains("."));
    }

    @Test
    public void string_produces_scroll_pane() {
        JComponent c = this.view.buildView("hello");
        assertInstanceOf(JScrollPane.class, c);
    }

    @Test
    public void world_point_produces_label() {
        JComponent c = this.view.buildView(new WorldPoint(3200, 3400, 0));
        assertInstanceOf(JLabel.class, c);
        assertTrue(((JLabel) c).getText().contains("3200"));
    }

    @Test
    public void exec_signal_produces_label() {
        JComponent c = this.view.buildView(new ExecSignal(new String[]{"group1"}));
        assertInstanceOf(JLabel.class, c);
    }

    @Test
    public void object_array_produces_panel() {
        JComponent c = this.view.buildView(new String[]{"a", "b"});
        assertInstanceOf(JPanel.class, c);
    }

    @Test
    public void inventory_map_produces_inventory_grid_view() {
        var map = new InventoryItemData.InventoryItemDataMap(0);
        JComponent c = this.view.buildView(map);
        assertInstanceOf(InventoryGridView.class, c);
    }

    @Test
    public void unknown_type_produces_label_with_tostring() {
        JComponent c = this.view.buildView(new Object() {
            @Override public String toString() { return "custom-type"; }
        });
        assertInstanceOf(JLabel.class, c);
        assertEquals("custom-type", ((JLabel) c).getText());
    }

    // ── InventoryGridView.abbreviate ──────────────────────────────────────────

    @Test
    public void abbreviate_short_name_unchanged() {
        assertEquals("Ok", InventoryGridView.abbreviate("Ok"));
    }

    @Test
    public void abbreviate_long_name_truncated_to_4() {
        assertEquals("Shar", InventoryGridView.abbreviate("Shark"));
    }

    @Test
    public void abbreviate_null_returns_question_mark() {
        assertEquals("?", InventoryGridView.abbreviate(null));
    }

    @Test
    public void abbreviate_empty_returns_question_mark() {
        assertEquals("?", InventoryGridView.abbreviate(""));
    }

    private static void assertInstanceOf(Class<?> expected, Object actual) {
        assertTrue("Expected " + expected.getSimpleName() + " but got "
            + (actual == null ? "null" : actual.getClass().getSimpleName()),
            expected.isInstance(actual));
    }
}
