package com.adamk33n3r.runelite.watchdog.alerts;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatAlert extends Alert {
    private String message = "";
    private boolean regexEnabled = false;

    public ChatAlert() {
        super("New Game Message Alert");
    }

    public ChatAlert(String name) {
        super(name);
    }

    @Override
    public String getDisplayName() {
        return "Game Message Alert";
    }
}
