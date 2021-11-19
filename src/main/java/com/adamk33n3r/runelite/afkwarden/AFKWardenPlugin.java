package com.adamk33n3r.runelite.afkwarden;

import com.adamk33n3r.runelite.afkwarden.alerts.Alert;
import com.adamk33n3r.runelite.afkwarden.alerts.ChatAlert;
import com.adamk33n3r.runelite.afkwarden.alerts.ResourceAlert;
import com.adamk33n3r.runelite.afkwarden.notifications.*;
import com.adamk33n3r.tts.MessageSegment;
import com.adamk33n3r.tts.TTSSegmentProcessor;
import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.inject.Provides;

import javax.inject.Inject;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.ItemID;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.client.chat.ChatMessageManager;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.PluginManager;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.util.AsyncBufferedImage;
import net.runelite.client.util.Text;
import net.runelite.http.api.ws.RuntimeTypeAdapterFactory;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@PluginDescriptor(
    name = "AFK Warden"
)
public class AFKWardenPlugin extends Plugin {
    @Inject
    private AFKWardenConfig config;
    @Inject
    private ConfigManager configManager;
    @Inject
    private ItemManager itemManager;

    @Inject
    private TTSSegmentProcessor ttsSegmentProcessor;

    @Inject
    private ClientToolbar clientToolbar;

    @Inject
    private PluginManager pluginManager;

    private AFKWardenPanel panel;

    private NavigationButton navButton;

    private Gson gson;

    // TODO: create an alert manager
//    private List<Alert> alerts = new ArrayList<>();

    private static final Type alertListType;

    static {
        alertListType = new TypeToken<List<Alert>>() {}.getType();
    }

    @Override
    protected void startUp() throws Exception {
        log.info("Example started!");
        final RuntimeTypeAdapterFactory<Alert> alertTypeFactory = RuntimeTypeAdapterFactory.of(Alert.class)
            .registerSubtype(ChatAlert.class)
            .registerSubtype(ResourceAlert.class);
        final RuntimeTypeAdapterFactory<INotification> notificationTypeFactory = RuntimeTypeAdapterFactory.of(INotification.class)
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
//        this.ttsSegmentProcessor.add(new MessageSegment("this is a test of the tts"));

        List<Alert> alerts = this.getAlerts();

        if (alerts.isEmpty()) {
            alerts.add(new ChatAlert("Test Chat Alert 1"));
            alerts.add(new ResourceAlert("Low HP"));
            alerts.add(new ChatAlert("Nothing interesting happens"));
            this.saveAlerts(alerts);
        }

        for (Alert alert : alerts) {
            log.info(alert.getClass().getSimpleName());
        }

        this.panel = this.injector.getInstance(AFKWardenPanel.class);
        AsyncBufferedImage icon = this.itemManager.getImage(ItemID.BELL_BAUBLE);
        this.navButton = NavigationButton.builder()
            .tooltip("AFK Warden")
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
        log.info(this.navButton.isTab() ? "is tab" : "is not tab");
        this.navButton.setSelected(true);
    }

    public List<Alert> getAlerts() {
        String json = configManager.getConfiguration("afk-warden", "alerts");
        log.info(json);
        Type alertListType = new TypeToken<List<Alert>>() {}.getType();
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
        String json = this.gson.toJson(alerts, alertListType);
        this.configManager.setConfiguration("afk-warden", "alerts", json);
    }

    public List<ChatAlert> getChatAlerts() {
        return this.getAlerts().stream()
            .filter(alert -> alert instanceof ChatAlert)
            .map(alert -> (ChatAlert)alert)
            .collect(Collectors.toList());
    }

    @Override
    protected void shutDown() throws Exception {
        log.info("Example stopped!");
        this.clientToolbar.removeNavigation(this.navButton);
    }

    @Subscribe
    public void onChatMessage(ChatMessage chatMessage) {
        log.info(chatMessage.getType().toString());
        log.info(chatMessage.getMessage());

        this.getChatAlerts().stream()
            .filter(chatAlert -> chatAlert.getChatMessageType() == chatMessage.getType())
            .forEach(chatAlert -> {
                if (chatMessage.getMessage().equals(chatAlert.getMessage())) {
                    for (INotification notification : chatAlert.getNotifications()) {
                        notification.fire(this);
                    }
                }
            });
//        if (chatMessage.getType() == this.config.chatMessageType()) {
//            String message = chatMessage.getMessage();
//            if (message.contains(this.config.chatMessage())) {
//                this.say("ALERT ALERT ALERT!");
//            }
//        }
    }

    @Subscribe
    public void onGameStateChanged(GameStateChanged gameStateChanged) {
//        if (gameStateChanged.getGameState() == GameState.LOGGED_IN) {
//        }
    }

    @Subscribe
    public void onGameTick(GameTick gameTick) {
        this.ttsSegmentProcessor.process();
    }

    @Subscribe
    private void onConfigChanged(ConfigChanged configChanged) {
        if (configChanged.getGroup().equals("afk-warden") && configChanged.getKey().equals("alerts")) {
            log.info("Calling REBUILD!!!");
            this.panel.rebuild();
        }
    }

    @Provides
    AFKWardenConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(AFKWardenConfig.class);
    }

    private void say(String message) {
        this.ttsSegmentProcessor.add(new MessageSegment(this.sanitize(message)));
    }

    private String sanitize(String message) {
        return Text.sanitizeMultilineText(
            message
                // Replace hyphens with spaces. It has trouble processing utterances.
                .replaceAll("-", " ")
                // The synthesizer seems to treat an ellipsis as nothing. Replace it with a period.
                .replaceAll("\\.\\.\\.", ". ")
        );
    }
}
