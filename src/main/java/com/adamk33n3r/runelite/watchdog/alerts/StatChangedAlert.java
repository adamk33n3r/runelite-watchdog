package com.adamk33n3r.runelite.watchdog.alerts;

import com.adamk33n3r.runelite.watchdog.ui.ComparableNumber;
import net.runelite.api.Skill;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StatChangedAlert extends Alert {
    private Skill skill = Skill.HITPOINTS;
    private int changedAmount = -5;
    private ComparableNumber.Comparator changedComparator = ComparableNumber.Comparator.LESS_THAN_OR_EQUALS;
    private StatChangedMode changedMode = StatChangedMode.RELATIVE;

    public StatChangedAlert() {
        super("New Stat Changed Alert");
    }

    public StatChangedAlert(String name) {
        super(name);
    }

    public boolean isRelative() {
        return this.changedMode == StatChangedMode.RELATIVE;
    }

    public boolean isAbsolute() {
        return this.changedMode == StatChangedMode.ABSOLUTE;
    }
}
