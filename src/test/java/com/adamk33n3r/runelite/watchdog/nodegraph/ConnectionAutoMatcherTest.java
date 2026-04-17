package com.adamk33n3r.runelite.watchdog.nodegraph;

import com.adamk33n3r.nodegraph.ExecSignal;
import com.adamk33n3r.nodegraph.VarInput;
import com.adamk33n3r.nodegraph.VarOutput;
import com.adamk33n3r.runelite.watchdog.ui.nodegraph.connections.ConnectionAutoMatcher;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

public class ConnectionAutoMatcherTest {

    // ── helpers ──────────────────────────────────────────────────────────────

    private static <T> VarOutput<T> out(String name, Class<T> type) {
        return new VarOutput<>(null, name, type, null);
    }

    private static <T> VarInput<T> in(String name, Class<T> type) {
        return new VarInput<>(null, name, type, null);
    }

    // ── tests ─────────────────────────────────────────────────────────────────

    // 1. Exact name + type match (ExecSignal)
    @Test
    public void test_exec_matches_by_name_and_type() {
        VarOutput<ExecSignal> output = out("Exec", ExecSignal.class);
        VarInput<ExecSignal> execIn = in("Exec", ExecSignal.class);
        VarInput<Boolean> enabledIn = in("Enabled", Boolean.class);

        VarInput<ExecSignal> result = ConnectionAutoMatcher.findBestMatchingInput(output,
            Arrays.asList(enabledIn, execIn));

        assertSame(execIn, result);
    }

    // 2. Name match wins (case-insensitive) — "Enabled" output → "Enabled" input
    @Test
    public void test_name_match_wins_case_insensitive() {
        VarOutput<Boolean> output = out("Enabled", Boolean.class);
        VarInput<Boolean> fireWhenFocused = in("Fire When Focused", Boolean.class);
        VarInput<Boolean> enabled = in("Enabled", Boolean.class);

        // Even though fireWhenFocused comes first, enabled wins on name
        VarInput<Boolean> result = ConnectionAutoMatcher.findBestMatchingInput(output,
            Arrays.asList(fireWhenFocused, enabled));

        assertSame(enabled, result);
    }

    // 3. Case-insensitive: output "EXEC" matches input "exec"
    @Test
    public void test_name_match_is_case_insensitive() {
        VarOutput<ExecSignal> output = out("EXEC", ExecSignal.class);
        VarInput<ExecSignal> execIn = in("exec", ExecSignal.class);

        VarInput<ExecSignal> result = ConnectionAutoMatcher.findBestMatchingInput(output,
            Collections.singletonList(execIn));

        assertSame(execIn, result);
    }

    // 4. No name match → falls back to first matching type
    @Test
    public void test_fallback_to_first_type_match() {
        VarOutput<Boolean> output = out("Enabled Out", Boolean.class);
        VarInput<Boolean> fireWhenFocused = in("Fire When Focused", Boolean.class);
        VarInput<Boolean> fireWhenAfk = in("Fire When AFK", Boolean.class);

        // Neither name matches "Enabled Out" — should return first Boolean input
        VarInput<Boolean> result = ConnectionAutoMatcher.findBestMatchingInput(output,
            Arrays.asList(fireWhenFocused, fireWhenAfk));

        assertSame(fireWhenFocused, result);
    }

    // 5. No compatible type → returns null
    @Test
    public void test_no_compatible_type_returns_null() {
        VarOutput<ExecSignal> output = out("Exec", ExecSignal.class);
        VarInput<Boolean> enabledIn = in("Enabled", Boolean.class);
        VarInput<Boolean> fireWhenFocused = in("Fire When Focused", Boolean.class);

        VarInput<ExecSignal> result = ConnectionAutoMatcher.findBestMatchingInput(output,
            Arrays.asList(enabledIn, fireWhenFocused));

        assertNull(result);
    }

    // 6. Empty candidate list → returns null
    @Test
    public void test_empty_candidates_returns_null() {
        VarOutput<Boolean> output = out("Enabled", Boolean.class);

        VarInput<Boolean> result = ConnectionAutoMatcher.findBestMatchingInput(output,
            Collections.emptyList());

        assertNull(result);
    }

    // 7. Name match candidate beats an earlier type-only candidate in the list
    @Test
    public void test_name_match_beats_earlier_type_only_candidate() {
        VarOutput<Boolean> output = out("Enabled", Boolean.class);
        VarInput<Boolean> fireWhenAfk = in("Fire When AFK", Boolean.class);   // earlier, type match only
        VarInput<Boolean> enabled = in("Enabled", Boolean.class);              // later, name+type match

        VarInput<Boolean> result = ConnectionAutoMatcher.findBestMatchingInput(output,
            Arrays.asList(fireWhenAfk, enabled));

        assertSame("Name match should win even if it appears later in the list", enabled, result);
    }

    // 8. Mixed types — only matching type considered in fallback
    @Test
    public void test_fallback_skips_wrong_type() {
        VarOutput<Number> output = out("Debounce", Number.class);
        VarInput<Boolean> boolIn = in("Enabled", Boolean.class);
        VarInput<ExecSignal> execIn = in("Exec", ExecSignal.class);
        VarInput<Number> numIn = in("Seconds", Number.class);

        VarInput<Number> result = ConnectionAutoMatcher.findBestMatchingInput(output,
            Arrays.asList(boolIn, execIn, numIn));

        assertSame(numIn, result);
    }

    // 9. Subtype: Integer output → Number input (isAssignableFrom)
    @SuppressWarnings("unchecked")
    @Test
    public void test_matches_on_subtype() {
        VarOutput<Integer> output = out("Score", Integer.class);
        VarInput<Number> numIn = in("Amount", Number.class);

        VarInput<?> result = ConnectionAutoMatcher.findBestMatchingInput(output,
            Collections.singletonList(numIn));

        assertSame(numIn, result);
    }

    // 10. Object input accepts any output type
    @SuppressWarnings("unchecked")
    @Test
    public void test_matches_on_object_input() {
        VarOutput<Number> output = out("Score", Number.class);
        VarInput<Object> objectIn = in("Value", Object.class);

        VarInput<?> result = ConnectionAutoMatcher.findBestMatchingInput(output,
            Collections.singletonList(objectIn));

        assertSame(objectIn, result);
    }
}
