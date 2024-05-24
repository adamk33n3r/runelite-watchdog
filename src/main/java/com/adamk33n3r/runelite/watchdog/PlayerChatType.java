package com.adamk33n3r.runelite.watchdog;

import net.runelite.api.ChatMessageType;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public enum PlayerChatType implements Displayable {
    ANY("Any", "Any message", (ChatMessageType[]) null),
    PUBLIC("Public", "Public", ChatMessageType.PUBLICCHAT, ChatMessageType.MODCHAT),
    PRIVATE("Private", "Private message", ChatMessageType.PRIVATECHAT, ChatMessageType.PRIVATECHATOUT, ChatMessageType.MODPRIVATECHAT),
    FRIENDS("Friends", "Friends Chat", ChatMessageType.FRIENDSCHAT, ChatMessageType.FRIENDSCHATNOTIFICATION),
    CLAN("Clan", "Clan Chat", ChatMessageType.CLAN_CHAT, ChatMessageType.CLAN_MESSAGE),
    GUEST_CLAN("Guest Clan", "Guest Clan Chat", ChatMessageType.CLAN_GUEST_CHAT, ChatMessageType.CLAN_GUEST_MESSAGE),
    GIM("GIM", "Group Iron Man Chat", ChatMessageType.CLAN_GIM_CHAT, ChatMessageType.CLAN_GIM_MESSAGE, ChatMessageType.CLAN_GIM_FORM_GROUP, ChatMessageType.CLAN_GIM_GROUP_WITH),
    AUTOTYPER("Autochat", "Autochat messages", ChatMessageType.AUTOTYPER, ChatMessageType.MODAUTOTYPER),
    ;

    private final String name;
    private final String tooltip;
    private final ChatMessageType[] chatMessageTypes;
    private static final List<ChatMessageType> ANY_TYPES;

    static {
        ANY_TYPES = Arrays.stream(values())
            .filter(playerChatType -> playerChatType != ANY)
            .flatMap((playerChatType) -> Arrays.stream(playerChatType.chatMessageTypes))
            .collect(Collectors.toList());
    }

    PlayerChatType(String name, String tooltip, ChatMessageType... chatMessageTypes) {
        this.name = name;
        this.tooltip = tooltip;
        this.chatMessageTypes = chatMessageTypes;
    }

    public boolean isOfType(ChatMessageType chatMessageType) {
        return this.chatMessageTypes == null ? ANY_TYPES.contains(chatMessageType) : Arrays.asList(this.chatMessageTypes).contains(chatMessageType);
    }
}
