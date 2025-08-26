package com.adamk33n3r.runelite.watchdog;

import com.adamk33n3r.runelite.watchdog.alerts.Alert;
import com.adamk33n3r.runelite.watchdog.ui.notifications.screenmarker.ScreenMarkerUtil;

import net.runelite.api.Client;
import net.runelite.api.ItemID;
import net.runelite.api.MessageNode;
import net.runelite.api.WorldView;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.OverheadTextChanged;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.events.NotificationFired;
import net.runelite.client.events.OverlayMenuClicked;
import net.runelite.client.events.ProfileChanged;
import net.runelite.client.game.ItemManager;
import net.runelite.client.input.KeyManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.ui.overlay.OverlayMenuEntry;
import net.runelite.client.util.AsyncBufferedImage;

import com.google.common.collect.EvictingQueue;
import com.google.inject.Binder;
import com.google.inject.Provides;
import com.google.inject.name.Names;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.util.HotkeyListener;
import okhttp3.OkHttpClient;

import javax.inject.Inject;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Queue;

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

    @Getter
    @Inject
    private PopupManager popupManager;

    @Inject
    private KeyManager keyManager;

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

    @Getter
    @Inject
    private ScreenMarkerUtil screenMarkerUtil;

    @Getter
    @Inject
    private WatchdogPanel panel;

    @Getter
    @Inject
    private SoundPlayer soundPlayer;

    @Getter
    @Inject
    private OkHttpClient httpClient;

    private AsyncBufferedImage icon;
    private AsyncBufferedImage iconDisabled;
    private NavigationButton navButton;
    private NavigationButton navButtonDisabled;

    @Getter
    private static WatchdogPlugin instance;

    @Getter
    private final Queue<MessageNode> messageQueue = EvictingQueue.create(200);

    @Getter
    private final Queue<OverheadTextChanged> overheadTextQueue = EvictingQueue.create(200);

    @Getter
    private final Queue<NotificationFired> notificationsQueue = EvictingQueue.create(20);

    private final List<AlertProcessor> alertProcessors = new ArrayList<>();

    @Getter
    private boolean isInBannedArea = false;

    private final List<HotkeyListener> hotkeyListeners = List.of(
        new HotkeyListener(() -> config.clearAllHotkey()) {
            @Override
            public void hotkeyPressed() {
                stopAllAlerts();
                soundPlayer.stop();
                notificationOverlay.clear();
                screenMarkerUtil.removeAllMarkers();
            }
        },
        new HotkeyListener(() -> config.stopAllProcessingAlertsHotkey()) {
            @Override
            public void hotkeyPressed() {
                stopAllAlerts();
            }
        },
        new HotkeyListener(() -> config.stopAllQueuedSoundsHotkey()) {
            @Override
            public void hotkeyPressed() {
                soundPlayer.stop();
            }
        },
        new HotkeyListener(() -> config.dismissAllOverlaysHotkey()) {
            @Override
            public void hotkeyPressed() {
                notificationOverlay.clear();
            }
        },
        new HotkeyListener(() -> config.dismissAllScreenMarkersHotkey()) {
            @Override
            public void hotkeyPressed() {
                screenMarkerUtil.removeAllMarkers();
            }
        }
    );

    public WatchdogPlugin() {
        instance = this;
    }

    @Override
    public void configure(Binder binder) {
        Properties properties = WatchdogProperties.getProperties();
        Names.bindProperties(binder, properties);
        binder.bind(WatchdogMuxer.class).toProvider(() -> this.panel.getMuxer());
    }

    @Override
    protected void startUp() throws Exception {
        this.eventBus.register(this.eventHandler);

        this.overlayManager.add(this.flashOverlay);
        this.overlayManager.add(this.notificationOverlay);
        this.screenMarkerUtil.startUp();

        this.alertManager.loadAlerts();

        this.icon = this.itemManager.getImage(ItemID.BELL_BAUBLE);
        this.iconDisabled = this.itemManager.getImage(ItemID.BELL_BAUBLE_6848);

        this.rebuildSidePanelButtons();

        this.soundPlayer.startUp();

        ToolTipManager.sharedInstance().setDismissDelay(Integer.MAX_VALUE);

        for (var hotkeyListener : this.hotkeyListeners) {
            this.keyManager.registerKeyListener(hotkeyListener);
        }
    }

    private void rebuildSidePanelButtons() {
        if (this.navButton != null) {
            this.clientToolbar.removeNavigation(this.navButton);
        }
        if (this.navButtonDisabled != null) {
            this.clientToolbar.removeNavigation(this.navButtonDisabled);
        }
        this.navButton = NavigationButton.builder()
            .tooltip("Watchdog")
            .icon(icon)
            .priority(this.config.sidePanelPriority())
            .panel(this.panel.getMuxer())
            .build();
        this.navButtonDisabled = NavigationButton.builder()
            .tooltip("Watchdog (In disabled area)")
            .icon(iconDisabled)
            .priority(this.config.sidePanelPriority())
            .panel(this.panel.getMuxer())
            .build();
        this.icon.onLoaded(() -> {
            if (this.isInBannedArea) {
                this.clientToolbar.addNavigation(this.navButtonDisabled);
            } else {
                this.clientToolbar.addNavigation(this.navButton);
            }
        });
    }

    @Override
    protected void shutDown() throws Exception {
        this.eventBus.unregister(this.eventHandler);
        this.clientToolbar.removeNavigation(this.navButton);
        this.clientToolbar.removeNavigation(this.navButtonDisabled);
        this.overlayManager.remove(this.flashOverlay);
        this.overlayManager.remove(this.notificationOverlay);
        this.soundPlayer.shutDown();
        this.screenMarkerUtil.shutDown();
        for (var hotkeyListener : this.hotkeyListeners) {
            this.keyManager.unregisterKeyListener(hotkeyListener);
        }
    }

    public void openConfiguration() {
        // We don't have access to the ConfigPlugin so let's just emulate an overlay click
        this.eventBus.post(new OverlayMenuClicked(new OverlayMenuEntry(RUNELITE_OVERLAY_CONFIG, null, null), this.notificationOverlay));
    }

    public void processAlert(Alert alert, String[] triggerValues, boolean forceFire) {
        var alertProcessor = new AlertProcessor(alert, triggerValues, true, this.alertProcessors::remove);
        this.alertProcessors.add(alertProcessor);
        alertProcessor.start();
    }

    public void stopAllAlerts() {
        for (var alertProcessor : alertProcessors) {
            alertProcessor.interrupt();
        }
        // Just in case it doesn't call the onFinish callback
        this.alertProcessors.clear();
    }

    @Subscribe
    private void onGameTick(GameTick gameTick) {
        int regionID = WorldPoint.fromLocalInstance(this.client, this.client.getLocalPlayer().getLocalLocation()).getRegionID();
        boolean before = this.isInBannedArea;
        WorldView worldView = this.client.getLocalPlayer().getWorldView();
        this.isInBannedArea = Region.isBannedRegion(regionID, worldView);

        // State changed so switch panel icon
        if (before != this.isInBannedArea) {
            if (this.isInBannedArea) {
                this.clientToolbar.removeNavigation(this.navButton);
                this.clientToolbar.addNavigation(this.navButtonDisabled);
                if (this.panel.getMuxer().isActive()) {
                    SwingUtilities.invokeLater(() -> this.clientToolbar.openPanel(this.navButtonDisabled));
                }
            } else {
                this.clientToolbar.removeNavigation(this.navButtonDisabled);
                this.clientToolbar.addNavigation(this.navButton);
                if (this.panel.getMuxer().isActive()) {
                    SwingUtilities.invokeLater(() -> this.clientToolbar.openPanel(this.navButton));
                }
            }
        }

        this.popupManager.processPopupQueue();
    }

    @Subscribe
    private void onGameStateChanged(GameStateChanged gameStateChanged) {
        this.soundPlayer.stop();
    }

    @Subscribe
    private void onChatMessage(ChatMessage chatMessage) {
        this.messageQueue.offer(chatMessage.getMessageNode());
    }
    @Subscribe
    private void onOverheadTextChanged(OverheadTextChanged overheadTextChanged) {
        this.overheadTextQueue.offer(overheadTextChanged);
    }
    @Subscribe
    private void onNotificationFired(NotificationFired notificationFired) {
        this.notificationsQueue.offer(notificationFired);
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
            } else if (configChanged.getKey().equals(WatchdogConfig.SIDE_PANEL_PRIORITY)) {
                this.rebuildSidePanelButtons();
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
