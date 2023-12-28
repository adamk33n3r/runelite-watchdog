package com.adamk33n3r.runelite.watchdog;

import net.runelite.api.ChatMessageType;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum PlayerChatType implements Displayable {
    ANY("Any", "Any message", (ChatMessageType[]) null),
    PUBLIC("Public", "Public", ChatMessageType.PUBLICCHAT, ChatMessageType.AUTOTYPER, ChatMessageType.MODCHAT, ChatMessageType.MODAUTOTYPER),
    PRIVATE("Private", "Private message", ChatMessageType.PRIVATECHAT, ChatMessageType.PRIVATECHATOUT, ChatMessageType.MODPRIVATECHAT),
    FRIENDS("Friends", "Friends Chat", ChatMessageType.FRIENDSCHAT),
    CLAN("Clan", "Clan Chat", ChatMessageType.CLAN_CHAT),
    GUEST_CLAN("Guest Clan", "Guest Clan Chat", ChatMessageType.CLAN_GUEST_CHAT),
    GIM("GIM", "Group Iron Man Chat", ChatMessageType.CLAN_GIM_CHAT),
    ;

    private final String name;
    private final String tooltip;
    private final ChatMessageType[] chatMessageTypes;

    PlayerChatType(String name, String tooltip, ChatMessageType... chatMessageTypes) {
        this.name = name;
        this.tooltip = tooltip;
        this.chatMessageTypes = chatMessageTypes;
    }

    boolean isOfType(ChatMessageType chatMessageType) {
        return this.chatMessageTypes != null && Arrays.asList(this.chatMessageTypes).contains(chatMessageType);
    }
}
