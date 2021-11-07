package com.adamk33n3r.runelite.narration.tts;

import net.runelite.api.MenuEntry;

public class MenuEntrySegment extends MessageSegment {
    public MenuEntrySegment(MenuEntry menuEntry) {
        super(menuEntry.getOption() + " " + menuEntry.getTarget());
    }
}
