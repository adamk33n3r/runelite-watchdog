package com.adamk33n3r.runelite.watchdog.alerts;

import com.adamk33n3r.runelite.watchdog.TriggerType;
import lombok.Getter;
import lombok.Setter;
import net.runelite.api.ChatMessageType;

@Getter
@Setter
public class ChatAlert extends Alert {
    private ChatMessageType chatMessageType = ChatMessageType.GAMEMESSAGE;
    private String message = "";

    public ChatAlert(String name) {
        super(name, TriggerType.CHAT);
    }
}
