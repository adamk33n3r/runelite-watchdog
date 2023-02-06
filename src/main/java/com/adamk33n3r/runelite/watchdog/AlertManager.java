package com.adamk33n3r.runelite.watchdog;

import com.adamk33n3r.runelite.watchdog.alerts.*;
import com.adamk33n3r.runelite.watchdog.notifications.*;

import net.runelite.client.config.ConfigManager;
import net.runelite.client.util.RuntimeTypeAdapterFactory;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import joptsimple.internal.Strings;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import javax.swing.SwingUtilities;
import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Slf4j
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

    @Inject
    @Named("watchdog.pluginVersion")
    private String pluginVersion;

    private static final Type ALERT_LIST_TYPE;

    static {
        ALERT_LIST_TYPE = new TypeToken<List<Alert>>() {}.getType();
    }

    @Inject
    private void init() {
        // Add new alert types here
        final RuntimeTypeAdapterFactory<Alert> alertTypeFactory = RuntimeTypeAdapterFactory.of(Alert.class)
            .registerSubtype(ChatAlert.class)
            .registerSubtype(NotificationFiredAlert.class)
            .registerSubtype(StatDrainAlert.class)
            .registerSubtype(StatChangedAlert.class)
            .registerSubtype(XPDropAlert.class)
            .registerSubtype(SoundFiredAlert.class);
        // Add new notification types here
        final RuntimeTypeAdapterFactory<Notification> notificationTypeFactory = RuntimeTypeAdapterFactory.of(Notification.class)
            .registerSubtype(TrayNotification.class)
            .registerSubtype(TextToSpeech.class)
            .registerSubtype(Sound.class)
            .registerSubtype(SoundEffect.class)
            .registerSubtype(ScreenFlash.class)
            .registerSubtype(GameMessage.class)
            .registerSubtype(Overhead.class)
            .registerSubtype(Overlay.class)
            .registerSubtype(NotificationEvent.class);
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
        this.handleUpgrades();
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
            // Shouldn't ever happen...but just in case.
            if (alert == null) {
                continue;
            }

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

    private void handleUpgrades() {
        Version currentVersion = new Version(this.pluginVersion);
        Version configVersion = new Version(this.configManager.getConfiguration(WatchdogConfig.CONFIG_GROUP_NAME, WatchdogConfig.PLUGIN_VERSION));
        log.debug("currentVersion: " + currentVersion);
        log.debug("configVersion: " + configVersion);
        if (currentVersion.compareTo(configVersion) > 0) {
            log.debug("Checking if data migration needed");
            // Changed Stat Drain to Stat Change in v2.4.0, so need to swap sign of drainAmount and move to new alert
            if (configVersion.compareTo(new Version("2.4.0")) < 0) {
                log.debug("Need to convert StatDrainAlerts to StatChangedAlerts");
                this.alerts.replaceAll(alert -> {
                    if (alert instanceof StatDrainAlert) {
                        StatDrainAlert statDrainAlert = (StatDrainAlert) alert;
                        StatChangedAlert statChangedAlert = new StatChangedAlert();
                        statChangedAlert.setName(statDrainAlert.getName());
                        statChangedAlert.setEnabled(statDrainAlert.isEnabled());
                        statChangedAlert.setDebounceTime(statDrainAlert.getDebounceTime());
                        statChangedAlert.setSkill(statDrainAlert.getSkill());
                        statChangedAlert.setChangedAmount(-statDrainAlert.getDrainAmount());
                        statChangedAlert.getNotifications().addAll(statDrainAlert.getNotifications());
                        return statChangedAlert;
                    }

                    return alert;
                });

                // Not sure why I thought it was a good idea to store the decibels in the JSON
                log.debug("Need to convert all Sound and TTS gain back to 0,10 scale.");
                this.alerts.stream()
                    .flatMap(alert -> alert.getNotifications().stream())
                    .filter(notification -> notification instanceof Sound)
                    .map(notification -> (Sound) notification)
                    .forEach(sound -> sound.setGain(Util.scale(sound.getGain(), -25, 5, 0, 10)));
            }

            this.configManager.setConfiguration(WatchdogConfig.CONFIG_GROUP_NAME, WatchdogConfig.PLUGIN_VERSION, currentVersion.getVersion());
            this.saveAlerts();
        }
    }

}
