package com.adamk33n3r.runelite.watchdog.nodegraph;

import com.adamk33n3r.nodegraph.ExecSignal;
import com.adamk33n3r.runelite.watchdog.NotificationType;
import com.adamk33n3r.runelite.watchdog.TriggerType;
import com.adamk33n3r.runelite.watchdog.ui.nodegraph.LogicNodeType;
import com.adamk33n3r.runelite.watchdog.ui.nodegraph.NodeTypeCompatibilityChecker;
import com.adamk33n3r.runelite.watchdog.ui.nodegraph.VariableNodeType;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Verifies the probe-based compatibility checker.
 * These tests implicitly validate that each node class registers the expected VarInputs
 * (e.g. NotificationNode registers an ExecSignal input, BooleanGate registers Boolean inputs).
 */
public class NodeTypeCompatibilityCheckerTest {

    // ── NotificationType ──────────────────────────────────────────────────────

    @Test
    public void test_notification_accepts_exec() {
        assertTrue(NodeTypeCompatibilityChecker.hasCompatibleInput(NotificationType.SCREEN_FLASH, ExecSignal.class));
    }

    @Test
    public void test_notification_accepts_boolean() {
        assertTrue(NodeTypeCompatibilityChecker.hasCompatibleInput(NotificationType.GAME_MESSAGE, Boolean.class));
    }

    @Test
    public void test_notification_accepts_number() {
        assertTrue(NodeTypeCompatibilityChecker.hasCompatibleInput(NotificationType.TEXT_TO_SPEECH, Number.class));
    }

    // ── LogicNodeType ─────────────────────────────────────────────────────────

    @Test
    public void test_boolean_gate_accepts_boolean() {
        assertTrue(NodeTypeCompatibilityChecker.hasCompatibleInput(LogicNodeType.BOOLEAN, Boolean.class));
    }

    @Test
    public void test_boolean_gate_rejects_exec() {
        assertFalse(NodeTypeCompatibilityChecker.hasCompatibleInput(LogicNodeType.BOOLEAN, ExecSignal.class));
    }

    @Test
    public void test_boolean_gate_rejects_number() {
        assertFalse(NodeTypeCompatibilityChecker.hasCompatibleInput(LogicNodeType.BOOLEAN, Number.class));
    }

    @Test
    public void test_equality_accepts_number() {
        assertTrue(NodeTypeCompatibilityChecker.hasCompatibleInput(LogicNodeType.EQUALITY, Number.class));
    }

    @Test
    public void test_equality_rejects_boolean() {
        assertFalse(NodeTypeCompatibilityChecker.hasCompatibleInput(LogicNodeType.EQUALITY, Boolean.class));
    }

    @Test
    public void test_equality_rejects_exec() {
        assertFalse(NodeTypeCompatibilityChecker.hasCompatibleInput(LogicNodeType.EQUALITY, ExecSignal.class));
    }

    // ── TriggerType ───────────────────────────────────────────────────────────

    @Test
    public void test_trigger_accepts_boolean() {
        // All TriggerNodes have an "Enabled" Boolean input
        assertTrue(NodeTypeCompatibilityChecker.hasCompatibleInput(TriggerType.GAME_MESSAGE, Boolean.class));
        assertTrue(NodeTypeCompatibilityChecker.hasCompatibleInput(TriggerType.STAT_CHANGED, Boolean.class));
        assertTrue(NodeTypeCompatibilityChecker.hasCompatibleInput(TriggerType.SPAWNED_OBJECT, Boolean.class));
    }

    @Test
    public void test_trigger_rejects_exec() {
        assertFalse(NodeTypeCompatibilityChecker.hasCompatibleInput(TriggerType.GAME_MESSAGE, ExecSignal.class));
    }

    // ── Excluded entries (null probe) ─────────────────────────────────────────

    @Test
    public void test_alert_group_excluded() {
        // ALERT_GROUP has no probe — always false
        assertFalse(NodeTypeCompatibilityChecker.hasCompatibleInput(TriggerType.ALERT_GROUP, Boolean.class));
    }

    @Test
    public void test_advanced_alert_excluded() {
        assertFalse(NodeTypeCompatibilityChecker.hasCompatibleInput(TriggerType.ADVANCED_ALERT, Boolean.class));
    }

    @Test
    public void test_variable_type_excluded() {
        // VariableNodeType has no probe — source-only nodes
        assertFalse(NodeTypeCompatibilityChecker.hasCompatibleInput(VariableNodeType.BOOLEAN, Boolean.class));
        assertFalse(NodeTypeCompatibilityChecker.hasCompatibleInput(VariableNodeType.NUMBER, Number.class));
    }
}
