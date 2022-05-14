package com.adamk33n3r.runelite.watchdog;

import com.adamk33n3r.runelite.watchdog.alerts.*;
import com.adamk33n3r.runelite.watchdog.notifications.*;
import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.inject.Provides;

import javax.inject.Inject;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.ItemID;
import net.runelite.api.Skill;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.StatChanged;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.events.NotificationFired;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.AsyncBufferedImage;
import net.runelite.http.api.ws.RuntimeTypeAdapterFactory;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Slf4j
@PluginDescriptor(
    name = "Watchdog"
)
public class WatchdogPlugin extends Plugin {
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

    @Inject
    private Client client;

    private WatchdogPanel panel;

    private NavigationButton navButton;

    private Gson gson;

    private List<Alert> cachedAlerts;

    private int[] previousLevels = new int[Skill.values().length];

    // TODO: create an alert manager
//    private List<Alert> alerts = new ArrayList<>();

    private static final Type alertListType;

    static {
        alertListType = new TypeToken<List<Alert>>() {}.getType();
    }

    @Getter
    private FlashOverlay flashOverlay;

    @Getter
    private static WatchdogPlugin instance;

    @Override
    protected void startUp() throws Exception {
        // TODO: Fix the notifications to not modify the obj
        instance = this;
        this.flashOverlay = this.injector.getInstance(FlashOverlay.class);
        this.overlayManager.add(this.flashOverlay);
        // Add new alert types here
        final RuntimeTypeAdapterFactory<Alert> alertTypeFactory = RuntimeTypeAdapterFactory.of(Alert.class)
            .registerSubtype(ChatAlert.class)
            .registerSubtype(IdleAlert.class)
            .registerSubtype(NotificationFiredAlert.class)
            .registerSubtype(StatDrainAlert.class)
            .registerSubtype(ResourceAlert.class);
        // Add new notification types here
        final RuntimeTypeAdapterFactory<Notification> notificationTypeFactory = RuntimeTypeAdapterFactory.of(Notification.class)
            .registerSubtype(TrayNotification.class)
            .registerSubtype(TextToSpeech.class)
            .registerSubtype(Sound.class)
            .registerSubtype(ScreenFlash.class)
            .registerSubtype(GameMessage.class);
        this.gson = new GsonBuilder()
//            .serializeNulls()
            .registerTypeAdapterFactory(alertTypeFactory)
            .registerTypeAdapterFactory(notificationTypeFactory)
            .create();

        List<Alert> alerts = this.refetchAlerts();

        if (alerts.isEmpty()) {
            alerts.add(new ChatAlert("Test Chat Alert 1"));
            alerts.add(new ResourceAlert("Low HP"));
            alerts.add(new ChatAlert("Nothing interesting happens"));
            this.saveAlerts(alerts);
        }

        this.panel = this.injector.getInstance(WatchdogPanel.class);
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

    public List<Alert> getAlerts() {
        if (this.cachedAlerts == null) {
            this.cachedAlerts = this.fetchAlerts();
        }
        return this.cachedAlerts;
    }
    public List<Alert> refetchAlerts() {
        return this.cachedAlerts = this.fetchAlerts();
    }
    public List<Alert> fetchAlerts() {
        String json = config.alerts();
        List<Alert> alerts = new ArrayList<>();
        if (!Strings.isNullOrEmpty(json)) {
            alerts = this.gson.fromJson(json, alertListType);
        }
        for (Alert alert : alerts) {
            this.injector.injectMembers(alert);
            for (INotification notification : alert.getNotifications()) {
                this.injector.injectMembers(notification);
            }
        }
        return alerts;
    }

    public void saveAlerts(List<Alert> alerts) {
        this.cachedAlerts = alerts;
        String json = this.gson.toJson(alerts, alertListType);
        this.configManager.setConfiguration(WatchdogConfig.configGroupName, "alerts", json);
    }

    @Override
    protected void shutDown() throws Exception {
        this.clientToolbar.removeNavigation(this.navButton);
        this.overlayManager.remove(this.flashOverlay);
    }

    @Subscribe
    public void onChatMessage(ChatMessage chatMessage) {
        // Don't process messages sent by this plugin
        if (chatMessage.getName().equals(this.getName())) {
            return;
        }

//        log.debug(chatMessage.getType().name() + ": " + chatMessage.getMessage());

        // Filter out player messages
        if (
            chatMessage.getType() == ChatMessageType.PUBLICCHAT
            || chatMessage.getType() == ChatMessageType.AUTOTYPER
            || chatMessage.getType() == ChatMessageType.PRIVATECHAT
            || chatMessage.getType() == ChatMessageType.PRIVATECHATOUT
            || chatMessage.getType() == ChatMessageType.MODCHAT
            || chatMessage.getType() == ChatMessageType.MODPRIVATECHAT
            || chatMessage.getType() == ChatMessageType.MODAUTOTYPER
            || chatMessage.getType() == ChatMessageType.FRIENDSCHAT
            || chatMessage.getType() == ChatMessageType.CLAN_CHAT
            || chatMessage.getType() == ChatMessageType.CLAN_GUEST_CHAT
            || chatMessage.getType() == ChatMessageType.CLAN_GIM_CHAT
        ) {
            return;
        }

        this.getAlerts().stream()
            .filter(alert -> alert instanceof ChatAlert)
            .map(alert -> (ChatAlert) alert)
//            .filter(chatAlert -> chatAlert.getChatMessageType() == chatMessage.getType())
            .filter(chatAlert -> {
                // TODO: Implement capture groups. Not sure best way to do this since notifications can be used on
                // alerts that wouldn't have this
                String regex = Util.createRegexFromGlob(chatAlert.getMessage());
                return Pattern.matches("(?i)"+regex, chatMessage.getMessage());
            })
            .forEach(this::fireAlert);
    }

    @Subscribe
    public void onNotificationFired(NotificationFired notificationFired) {
        this.getAlerts().stream()
            .filter(alert -> alert instanceof NotificationFiredAlert)
            .map(alert -> (NotificationFiredAlert) alert)
            .filter(notificationFiredAlert -> {
                String regex = Util.createRegexFromGlob(notificationFiredAlert.getMessage());
                return Pattern.matches("(?i)"+regex, notificationFired.getMessage());
            })
            .forEach(this::fireAlert);
    }

    @Subscribe
    public void onStatChanged(StatChanged statChanged) {
//        log.debug(String.format("%s: %s/%s", statChanged.getSkill().getName(), statChanged.getBoostedLevel(), statChanged.getLevel()));
        int previousLevel = this.previousLevels[statChanged.getSkill().ordinal()];
        this.getAlerts().stream()
            .filter(alert -> alert instanceof StatDrainAlert)
            .map(alert -> (StatDrainAlert) alert)
            .filter(alert -> {
                boolean isSkill = alert.getSkill() == statChanged.getSkill();
                int targetLevel = statChanged.getLevel() - alert.getDrainAmount();
                boolean isLower = statChanged.getBoostedLevel() <= targetLevel;
                boolean wasHigher = previousLevel > targetLevel;
                log.debug("targetLevel: " + targetLevel);
                log.debug("{}, {}, {}", isSkill, isLower, wasHigher);
                return isSkill && isLower && wasHigher;
            })
            .forEach(this::fireAlert);

        int[] boostedSkillLevels = this.client.getBoostedSkillLevels();
        for (Skill skill : Skill.values()) {
            this.previousLevels[skill.ordinal()] = boostedSkillLevels[skill.ordinal()];
        }
    }

    @Subscribe
    private void onConfigChanged(ConfigChanged configChanged) {
        if (configChanged.getGroup().equals(WatchdogConfig.configGroupName) && configChanged.getKey().equals("alerts")) {
            this.panel.rebuild();
        }
    }

    @Subscribe
    private void onGameStateChanged(GameStateChanged gameStateChanged) {
        int[] boostedSkillLevels = this.client.getBoostedSkillLevels();
        for (Skill skill : Skill.values()) {
            this.previousLevels[skill.ordinal()] = boostedSkillLevels[skill.ordinal()];
        }
    }

    @Provides
    WatchdogConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(WatchdogConfig.class);
    }

    private void fireAlert(Alert alert) {
        alert.getNotifications().forEach(Notification::fire);
    }
}
