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
public class XPDropAlert extends Alert {
    @Builder.Default
    private Skill skill = Skill.ATTACK;
    @Builder.Default
    private int gainedAmount = 1;
    @Builder.Default
    private ComparableNumber.Comparator gainedComparator = ComparableNumber.Comparator.GREATER_THAN_OR_EQUALS;

    public XPDropAlert() {
        super("New XP Drop Alert");
    }

    public XPDropAlert(String name) {
        super(name);
    }
}
