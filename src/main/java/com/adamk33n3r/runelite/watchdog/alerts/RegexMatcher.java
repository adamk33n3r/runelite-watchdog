package com.adamk33n3r.runelite.watchdog.alerts;

public interface RegexMatcher {
    boolean isRegexEnabled();
    Alert setRegexEnabled(boolean regexEnabled);
    String getPattern();
    void setPattern(String pattern);
}
