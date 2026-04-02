package com.adamk33n3r.runelite.watchdog.ui.alerts;

import com.adamk33n3r.runelite.watchdog.WatchdogPanel;
import com.adamk33n3r.runelite.watchdog.alerts.XPDropAlert;
import com.adamk33n3r.runelite.watchdog.ui.ComparableNumber;
import com.adamk33n3r.runelite.watchdog.ui.panels.AlertContentBuilder;
import com.adamk33n3r.runelite.watchdog.ui.panels.AlertPanel;
import com.adamk33n3r.runelite.watchdog.ui.panels.PanelUtils;

import net.runelite.api.Skill;

public class XPDropAlertPanel extends AlertPanel<XPDropAlert> {
    public XPDropAlertPanel(WatchdogPanel watchdogPanel, XPDropAlert alert) {
        super(watchdogPanel, alert);
    }

    @Override
    protected void build() {
        this.addAlertDefaults();
        buildTypeContent(this.alert, new AlertContentBuilder(this.getControlContainer(), this.getSaveAction(), this::rebuild));
        this.addNotifications();
    }

    public static void buildTypeContent(XPDropAlert alert, AlertContentBuilder builder) {
        builder
            .addSelect("Skill", "The skill to track", Skill.class, alert.getSkill(), alert::setSkill)
            .addSubPanelControl(PanelUtils.createLabeledComponent(
                "Gained Amount",
                "How much xp needed to trigger this alert",
                new ComparableNumber(alert.getGainedAmount(), alert::setGainedAmount, 0, Integer.MAX_VALUE, 1, alert.getGainedComparator(), alert::setGainedComparator)));
    }
}
