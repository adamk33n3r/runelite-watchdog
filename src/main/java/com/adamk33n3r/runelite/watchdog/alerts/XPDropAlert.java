package com.adamk33n3r.runelite.watchdog.alerts;

import net.runelite.api.Skill;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class XPDropAlert extends Alert {
    private Skill skill = Skill.ATTACK;
    private int gainedAmount = 1;

    public XPDropAlert() {
        super("New XP Drop Alert");
    }

    public XPDropAlert(String name) {
        super(name);
    }
}
