package com.adamk33n3r.runelite.watchdog.notifications.tts;

import com.adamk33n3r.runelite.watchdog.Displayable;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TTSSource implements Displayable {
    LEGACY("Legacy", "Legacy"),
    ELEVEN_LABS("Eleven Labs", "Eleven Labs (Supply your API Key in Config)"),
    ;
    private final String name;
    private final String tooltip;
}
