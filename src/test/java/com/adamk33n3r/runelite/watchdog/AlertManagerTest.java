package com.adamk33n3r.runelite.watchdog;

import com.adamk33n3r.runelite.watchdog.alerts.Alert;
import com.adamk33n3r.runelite.watchdog.alerts.ChatAlert;
import com.adamk33n3r.runelite.watchdog.notifications.Notification;
import com.adamk33n3r.runelite.watchdog.notifications.Overlay;

import net.runelite.api.Client;
import net.runelite.client.account.SessionManager;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.ui.ClientUI;
import net.runelite.client.ui.MultiplexingPluginPanel;
import net.runelite.http.api.RuneLiteAPI;

import com.google.gson.Gson;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.testing.fieldbinder.Bind;
import com.google.inject.testing.fieldbinder.BoundFieldModule;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.internal.util.reflection.FieldSetter;
import org.mockito.junit.MockitoJUnitRunner;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import java.util.concurrent.ScheduledExecutorService;

@RunWith(MockitoJUnitRunner.class)
public class AlertManagerTest {
    @Mock
    @Bind
    EventBus eventBus;

    @Mock
    @Bind
    ScheduledExecutorService executor;
    @Mock
    @Bind
    Client client;
    @Mock
    @Bind
    ClientUI clientUI;
    @Mock
    @Bind
    SessionManager sessionManager;
    @Mock
    @Bind
    ConfigManager configManager;
    @Bind
    Gson clientGson = RuneLiteAPI.GSON;
    @Bind
    @Named("watchdog.pluginVersion")
    private final String pluginVersion = "3.0.0-TEST";

    @Mock
    @Bind
    WatchdogConfig watchdogConfig;

    @Mock
    @Bind
    WatchdogPanel watchdogPanel;

    @Bind
    @Named("watchdog.helpURL")
    private final String HELP_URL = "";

    @Bind
    @Named("watchdog.discordURL")
    private final String DISCORD_URL = "";

    @Bind
    @Named("watchdog.kofiURL")
    private final String KOFI_URL = "";

    @Bind
    @Named("watchdog.pluginVersionFull")
    private final String PLUGIN_VERSION_FULL = "";

    @Bind
    @Named("VERSION_PHASE")
    private final String PLUGIN_VERSION_PHASE = "";

    @Inject
    AlertManager alertManager;

    @Bind
    Provider<MultiplexingPluginPanel> multiplexingPluginPanelProvider = () -> alertManager.getWatchdogPanel().getMuxer();

    @Before
    public void before() throws NoSuchFieldException {
        BoundFieldModule module = BoundFieldModule.of(this);
        Injector injector = Guice.createInjector(module);
        injector.injectMembers(this);

        WatchdogPlugin watchdogPlugin = new WatchdogPlugin();
        FieldSetter.setField(watchdogPlugin, Plugin.class.getDeclaredField("injector"), injector);
    }

