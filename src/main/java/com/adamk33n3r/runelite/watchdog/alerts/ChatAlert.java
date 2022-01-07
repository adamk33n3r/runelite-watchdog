package com.adamk33n3r.runelite.watchdog.alerts;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatAlert extends Alert {
    private String message = "Nothing interesting happens.";

    public ChatAlert() {
        super("New Chat Alert");
    }

    public ChatAlert(String name) {
        super(name);
    }
}
