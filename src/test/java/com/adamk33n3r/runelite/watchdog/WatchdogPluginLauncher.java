package com.adamk33n3r.runelite.watchdog;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class WatchdogPluginLauncher {
    public static void main(String[] args) throws Exception {
        ExternalPluginManager.loadBuiltin(WatchdogPlugin.class);
        RuneLite.main(args);
    }
}