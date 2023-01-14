package com.adamk33n3r.runelite.watchdog;

import com.adamk33n3r.runelite.watchdog.alerts.*;
import com.adamk33n3r.runelite.watchdog.notifications.*;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import joptsimple.internal.Strings;
import lombok.Getter;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.util.RuntimeTypeAdapterFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.swing.SwingUtilities;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Singleton
public class AlertManager {
    @Inject
    private ConfigManager configManager;
    @Inject
    private Gson clientGson;
    @Getter
    private Gson gson;

    @Getter
    private final List<Alert> alerts = new CopyOnWriteArrayList<>();

    @Getter
    @Inject
    private WatchdogPanel watchdogPanel;

    private static final Type ALERT_LIST_TYPE;

    static {
        ALERT_LIST_TYPE = new TypeToken<List<Alert>>() {}.getType();
    }

    @Inject
    private void init() {
        // Add new alert types here
        final RuntimeTypeAdapterFactory<Alert> alertTypeFactory = RuntimeTypeAdapterFactory.of(Alert.class)
            .registerSubtype(ChatAlert.class)
            .registerSubtype(IdleAlert.class)
            .registerSubtype(NotificationFiredAlert.class)
            .registerSubtype(StatDrainAlert.class)
            .registerSubtype(ResourceAlert.class)
            .registerSubtype(SoundFiredAlert.class);
        // Add new notification types here
        final RuntimeTypeAdapterFactory<Notification> notificationTypeFactory = RuntimeTypeAdapterFactory.of(Notification.class)
            .registerSubtype(TrayNotification.class)
            .registerSubtype(TextToSpeech.class)
            .registerSubtype(Sound.class)
            .registerSubtype(ScreenFlash.class)
            .registerSubtype(GameMessage.class)
            .registerSubtype(Overhead.class);
        this.gson = this.clientGson.newBuilder()
//            .serializeNulls()
            .registerTypeAdapterFactory(alertTypeFactory)
            .registerTypeAdapterFactory(notificationTypeFactory)
            .create();
    }

    public void addAlert(Alert alert) {
        this.alerts.add(alert);
        this.saveAlerts();

        SwingUtilities.invokeLater(this.watchdogPanel::rebuild);
    }

    public void removeAlert(Alert alert) {
        this.alerts.remove(alert);
        this.saveAlerts();

        SwingUtilities.invokeLater(this.watchdogPanel::rebuild);
    }

    public void loadAlerts() {
        final String json = this.configManager.getConfiguration(WatchdogConfig.CONFIG_GROUP_NAME, WatchdogConfig.ALERTS);
        this.importAlerts(json, false);
    }

    public void importAlerts(String json, boolean append) {
        if (!Strings.isNullOrEmpty(json)) {
            if (!append) {
                this.alerts.clear();
            }
            this.alerts.addAll(this.gson.fromJson(json, ALERT_LIST_TYPE));
            // Save immediately to save new properties
            this.saveAlerts();
        }

        // Inject dependencies
        for (Alert alert : this.alerts) {
            WatchdogPlugin.getInstance().getInjector().injectMembers(alert);
            for (INotification notification : alert.getNotifications()) {
                WatchdogPlugin.getInstance().getInjector().injectMembers(notification);
            }
        }

        SwingUtilities.invokeLater(this.watchdogPanel::rebuild);
    }

    public void saveAlerts() {
        String json = this.gson.toJson(this.alerts, ALERT_LIST_TYPE);
        this.configManager.setConfiguration(WatchdogConfig.CONFIG_GROUP_NAME, WatchdogConfig.ALERTS, json);
    }

    public String toJSON() {
        return this.gson.toJson(this.alerts, ALERT_LIST_TYPE);
    }

}
