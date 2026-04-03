package com.adamk33n3r.runelite.watchdog.ui.alerts;

import com.adamk33n3r.runelite.watchdog.WatchdogProperties;
import com.adamk33n3r.runelite.watchdog.alerts.SoundFiredAlert;
import com.adamk33n3r.runelite.watchdog.ui.panels.AlertContentPanel;

public class SoundFiredAlertPanel extends AlertContentPanel<SoundFiredAlert> {

    public SoundFiredAlertPanel(SoundFiredAlert alert, Runnable onChange) {
        super(alert, onChange);
        this.init();
    }

    @Override
    public void buildTypeContent() {
        this.addRichTextPane("<html>Go to <a href='" + WatchdogProperties.getProperties().getProperty("watchdog.wikiPage.soundIDs") + "'>this wiki page</a> to get a list<br>of sound ids</html>")
            .addSpinner("Sound ID", "The ID of the sound", this.alert.getSoundID(), this.alert::setSoundID);
    }
}
