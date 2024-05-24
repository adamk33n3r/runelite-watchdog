package com.adamk33n3r.runelite.watchdog.notifications;

import com.adamk33n3r.runelite.watchdog.Util;
import com.adamk33n3r.runelite.watchdog.WatchdogConfig;
import com.adamk33n3r.runelite.watchdog.WatchdogPlugin;

import net.runelite.api.ChatMessageType;
import net.runelite.client.chat.ChatColorType;
import net.runelite.client.chat.ChatMessageBuilder;
import net.runelite.client.chat.ChatMessageManager;
import net.runelite.client.chat.QueuedMessage;

import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;

@Slf4j
@NoArgsConstructor
@Accessors(chain = true)
public class GameMessage extends MessageNotification {
    @Inject
    private transient ChatMessageManager chatMessageManager;

    @Inject
    public GameMessage(WatchdogConfig config) {
        super(config);
    }

    @Override
    protected void fireImpl(String[] triggerValues) {
        final String formattedMessage = new ChatMessageBuilder()
            .append(ChatColorType.HIGHLIGHT)
            .append(Util.processTriggerValues(this.message, triggerValues))
            .build();
        this.chatMessageManager.queue(QueuedMessage.builder()
            .type(ChatMessageType.CONSOLE)
            .name(WatchdogPlugin.getInstance().getName())
            .runeLiteFormattedMessage(formattedMessage)
            .build());
    }
}
