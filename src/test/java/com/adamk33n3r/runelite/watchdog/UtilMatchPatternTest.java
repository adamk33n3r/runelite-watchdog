package com.adamk33n3r.runelite.watchdog;

import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertNull;

public class UtilMatchPatternTest {

    @Test
    public void empty_glob_pattern_returns_null() {
        assertNull(Util.matchPattern(() -> "", () -> false, "anything at all"));
    }

    @Test
    public void empty_regex_pattern_returns_null() {
        assertNull(Util.matchPattern(() -> "", () -> true, "anything at all"));
    }

    @Test
    public void non_empty_glob_still_matches() {
        String[] groups = Util.matchPattern(() -> "hello {*}", () -> false, "hello world");
        assertArrayEquals(new String[]{"world"}, groups);
    }

    @Test
    public void non_empty_regex_still_matches() {
        String[] groups = Util.matchPattern(() -> "(\\d+)", () -> true, "got 42");
        assertArrayEquals(new String[]{"42"}, groups);
    }

    @Test
    public void glob_no_match_returns_null() {
        assertNull(Util.matchPattern(() -> "foo", () -> false, "bar"));
    }
}
