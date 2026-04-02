package com.adamk33n3r.runelite.watchdog.ui.alerts;

import com.adamk33n3r.runelite.watchdog.WatchdogPanel;
import com.adamk33n3r.runelite.watchdog.alerts.OverheadTextAlert;
import com.adamk33n3r.runelite.watchdog.ui.panels.AlertContentBuilder;
import com.adamk33n3r.runelite.watchdog.ui.panels.AlertPanel;

public class OverheadTextAlertPanel extends AlertPanel<OverheadTextAlert> {
    public OverheadTextAlertPanel(WatchdogPanel watchdogPanel, OverheadTextAlert alert) {
        super(watchdogPanel, alert);
    }

    @Override
    protected void build() {
        this.addAlertDefaults();
        buildTypeContent(this.alert, new AlertContentBuilder(this.getControlContainer(), this.getSaveAction(), this::rebuild));
        this.addNotifications();
    }

    public static void buildTypeContent(OverheadTextAlert alert, AlertContentBuilder builder) {
        builder
            .addRegexMatcher(alert, "Enter the message to trigger on...", "The message to trigger on. Supports glob (*)", MessagePickerButton.createOverheadTextPickerButton((selected) -> {
                alert.setPattern(selected);
                builder.rebuild();
            }))
            .addRegexMatcher(alert::getNpcName, alert::setNpcName, alert::isNpcRegexEnabled, alert::setNpcRegexEnabled, "(Optional) NPC name to filter on...", "The name to trigger on. Supports glob (*)", null);
    }
}
