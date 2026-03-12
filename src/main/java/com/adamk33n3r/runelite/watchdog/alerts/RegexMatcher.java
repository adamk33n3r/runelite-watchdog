package com.adamk33n3r.runelite.watchdog.alerts;

public interface RegexMatcher {
    boolean isRegexEnabled();
    void setRegexEnabled(boolean regexEnabled);
    String getPattern();
    void setPattern(String pattern);
}
