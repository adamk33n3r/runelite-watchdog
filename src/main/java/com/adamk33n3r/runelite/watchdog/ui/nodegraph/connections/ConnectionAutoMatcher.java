package com.adamk33n3r.runelite.watchdog.ui.nodegraph.connections;

import com.adamk33n3r.nodegraph.VarInput;
import com.adamk33n3r.nodegraph.VarOutput;

import java.util.Collection;

/**
 * Pure matching logic for auto-connecting a dragged output to the best available input on a newly created node.
 * <p>
 * Priority 1: same type AND name (case-insensitive) — e.g. output "Exec" connects to input "Exec".
 * Priority 2: first input with the same type — fallback when no name match exists.
 */
public class ConnectionAutoMatcher {

    @SuppressWarnings("unchecked")
    public static <T> VarInput<T> findBestMatchingInput(
        VarOutput<T> output,
        Collection<? extends VarInput<?>> candidates
    ) {
        Class<T> type = output.getType();
        String name = output.getName();

        // Priority 1: same type + name (case-insensitive)
        for (VarInput<?> input : candidates) {
            if (input.getType() == type && input.getName().equalsIgnoreCase(name)) {
                return (VarInput<T>) input;
            }
        }

        // Priority 2: first input with matching type
        for (VarInput<?> input : candidates) {
            if (input.getType() == type) {
                return (VarInput<T>) input;
            }
        }

        return null;
    }
}
