package com.adamk33n3r.runelite.watchdog;

import jaco.mp3.player.MP3Player;

/**
 * Overriding base class so that it doesn't try to call jswing stuff
 */
public class MP3PlayerExt extends MP3Player {
    public MP3PlayerExt() {
        // Do nothing
    }
}
