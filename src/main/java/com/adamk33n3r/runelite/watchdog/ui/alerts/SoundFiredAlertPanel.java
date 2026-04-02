package com.adamk33n3r.runelite.watchdog.ui.alerts;

import com.adamk33n3r.runelite.watchdog.WatchdogPanel;
import com.adamk33n3r.runelite.watchdog.WatchdogProperties;
import com.adamk33n3r.runelite.watchdog.alerts.SoundFiredAlert;
import com.adamk33n3r.runelite.watchdog.ui.panels.AlertContentBuilder;
import com.adamk33n3r.runelite.watchdog.ui.panels.AlertPanel;

public class SoundFiredAlertPanel extends AlertPanel<SoundFiredAlert> {
    public SoundFiredAlertPanel(WatchdogPanel watchdogPanel, SoundFiredAlert alert) {
        super(watchdogPanel, alert);
    }

    @Override
    protected void build() {
        this.addAlertDefaults();
        buildTypeContent(this.alert, new AlertContentBuilder(this.getControlContainer(), this.getSaveAction(), this::rebuild));
        this.addNotifications();
    }

    public static void buildTypeContent(SoundFiredAlert alert, AlertContentBuilder builder) {
        builder
            .addRichTextPane("<html>Go to <a href='" + WatchdogProperties.getProperties().getProperty("watchdog.wikiPage.soundIDs") + "'>this wiki page</a> to get a list<br>of sound ids</html>")
            .addSpinner("Sound ID", "The ID of the sound", alert.getSoundID(), alert::setSoundID);
    }
}
