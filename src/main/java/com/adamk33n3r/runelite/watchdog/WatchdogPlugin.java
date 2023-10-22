package com.adamk33n3r.runelite.watchdog;

import com.adamk33n3r.runelite.watchdog.alerts.Alert;
import com.adamk33n3r.runelite.watchdog.alerts.ChatAlert;
import com.adamk33n3r.runelite.watchdog.alerts.NotificationFiredAlert;
import com.adamk33n3r.runelite.watchdog.notifications.ScreenFlash;
import com.adamk33n3r.runelite.watchdog.notifications.TrayNotification;

import net.runelite.api.Client;
import net.runelite.api.ItemID;
import net.runelite.api.MenuAction;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.events.OverlayMenuClicked;
import net.runelite.client.events.ProfileChanged;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.config.ConfigPlugin;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.MultiplexingPluginPanel;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.ui.overlay.OverlayMenuEntry;
import net.runelite.client.util.AsyncBufferedImage;

import com.google.inject.Binder;
import com.google.inject.Provides;
import com.google.inject.name.Names;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;

import javax.inject.Inject;
import javax.inject.Provider;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import static net.runelite.api.MenuAction.RUNELITE_OVERLAY_CONFIG;

@Slf4j
@PluginDescriptor(
    name = "Watchdog",
    description = "Create custom alerts for different events like messages, stats, or built-in notifications",
    tags = {"alert", "notification","custom","advanced","overlay","sound","drop","afk","tracker","reminder","notify","notifier","helper","timer","message"}
)
public class WatchdogPlugin extends Plugin {
    @Getter
    @Inject
    private WatchdogConfig config;
    @Inject
    private ConfigManager configManager;
    @Inject
    private ItemManager itemManager;

    @Inject
    private ClientToolbar clientToolbar;

    @Inject
    private OverlayManager overlayManager;

    @Getter
    @Inject
    private AlertManager alertManager;

    @Inject
    private Client client;

    @Inject
    private EventBus eventBus;

    @Inject
    private EventHandler eventHandler;

    @Getter
    @Inject
    private FlashOverlay flashOverlay;

    @Getter
    @Inject
    private NotificationOverlay notificationOverlay;

    @Inject
    private Provider<ConfigPlugin> configPluginProvider;

    @Getter
    private WatchdogPanel panel;

    @Getter
    @Inject
    private SoundPlayer soundPlayer;

    @Getter
    @Inject
    private OkHttpClient httpClient;

    private NavigationButton navButton;

    @Getter
    private static WatchdogPlugin instance;

    private ScheduledFuture<?> soundPlayerFuture;

    private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

    public WatchdogPlugin() {
        instance = this;
    }

    @Override
    public void configure(Binder binder) {
        Properties properties = WatchdogProperties.getProperties();
        Names.bindProperties(binder, properties);
        binder.bind(MultiplexingPluginPanel.class).toProvider(() -> alertManager.getWatchdogPanel().getMuxer());
    }

    @Override
    protected void startUp() throws Exception {
        this.eventBus.register(this.eventHandler);

        this.overlayManager.add(this.flashOverlay);
        this.overlayManager.add(this.notificationOverlay);

        this.alertManager.loadAlerts();
        List<Alert> alerts = this.alertManager.getAlerts();

        if (alerts.isEmpty()) {
            ChatAlert readyToHarvest = new ChatAlert("Ready to Harvest");
            readyToHarvest.setDebounceTime(500);
            readyToHarvest.setMessage("*is ready to harvest*");
            TrayNotification harvestNotification = this.injector.getInstance(TrayNotification.class);
            harvestNotification.setMessage("Time to harvest your crops!");
            readyToHarvest.getNotifications().add(harvestNotification);
            this.alertManager.addAlert(readyToHarvest, false);

            NotificationFiredAlert outOfCombat = new NotificationFiredAlert("Out of Combat");
            outOfCombat.setMessage("You are now out of combat!");
            outOfCombat.getNotifications().add(this.injector.getInstance(ScreenFlash.class));
            this.alertManager.addAlert(outOfCombat, false);
        }

        this.panel = this.alertManager.getWatchdogPanel();
        AsyncBufferedImage icon = this.itemManager.getImage(ItemID.BELL_BAUBLE);
        this.navButton = NavigationButton.builder()
            .tooltip("Watchdog")
            .icon(icon)
            .priority(1)
            .panel(this.panel.getMuxer())
            .build();
        this.clientToolbar.addNavigation(this.navButton);
        // For first load
        icon.onLoaded(() -> {
            this.clientToolbar.removeNavigation(this.navButton);
            this.clientToolbar.addNavigation(this.navButton);
        });

        this.startSoundPlayerTimer();
    }

    private void startSoundPlayerTimer() {
        if (this.soundPlayerFuture != null && !this.soundPlayerFuture.isCancelled()) {
            return;
        }

        // Don't start the timer if we're not queuing sounds
        if (!this.config.putSoundsIntoQueue()) {
            return;
        }

        this.soundPlayerFuture = this.executor.scheduleAtFixedRate(
            () -> this.soundPlayer.processQueue(),
            0,
            100,
            TimeUnit.MILLISECONDS
        );
    }

    @Override
    protected void shutDown() throws Exception {
        this.eventBus.unregister(this.eventHandler);
        this.clientToolbar.removeNavigation(this.navButton);
        this.overlayManager.remove(this.flashOverlay);
        this.overlayManager.remove(this.notificationOverlay);
    }

    public void openConfiguration() {
        // We don't have access to the ConfigPlugin so let's just emulate an overlay click
        this.eventBus.post(new OverlayMenuClicked(new OverlayMenuEntry(RUNELITE_OVERLAY_CONFIG, null, null), this.notificationOverlay));
    }

    @Subscribe
    public void onOverlayMenuClicked(final OverlayMenuClicked event) {
        if (!(event.getEntry().getMenuAction() == MenuAction.RUNELITE_OVERLAY
            && event.getOverlay() == this.notificationOverlay))
        {
            return;
        }

        if (event.getEntry().getOption().equals(NotificationOverlay.CLEAR))
        {
            this.notificationOverlay.clear();
        }
    }

    @Subscribe
    private void onConfigChanged(ConfigChanged configChanged) {
        if (configChanged.getGroup().equals(WatchdogConfig.CONFIG_GROUP_NAME)) {
            if (configChanged.getKey().equals(WatchdogConfig.ENABLE_TTS)) {
                // To the top!
                while (this.panel.getMuxer().getComponentCount() > 1) {
                    this.panel.getMuxer().popState();
                }
                this.panel.rebuild();
            } else if (configChanged.getKey().equals(WatchdogConfig.PUT_SOUNDS_INTO_QUEUE)) {
                log.debug("sound queue config changed:"+this.config.putSoundsIntoQueue());
                if (this.config.putSoundsIntoQueue()) {
                    this.startSoundPlayerTimer();
                } else {
                    this.getSoundPlayer().clearQueue();
                    this.soundPlayerFuture.cancel(false);
                }
            }
        }
    }

    @Subscribe
    private void onProfileChanged(ProfileChanged profileChanged) {
        this.alertManager.loadAlerts();
    }

    @Provides
    WatchdogConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(WatchdogConfig.class);
    }
}
