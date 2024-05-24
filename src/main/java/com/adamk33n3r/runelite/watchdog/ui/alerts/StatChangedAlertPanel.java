package com.adamk33n3r.runelite.watchdog.ui.alerts;

import com.adamk33n3r.runelite.watchdog.WatchdogPanel;
import com.adamk33n3r.runelite.watchdog.alerts.StatChangedAlert;
import com.adamk33n3r.runelite.watchdog.ui.ComparableNumber;
import com.adamk33n3r.runelite.watchdog.ui.panels.AlertPanel;
import com.adamk33n3r.runelite.watchdog.ui.panels.PanelUtils;

import net.runelite.api.Skill;

public class StatChangedAlertPanel extends AlertPanel<StatChangedAlert> {
    public StatChangedAlertPanel(WatchdogPanel watchdogPanel, StatChangedAlert alert) {
        super(watchdogPanel, alert);
    }

    @Override
    protected void build() {
        this.addAlertDefaults()
            .addSelect("Skill", "The skill to track", Skill.class, this.alert.getSkill(), this.alert::setSkill)
            .addSubPanelControl(PanelUtils.createLabeledComponent(
                "Changed Amount",
                "The difference in level to trigger the alert. Can be positive for boost and negative for drain",
                new ComparableNumber(this.alert.getChangedAmount(), this.alert::setChangedAmount, -99, 99, 1, this.alert.getChangedComparator(), this.alert::setChangedComparator)))
            .addNotifications();
    }
}
