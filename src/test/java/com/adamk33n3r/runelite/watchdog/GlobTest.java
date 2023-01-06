package com.adamk33n3r.runelite.watchdog;

import org.junit.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class GlobTest {
    @Test
    public void star_becomes_dot_star() throws Exception {
        assertEquals("gl.*b", Util.createRegexFromGlob("gl*b"));
    }

    @Test
    public void escaped_star_is_unchanged() throws Exception {
        assertEquals("gl\\*b", Util.createRegexFromGlob("gl\\*b"));
    }

    @Test
    public void question_mark_becomes_dot() throws Exception {
        assertEquals("gl.b", Util.createRegexFromGlob("gl?b"));
    }

    @Test
    public void escaped_question_mark_is_unchanged() throws Exception {
        assertEquals("gl\\?b", Util.createRegexFromGlob("gl\\?b"));
    }

    @Test
    public void character_classes_dont_need_conversion() throws Exception {
        assertEquals("gl[-o]b", Util.createRegexFromGlob("gl[-o]b"));
    }

    @Test
    public void escaped_classes_are_unchanged() throws Exception {
        assertEquals("gl\\[-o\\]b", Util.createRegexFromGlob("gl\\[-o\\]b"));
    }

    @Test
    public void negation_in_character_classes() throws Exception {
        assertEquals("gl[^a-n!p-z]b", Util.createRegexFromGlob("gl[!a-n!p-z]b"));
    }

    @Test
    public void nested_negation_in_character_classes() throws Exception {
        assertEquals("gl[[^a-n]!p-z]b", Util.createRegexFromGlob("gl[[!a-n]!p-z]b"));
    }

    @Test
    public void escape_carat_if_it_is_the_first_char_in_a_character_class() throws Exception {
        assertEquals("gl[\\^o]b", Util.createRegexFromGlob("gl[^o]b"));
    }

    @Test
    public void metachars_are_escaped() throws Exception {
        assertEquals("gl..*\\.\\(\\)\\+\\|\\^\\$\\@\\%b", Util.createRegexFromGlob("gl?*.()+|^$@%b"));
    }

    @Test
    public void metachars_in_character_classes_dont_need_escaping() throws Exception {
        assertEquals("gl[?*.()+|^$@%]b", Util.createRegexFromGlob("gl[?*.()+|^$@%]b"));
    }

    @Test
    public void escaped_backslash_is_unchanged() throws Exception {
        assertEquals("gl\\\\b", Util.createRegexFromGlob("gl\\\\b"));
    }

    @Test
    public void slashQ_and_slashE_are_escaped() throws Exception {
        assertEquals("\\\\Qglob\\\\E", Util.createRegexFromGlob("\\Qglob\\E"));
    }

    @Test
    public void braces_are_turned_into_groups() throws Exception {
        assertEquals("(glob|regex)", Util.createRegexFromGlob("{glob,regex}"));
    }

    @Test
    public void escaped_braces_are_unchanged() throws Exception {
        assertEquals("\\{glob\\}", Util.createRegexFromGlob("\\{glob\\}"));
    }

    @Test
    public void commas_dont_need_escaping() throws Exception {
        assertEquals("(glob,regex),", Util.createRegexFromGlob("{glob\\,regex},"));
    }

    @Test
    public void test_real_example() throws Exception {
        String regex = Util.createRegexFromGlob("Your {*} {are,is} ready to harvest");
        assertEquals("Your (.*) (are|is) ready to harvest", regex);
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher("Your tomatoes are ready to harvest");
        assertTrue(matcher.matches());
        assertEquals(2, matcher.groupCount());
        assertEquals("tomatoes", matcher.group(1));
    }

    @Test
    public void test_character_classes() throws Exception {
        String regex = Util.createRegexFromGlob("Jan [4-9]th");
        assertEquals("Jan [4-9]th", regex);
        assertTrue("Jan 8th".matches(regex));
    }

    @Test
    public void test_valuable_drop() throws Exception {
        String regex = Util.createRegexFromGlob("*Valuable drop: *\\([5-9],??? coins\\)*");
        assertEquals(".*Valuable drop: .*\\([5-9],... coins\\).*", regex);
        assertTrue("Valuable drop: (5,001 coins)".matches(regex));
    }

    @Test
    public void test_valuable_drop_all() throws Exception {
        String regex = Util.createRegexFromGlob("*drop*");
        assertEquals(".*drop.*", regex);
        assertTrue("Valuable drop: (5,001 coins)".matches(regex));
    }
}
