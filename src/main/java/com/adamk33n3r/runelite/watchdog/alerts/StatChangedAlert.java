package com.adamk33n3r.runelite.watchdog.alerts;

import net.runelite.api.Skill;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StatChangedAlert extends Alert {
    private Skill skill = Skill.ATTACK;
    private int changedAmount = 1;

    public StatChangedAlert() {
        super("New Stat Changed Alert");
    }

    public StatChangedAlert(String name) {
        super(name);
    }
}
