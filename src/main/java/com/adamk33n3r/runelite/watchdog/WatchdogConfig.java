package com.adamk33n3r.runelite.watchdog;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("watchdog")
public interface WatchdogConfig extends Config {
    String configGroupName = "watchdog";
    @ConfigItem(
        keyName = "alerts",
        name = "Alerts",
        description = "Serialized Alerts as a JSON string",
        hidden = true
    )
    default String alerts() { return "[]"; }
}
