package com.adamk33n3r.runelite.watchdog.alerts;

import com.adamk33n3r.runelite.watchdog.TriggerType;
import lombok.Getter;
import lombok.Setter;
import net.runelite.api.Skill;

@Getter
@Setter
public class StatDrainAlert extends Alert {
    private Skill skill = Skill.ATTACK;
    private int drainAmount = 1;

    public StatDrainAlert(String name) {
        super(name, TriggerType.STAT_DRAIN);
    }
}
