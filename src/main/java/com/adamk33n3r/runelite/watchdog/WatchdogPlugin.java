package com.adamk33n3r.runelite.watchdog;

import com.adamk33n3r.runelite.watchdog.alerts.*;
import com.adamk33n3r.runelite.watchdog.notifications.*;
import com.google.inject.Binder;
import com.google.inject.Provides;

import javax.inject.Inject;

import com.google.inject.name.Names;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.ItemID;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.AsyncBufferedImage;

import java.util.List;
import java.util.Properties;

@Slf4j
@PluginDescriptor(
    name = "Watchdog"
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

    private WatchdogPanel panel;

    private NavigationButton navButton;

    @Getter
    private static WatchdogPlugin instance;

    public WatchdogPlugin() {
        instance = this;
    }

    @Override
    public void configure(Binder binder) {
        Properties properties = WatchdogProperties.getProperties();
        Names.bindProperties(binder, properties);
    }

    @Override
    protected void startUp() throws Exception {
        // TODO: Fix the notifications to not modify the obj

        this.eventBus.register(this.eventHandler);

        this.overlayManager.add(this.flashOverlay);

        this.alertManager.loadAlerts();
        List<Alert> alerts = this.alertManager.getAlerts();

        if (alerts.isEmpty()) {
            ChatAlert readyToHarvest = new ChatAlert("Ready to Harvest");
            readyToHarvest.setDebounceTime(500);
            readyToHarvest.setMessage("*is ready to harvest*");
            TrayNotification harvestNotification = new TrayNotification();
            harvestNotification.setMessage("Time to harvest your crops!");
            readyToHarvest.getNotifications().add(harvestNotification);
            this.alertManager.addAlert(readyToHarvest);

            NotificationFiredAlert outOfCombat = new NotificationFiredAlert("Out of Combat");
            outOfCombat.setMessage("You are now out of combat!");
            outOfCombat.getNotifications().add(new ScreenFlash());
            this.alertManager.addAlert(outOfCombat);
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
    }

    @Override
    protected void shutDown() throws Exception {
        this.eventBus.unregister(this.eventHandler);
        this.clientToolbar.removeNavigation(this.navButton);
        this.overlayManager.remove(this.flashOverlay);
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
            }
        }
    }

    @Provides
    WatchdogConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(WatchdogConfig.class);
    }
}
