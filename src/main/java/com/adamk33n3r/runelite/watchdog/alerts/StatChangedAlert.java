package com.adamk33n3r.runelite.watchdog.alerts;

import com.adamk33n3r.runelite.watchdog.ui.ComparableNumber;
import lombok.Builder;
import lombok.experimental.SuperBuilder;
import net.runelite.api.Skill;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@SuperBuilder
public class StatChangedAlert extends Alert {
    @Builder.Default
    private Skill skill = Skill.HITPOINTS;
    @Builder.Default
    private int changedAmount = -5;
    @Builder.Default
    private ComparableNumber.Comparator changedComparator = ComparableNumber.Comparator.LESS_THAN_OR_EQUALS;

    public StatChangedAlert() {
        super("New Stat Changed Alert");
    }

    public StatChangedAlert(String name) {
        super(name);
    }
}
