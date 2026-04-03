package com.adamk33n3r.runelite.watchdog.ui.alerts;

import com.adamk33n3r.runelite.watchdog.alerts.StatChangedAlert;
import com.adamk33n3r.runelite.watchdog.alerts.StatChangedMode;
import com.adamk33n3r.runelite.watchdog.ui.ComparableNumber;
import com.adamk33n3r.runelite.watchdog.ui.panels.AlertContentPanel;
import com.adamk33n3r.runelite.watchdog.ui.panels.PanelUtils;

import net.runelite.api.Skill;

import javax.swing.JPanel;

public class StatChangedAlertPanel extends AlertContentPanel<StatChangedAlert> {

    public StatChangedAlertPanel(StatChangedAlert alert, Runnable onChange) {
        super(alert, onChange);
        this.init();
    }

    @Override
    public void buildTypeContent() {
        this.addSelect("Skill", "The skill to track.", Skill.class, this.alert.getSkill(), this.alert::setSkill)
            .addSelect("Changed Mode", "The mode to compare the skill to the amount with.", StatChangedMode.class, this.alert.getChangedMode(), val -> {
                this.alert.setChangedMode(val);
                this.rebuild();
            })
            .addSubPanelControl(this.alert.getChangedMode() == StatChangedMode.RELATIVE ?
                createRelativeLevelPanel(this.alert) : this.alert.getChangedMode() == StatChangedMode.PERCENTAGE ?
                createPercentageLevelPanel(this.alert) :
                createAbsoluteLevelPanel(this.alert));
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