    @Test
    public void test_import() {
        String json = "[{\"type\":\"ChatAlert\",\"message\":\"*is ready to harvest*\",\"regexEnabled\":false,\"enabled\":true,\"name\":\"Ready to Harvest\",\"debounceTime\":500,\"notifications\":[{\"type\":\"TrayNotification\",\"message\":\"Time to harvest your crops!\",\"fireWhenFocused\":true},{\"type\":\"Sound\",\"path\":\"C:\\\\Users\\\\adamg\\\\Music\\\\airplane_seatbelt.mp3\",\"gain\":10,\"fireWhenFocused\":true},{\"type\":\"TextToSpeech\",\"gain\":5,\"rate\":1,\"voice\":\"GEORGE\",\"source\":\"LEGACY\",\"message\":\"\",\"fireWhenFocused\":true},{\"type\":\"Overhead\",\"displayTime\":3,\"message\":\"\",\"fireWhenFocused\":true}]},{\"type\":\"NotificationFiredAlert\",\"message\":\"You are now out of combat!\",\"regexEnabled\":false,\"enabled\":true,\"name\":\"Out of Combat\",\"debounceTime\":0,\"notifications\":[{\"type\":\"ScreenFlash\",\"color\":\"#46FF0000\",\"flashMode\":\"FLASH\",\"flashDuration\":0,\"fireWhenFocused\":true}]},{\"type\":\"SpawnedAlert\",\"spawnedDespawned\":\"SPAWNED\",\"spawnedType\":\"ITEM\",\"spawnedName\":\"Bones\",\"regexEnabled\":false,\"enabled\":false,\"name\":\"Bones Spawned\",\"debounceTime\":0,\"notifications\":[{\"type\":\"SoundEffect\",\"soundID\":3925,\"gain\":10,\"fireWhenFocused\":true},{\"type\":\"ScreenFlash\",\"color\":\"#46FF0000\",\"flashMode\":\"FLASH\",\"flashDuration\":0,\"fireWhenFocused\":true}]},{\"type\":\"SpawnedAlert\",\"spawnedDespawned\":\"SPAWNED\",\"spawnedType\":\"NPC\",\"spawnedName\":\"Gee\",\"regexEnabled\":false,\"enabled\":false,\"name\":\"NPC Spawn\",\"debounceTime\":0,\"notifications\":[{\"type\":\"Overhead\",\"displayTime\":3,\"message\":\"The dude is here\",\"fireWhenFocused\":true},{\"type\":\"TextToSpeech\",\"gain\":10,\"rate\":1,\"voice\":\"GEORGE\",\"source\":\"LEGACY\",\"message\":\"\",\"fireWhenFocused\":true}]},{\"type\":\"SpawnedAlert\",\"spawnedDespawned\":\"SPAWNED\",\"spawnedType\":\"GAME_OBJECT\",\"spawnedName\":\"Tree\",\"regexEnabled\":false,\"enabled\":false,\"name\":\"New Spawned Alert\",\"debounceTime\":0,\"notifications\":[{\"type\":\"Overhead\",\"displayTime\":3,\"message\":\"CHOP THE TREE\",\"fireWhenFocused\":true},{\"type\":\"SoundEffect\",\"soundID\":3924,\"gain\":10,\"fireWhenFocused\":true}]},{\"type\":\"ChatAlert\",\"message\":\"\",\"regexEnabled\":false,\"enabled\":true,\"name\":\"test 11labs lag\",\"debounceTime\":0,\"notifications\":[{\"type\":\"TextToSpeech\",\"gain\":10,\"rate\":1,\"voice\":\"GEORGE\",\"source\":\"LEGACY\",\"message\":\"this is a test\",\"fireWhenFocused\":true}]}]";
        Mockito.when(this.configManager.getConfiguration(WatchdogConfig.CONFIG_GROUP_NAME, WatchdogConfig.ALERTS))
            .thenReturn(json);
        Mockito.when(this.configManager.getConfiguration(WatchdogConfig.CONFIG_GROUP_NAME, WatchdogConfig.PLUGIN_VERSION))
            .thenReturn(this.pluginVersion);
        Assert.assertEquals(alertManager.getAlerts().size(), 0);
        alertManager.loadAlerts();
        Assert.assertEquals(alertManager.getAlerts().size(), 6);
    }

    @Test
    public void test_upgrade() {
        String json = "[{\"type\":\"ChatAlert\",\"message\":\"\",\"regexEnabled\":false,\"enabled\":true,\"name\":\"Upgrade Test\",\"debounceTime\":0,\"notifications\":[{\"type\":\"Overlay\",\"color\":\"#46FF0000\",\"sticky\":false,\"timeToLive\":5,\"imagePath\":\"\",\"message\":\"Overlay notification\",\"fireWhenFocused\":true}]}]";
        Mockito.when(this.configManager.getConfiguration(WatchdogConfig.CONFIG_GROUP_NAME, WatchdogConfig.ALERTS))
            .thenReturn(json);
        Mockito.when(this.configManager.getConfiguration(WatchdogConfig.CONFIG_GROUP_NAME, WatchdogConfig.PLUGIN_VERSION))
            .thenReturn("2.12.0");
        alertManager.loadAlerts();
        Alert alert = alertManager.getAlerts().get(0);
        Assert.assertTrue(alert instanceof ChatAlert);
        Notification notification = alert.getNotifications().get(0);
        Assert.assertTrue(notification instanceof Overlay);
        Assert.assertNotNull(((Overlay) notification).getTextColor());

    }
}
