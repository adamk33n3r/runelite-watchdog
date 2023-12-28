package com.adamk33n3r.runelite.watchdog.alerts;

import com.adamk33n3r.runelite.watchdog.PlayerChatType;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlayerChatAlert extends ChatAlert {
    private PlayerChatType playerChatType = PlayerChatType.ANY;

    public PlayerChatAlert() {
        super("New Player Chat Message Alert");
    }

    public PlayerChatAlert(String name) {
        super(name);
    }
}
