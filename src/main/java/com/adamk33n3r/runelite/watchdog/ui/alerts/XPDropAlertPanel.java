package com.adamk33n3r.runelite.watchdog.ui.alerts;

import com.adamk33n3r.runelite.watchdog.WatchdogPanel;
import com.adamk33n3r.runelite.watchdog.alerts.XPDropAlert;
import com.adamk33n3r.runelite.watchdog.ui.ComparableNumber;
import com.adamk33n3r.runelite.watchdog.ui.panels.AlertPanel;
import com.adamk33n3r.runelite.watchdog.ui.panels.PanelUtils;

import net.runelite.api.Skill;

public class XPDropAlertPanel extends AlertPanel<XPDropAlert> {
    public XPDropAlertPanel(WatchdogPanel watchdogPanel, XPDropAlert alert) {
        super(watchdogPanel, alert);
    }

    @Override
    protected void build() {
        this.addAlertDefaults()
            .addSelect("Skill", "The skill to track", Skill.class, this.alert.getSkill(), this.alert::setSkill)
            .addSubPanelControl(PanelUtils.createLabeledComponent(
                "Gained Amount",
                "How much xp needed to trigger this alert",
                new ComparableNumber(this.alert.getGainedAmount(), this.alert::setGainedAmount, 0, Integer.MAX_VALUE, 1, this.alert.getGainedComparator(), this.alert::setGainedComparator)))
            .addNotifications();
    }
}
