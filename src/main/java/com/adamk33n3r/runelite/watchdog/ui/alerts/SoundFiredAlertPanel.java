package com.adamk33n3r.runelite.watchdog.ui.alerts;

import com.adamk33n3r.runelite.watchdog.WatchdogPanel;
import com.adamk33n3r.runelite.watchdog.WatchdogPlugin;
import com.adamk33n3r.runelite.watchdog.alerts.SoundFiredAlert;
import com.adamk33n3r.runelite.watchdog.ui.panels.AlertPanel;

import com.google.inject.Key;
import com.google.inject.name.Names;

public class SoundFiredAlertPanel extends AlertPanel<SoundFiredAlert> {
    public SoundFiredAlertPanel(WatchdogPanel watchdogPanel, SoundFiredAlert alert) {
        super(watchdogPanel, alert);
    }

    @Override
    protected void build() {
        String wikiPage = WatchdogPlugin.getInstance().getInjector().getInstance(Key.get(String.class, Names.named("watchdog.wikiPage.soundIDs")));
        this.addAlertDefaults()
            .addRichTextPane("<html>Go to <a href='" + wikiPage + "'>this wiki page</a> to get a list<br>of sound ids</html>")
            .addSpinner("Sound ID", "The ID of the sound", this.alert.getSoundID(), this.alert::setSoundID, 0, 99999, 1)
            .addNotifications();
    }
}
