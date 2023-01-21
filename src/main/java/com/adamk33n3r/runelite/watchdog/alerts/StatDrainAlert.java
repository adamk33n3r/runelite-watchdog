package com.adamk33n3r.runelite.watchdog.alerts;

import net.runelite.api.Skill;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StatDrainAlert extends Alert {
    private Skill skill = Skill.ATTACK;
    private int drainAmount = 1;

    public StatDrainAlert() {
        super("New Stat Drain Alert");
    }

    public StatDrainAlert(String name) {
        super(name);
    }
}
