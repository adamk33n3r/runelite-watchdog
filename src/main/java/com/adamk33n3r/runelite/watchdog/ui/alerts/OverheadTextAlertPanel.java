package com.adamk33n3r.runelite.watchdog.ui.alerts;

import com.adamk33n3r.runelite.watchdog.alerts.OverheadTextAlert;
import com.adamk33n3r.runelite.watchdog.ui.panels.AlertContentPanel;

public class OverheadTextAlertPanel extends AlertContentPanel<OverheadTextAlert> {

    public OverheadTextAlertPanel(OverheadTextAlert alert, Runnable onChange) {
        super(alert, onChange);
        this.init();
    }

    @Override
    public void buildTypeContent() {
        this.addRegexMatcher(this.alert, "Enter the message to trigger on...", "The message to trigger on. Supports glob (*)", MessagePickerButton.createOverheadTextPickerButton(selected -> {
                this.alert.setPattern(selected);
                this.rebuild();
            }))
            .addRegexMatcher(this.alert::getNpcName, this.alert::setNpcName, this.alert::isNpcRegexEnabled, this.alert::setNpcRegexEnabled, "(Optional) NPC name to filter on...", "The name to trigger on. Supports glob (*)", null);
    }
}
