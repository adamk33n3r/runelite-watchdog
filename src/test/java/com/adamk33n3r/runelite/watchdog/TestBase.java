package com.adamk33n3r.runelite.watchdog;

import com.google.gson.Gson;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.testing.fieldbinder.Bind;
import com.google.inject.testing.fieldbinder.BoundFieldModule;
import net.runelite.api.Client;
import net.runelite.client.chat.ChatMessageManager;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.config.RuneLiteConfig;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.ui.ClientUI;
import net.runelite.client.ui.MultiplexingPluginPanel;
import net.runelite.http.api.RuneLiteAPI;
import org.junit.Before;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.internal.util.reflection.FieldSetter;

import javax.inject.Named;
import javax.inject.Provider;
import java.util.concurrent.ScheduledExecutorService;

public abstract class TestBase {
    @Bind
    @Named("watchdog.pluginVersion")
    final String pluginVersion = "3.0.0-TEST";
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
    @Bind
    @Named("runelite.title")
    private final String RUNELITE_TITLE = "RuneLite";

    @Bind
    @Spy
    WatchdogPlugin watchdogPlugin;
    @Bind
    @Mock
    WatchdogConfig watchdogConfig;
    @Bind
    @Spy
    AlertManager alertManager;
    @Mock
    @Bind
    WatchdogPanel watchdogPanel;
    @Bind
    Provider<WatchdogMuxer> muxerProvider = () -> this.watchdogPanel.getMuxer();
    @Bind
    Provider<MultiplexingPluginPanel> multiplexingPluginPanelProvider = () -> alertManager.getWatchdogPanel().getMuxer();


    @Bind
    Gson clientGson = RuneLiteAPI.GSON;
    @Mock
    @Bind
    ScheduledExecutorService executor;
    @Mock
    @Bind
    RuneLiteConfig runeliteConfig;
    @Mock
    @Bind
    ConfigManager configManager;
    @Mock
    @Bind
    Client client;
    @Mock
    @Bind
    ClientUI clientUI;
    @Mock
    @Bind
    ItemManager itemManager;
    @Mock
    @Bind
    ChatMessageManager chatMessageManager;

    @Before
    public void before() throws NoSuchFieldException {
        BoundFieldModule module = BoundFieldModule.of(this);
        Injector injector = Guice.createInjector(module);
        injector.injectMembers(this);

        // can't mock the getInjector method because it's final
        FieldSetter.setField(watchdogPlugin, Plugin.class.getDeclaredField("injector"), injector);
    }
}
