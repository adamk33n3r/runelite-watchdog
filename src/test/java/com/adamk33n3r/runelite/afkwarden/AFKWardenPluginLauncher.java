package com.adamk33n3r.runelite.afkwarden;

import com.adamk33n3r.runelite.afkwarden.alerts.Alert;
import com.adamk33n3r.runelite.afkwarden.alerts.ChatAlert;
import com.adamk33n3r.runelite.afkwarden.alerts.ResourceAlert;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;
import net.runelite.http.api.RuneLiteAPI;
import net.runelite.http.api.ws.RuntimeTypeAdapterFactory;

import java.util.ArrayList;
import java.util.List;

public class AFKWardenPluginLauncher {
    public static void main(String[] args) throws Exception {
        ExternalPluginManager.loadBuiltin(AFKWardenPlugin.class);
        RuneLite.main(args);
    }
}