package com.adamk33n3r.runelite.afkwarden;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class AFKWardenPluginLauncher {
    public static void main(String[] args) throws Exception {
        ExternalPluginManager.loadBuiltin(AFKWardenPlugin.class);
        RuneLite.main(args);
    }
}