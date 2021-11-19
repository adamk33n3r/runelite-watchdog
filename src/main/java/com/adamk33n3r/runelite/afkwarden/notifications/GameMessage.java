package com.adamk33n3r.runelite.afkwarden.notifications;

import com.adamk33n3r.runelite.afkwarden.AFKWardenPlugin;
import lombok.AllArgsConstructor;
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
public class GameMessage implements INotification {
    @Inject
    private ChatMessageManager chatMessageManager;

    public String message;

    @Override
    public void fire(AFKWardenPlugin plugin) {
        log.info("Fire GameMessage");
        final String formattedMessage = new ChatMessageBuilder()
            .append(ChatColorType.HIGHLIGHT)
            .append(this.message)
            .build();
//        plugin.getChatMessageManager().queue(QueuedMessage.builder()
        this.chatMessageManager.queue(QueuedMessage.builder()
            .type(ChatMessageType.CONSOLE)
            .name("afk-warden")
            .runeLiteFormattedMessage(formattedMessage)
            .build());
    }
}
