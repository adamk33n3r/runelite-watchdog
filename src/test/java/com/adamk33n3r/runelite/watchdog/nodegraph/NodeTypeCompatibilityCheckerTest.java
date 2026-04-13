package com.adamk33n3r.runelite.watchdog.nodegraph;

import com.adamk33n3r.nodegraph.ExecSignal;
import com.adamk33n3r.runelite.watchdog.NotificationType;
import com.adamk33n3r.runelite.watchdog.TestBase;
import com.adamk33n3r.runelite.watchdog.TriggerType;
import com.adamk33n3r.runelite.watchdog.ui.nodegraph.LogicNodeType;
import com.adamk33n3r.runelite.watchdog.ui.nodegraph.NodeTypeCompatibilityChecker;
import com.adamk33n3r.runelite.watchdog.ui.nodegraph.VariableNodeType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import javax.inject.Inject;

import static org.junit.Assert.*;

/**
 * Verifies the probe-based compatibility checker.
 * These tests implicitly validate that each node class registers the expected VarInputs
 * (e.g. NotificationNode registers an ExecSignal input, BooleanGate registers Boolean inputs).
 */
@RunWith(MockitoJUnitRunner.class)
public class NodeTypeCompatibilityCheckerTest extends TestBase {

    @Inject
    NodeTypeCompatibilityChecker checker;

    // ── NotificationType ──────────────────────────────────────────────────────

    @Test
    public void test_notification_accepts_exec() {
        assertTrue(checker.hasCompatibleInput(NotificationType.SCREEN_FLASH, ExecSignal.class));
    }

    @Test
    public void test_notification_accepts_boolean() {
        assertTrue(checker.hasCompatibleInput(NotificationType.GAME_MESSAGE, Boolean.class));
    }

    @Test
    public void test_notification_accepts_number() {
        assertTrue(checker.hasCompatibleInput(NotificationType.TEXT_TO_SPEECH, Number.class));
    }

    // ── LogicNodeType ─────────────────────────────────────────────────────────

    @Test
    public void test_boolean_gate_accepts_boolean() {
        assertTrue(checker.hasCompatibleInput(LogicNodeType.BOOLEAN, Boolean.class));
    }

    @Test
    public void test_boolean_gate_rejects_exec() {
        assertFalse(checker.hasCompatibleInput(LogicNodeType.BOOLEAN, ExecSignal.class));
    }

    @Test
    public void test_boolean_gate_rejects_number() {
        assertFalse(checker.hasCompatibleInput(LogicNodeType.BOOLEAN, Number.class));
    }

    @Test
    public void test_equality_accepts_number() {
        assertTrue(checker.hasCompatibleInput(LogicNodeType.EQUALITY, Number.class));
    }

    @Test
    public void test_equality_rejects_boolean() {
        assertFalse(checker.hasCompatibleInput(LogicNodeType.EQUALITY, Boolean.class));
    }

    @Test
    public void test_equality_rejects_exec() {
        assertFalse(checker.hasCompatibleInput(LogicNodeType.EQUALITY, ExecSignal.class));
    }

    // ── TriggerType ───────────────────────────────────────────────────────────

    @Test
    public void test_trigger_accepts_boolean() {
        // All TriggerNodes have an "Enabled" Boolean input
        assertTrue(checker.hasCompatibleInput(TriggerType.GAME_MESSAGE, Boolean.class));
        assertTrue(checker.hasCompatibleInput(TriggerType.STAT_CHANGED, Boolean.class));
        assertTrue(checker.hasCompatibleInput(TriggerType.SPAWNED_OBJECT, Boolean.class));
    }

    @Test
    public void test_trigger_rejects_exec() {
        assertFalse(checker.hasCompatibleInput(TriggerType.GAME_MESSAGE, ExecSignal.class));
    }

    @Test
    public void test_trigger_rejects_number() {
        // TriggerNode does not currently have a Number input (Debounce is commented out)
        assertFalse(checker.hasCompatibleInput(TriggerType.STAT_CHANGED, Number.class));
    }

    // ── Alert group / advanced alert ──────────────────────────────────────────
    // These types are now probed like any other TriggerType (wrapped in TriggerNode),
    // so they inherit the standard Boolean/Number inputs. Popup-level exclusion is
    // handled separately in NewNodePopup and is not the responsibility of this checker.

    @Test
    public void test_alert_group_accepts_boolean() {
        // ALERT_GROUP is now a TriggerNode probe — inherits "Enabled" Boolean input
        assertTrue(checker.hasCompatibleInput(TriggerType.ALERT_GROUP, Boolean.class));
    }

    @Test
    public void test_alert_group_rejects_exec() {
        assertFalse(checker.hasCompatibleInput(TriggerType.ALERT_GROUP, ExecSignal.class));
    }

    @Test
    public void test_advanced_alert_accepts_boolean() {
        assertTrue(checker.hasCompatibleInput(TriggerType.ADVANCED_ALERT, Boolean.class));
    }

    // ── VariableNodeType — source-only, no probe ──────────────────────────────

    @Test
    public void test_variable_type_excluded() {
        // VariableNodeType has no probe — source-only nodes; always returns false
        assertFalse(checker.hasCompatibleInput(VariableNodeType.BOOLEAN, Boolean.class));
        assertFalse(checker.hasCompatibleInput(VariableNodeType.NUMBER, Number.class));
    }
}
