package com.adamk33n3r.runelite.watchdog.notifications;

import com.adamk33n3r.runelite.watchdog.WatchdogPlugin;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.client.chat.ChatColorType;
import net.runelite.client.chat.ChatMessageBuilder;
import net.runelite.client.chat.ChatMessageManager;
import net.runelite.client.chat.QueuedMessage;

import javax.inject.Inject;

@Slf4j
@Data
//@AllArgsConstructor
public class GameMessage extends NotificationWithMessage {
    @Inject
    private transient ChatMessageManager chatMessageManager;

    public GameMessage() {
        this.message = "Hey! Wake up!";
    }

    @Override
    public void fire(WatchdogPlugin plugin) {
        final String formattedMessage = new ChatMessageBuilder()
            .append(ChatColorType.HIGHLIGHT)
            .append(this.message)
            .build();
        log.info(this.chatMessageManager == null ? "is null" : "is not null");
        this.chatMessageManager.queue(QueuedMessage.builder()
            .type(ChatMessageType.CONSOLE)
            .name(plugin.getName())
            .runeLiteFormattedMessage(formattedMessage)
            .build());
    }
}
