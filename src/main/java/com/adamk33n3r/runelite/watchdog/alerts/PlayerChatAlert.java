package com.adamk33n3r.runelite.watchdog.alerts;

import com.adamk33n3r.runelite.watchdog.Displayable;
import com.adamk33n3r.runelite.watchdog.PlayerChatType;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class PlayerChatAlert extends Alert implements RegexMatcher {
    @Accessors(chain = false)
    private boolean regexEnabled = false;
    private String message = "";
    private PlayerChatType playerChatType = PlayerChatType.ANY;
    private boolean prependSender = false;
    private ChatDirection chatDirection = ChatDirection.BOTH;

    @Override
    public String getPattern() {
        return this.message;
    }

    @Override
    public void setPattern(String pattern) {
        this.message = pattern;
    }

    public PlayerChatAlert() {
        super("New Player Chat Message Alert");
    }

    public PlayerChatAlert(String name) {
        super(name);
    }

    @Getter
    @AllArgsConstructor
    public enum ChatDirection implements Displayable {
        BOTH("Both", "Both directions"),
        SENT_ONLY("Sent Only", "Only trigger on sent messages"),
        RECEIVED_ONLY("Received Only", "Only trigger on received messages"),
        ;

        private final String name;
        private final String tooltip;
    }
}
