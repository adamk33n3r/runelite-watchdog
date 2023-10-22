package com.adamk33n3r.runelite.watchdog;

import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class SearchTextTest {
    @Test
    public void test_simple_search() {
        List<String> keywords = Arrays.asList("me", "you", "adamk33n3r");
        Assert.assertTrue(Util.searchText("k33n3r", keywords));
        Assert.assertTrue(Util.searchText("Adam", keywords));
        Assert.assertFalse(Util.searchText("frank", keywords));
    }

    @Test
    public void test_accented_characters() {
        List<String> keywords = Arrays.asList("adamk33n3r", "Ðomé");
        Assert.assertTrue(Util.searchText("Ðome", keywords));
    }
}
