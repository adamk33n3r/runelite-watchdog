package com.adamk33n3r.runelite.afkwarden.alerts;

import lombok.Getter;
import lombok.Setter;
import net.runelite.api.ChatMessageType;


@Getter
@Setter
public class ChatAlert extends Alert {
    private ChatMessageType chatMessageType;
    private String message;

    public ChatAlert(String name) {
        super(name);
    }
}
