package com.adamk33n3r.runelite.watchdog.ui.alerts;

import com.adamk33n3r.runelite.watchdog.WatchdogPanel;
import com.adamk33n3r.runelite.watchdog.alerts.StatChangedAlert;
import com.adamk33n3r.runelite.watchdog.alerts.StatChangedMode;
import com.adamk33n3r.runelite.watchdog.ui.ComparableNumber;
import com.adamk33n3r.runelite.watchdog.ui.panels.AlertContentBuilder;
import com.adamk33n3r.runelite.watchdog.ui.panels.AlertPanel;
import com.adamk33n3r.runelite.watchdog.ui.panels.PanelUtils;

import net.runelite.api.Skill;

import javax.swing.JPanel;

public class StatChangedAlertPanel extends AlertPanel<StatChangedAlert> {
    public StatChangedAlertPanel(WatchdogPanel watchdogPanel, StatChangedAlert alert) {
        super(watchdogPanel, alert);
    }

    @Override
    protected void build() {
        this.addAlertDefaults();
        buildTypeContent(this.alert, new AlertContentBuilder(this.getControlContainer(), this.getSaveAction(), this::rebuild));
        this.addNotifications();
    }

    public static void buildTypeContent(StatChangedAlert alert, AlertContentBuilder builder) {
        builder
            .addSelect("Skill", "The skill to track.", Skill.class, alert.getSkill(), alert::setSkill)
            .addSelect("Changed Mode", "The mode to compare the skill to the amount with.", StatChangedMode.class, alert.getChangedMode(), val -> {
                alert.setChangedMode(val);
                builder.rebuild();
            })
            .addSubPanelControl(alert.getChangedMode() == StatChangedMode.RELATIVE ?
                createRelativeLevelPanel(alert) : alert.getChangedMode() == StatChangedMode.PERCENTAGE ?
                createPercentageLevelPanel(alert) :
                createAbsoluteLevelPanel(alert));
    }

    private static JPanel createRelativeLevelPanel(StatChangedAlert alert) {
        return PanelUtils.createLabeledComponent(
            "Changed Amount",
            "The difference in level to trigger the alert. Can be positive for boost and negative for drain.",
            new ComparableNumber(alert.getChangedAmount(), alert::setChangedAmount, -99, 99, 1, alert.getChangedComparator(), alert::setChangedComparator));
    }

    private static JPanel createAbsoluteLevelPanel(StatChangedAlert alert) {
        return PanelUtils.createLabeledComponent(
            "Level",
            "The level to trigger the alert.",
            new ComparableNumber(alert.getChangedAmount(), alert::setChangedAmount, 0, 99, 1, alert.getChangedComparator(), alert::setChangedComparator));
    }

    private static JPanel createPercentageLevelPanel(StatChangedAlert alert) {
        return PanelUtils.createLabeledComponent(
            "Percentage",
            "The percentage of your level (rounded down) to trigger the alert.",
            new ComparableNumber(alert.getChangedAmount(), alert::setChangedAmount, 0, 100, 1, alert.getChangedComparator(), alert::setChangedComparator));
    }
}
