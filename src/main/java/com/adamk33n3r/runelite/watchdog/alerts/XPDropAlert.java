package com.adamk33n3r.runelite.watchdog.alerts;

import com.adamk33n3r.runelite.watchdog.ui.ComparableNumber;
import net.runelite.api.Skill;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class XPDropAlert extends Alert {
    private Skill skill = Skill.ATTACK;
    private int gainedAmount = 1;
    private ComparableNumber.Comparator gainedComparator = ComparableNumber.Comparator.GREATER_THAN_OR_EQUALS;

    public XPDropAlert() {
        super("New XP Drop Alert");
    }

    public XPDropAlert(String name) {
        super(name);
    }
}
