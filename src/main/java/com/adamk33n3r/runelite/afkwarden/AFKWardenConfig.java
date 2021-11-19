package com.adamk33n3r.runelite.afkwarden;

import net.runelite.api.ChatMessageType;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("afk-warden")
public interface AFKWardenConfig extends Config {
    @ConfigItem(
        keyName = "alerts",
        name = "Alerts",
        description = "Serialized Alerts as a JSON string"
    )
    default String alerts() { return "[]"; };
}
