package com.adamk33n3r.runelite.watchdog;

import com.adamk33n3r.runelite.watchdog.alerts.Alert;
import com.adamk33n3r.runelite.watchdog.alerts.AlertGroup;
import com.adamk33n3r.runelite.watchdog.alerts.ChatAlert;
import com.adamk33n3r.runelite.watchdog.alerts.FlashMode;
import com.adamk33n3r.runelite.watchdog.alerts.InventoryAlert;
import com.adamk33n3r.runelite.watchdog.alerts.NotificationFiredAlert;
import com.adamk33n3r.runelite.watchdog.alerts.RegexMatcher;
import com.adamk33n3r.runelite.watchdog.alerts.SpawnedAlert;
import com.adamk33n3r.runelite.watchdog.alerts.StatChangedAlert;
import com.adamk33n3r.runelite.watchdog.alerts.StatDrainAlert;
import com.adamk33n3r.runelite.watchdog.alerts.XPDropAlert;
import com.adamk33n3r.runelite.watchdog.notifications.GameMessage;
import com.adamk33n3r.runelite.watchdog.notifications.IAudioNotification;
import com.adamk33n3r.runelite.watchdog.notifications.INotification;
import com.adamk33n3r.runelite.watchdog.notifications.Notification;
import com.adamk33n3r.runelite.watchdog.notifications.NotificationEvent;
import com.adamk33n3r.runelite.watchdog.notifications.Overhead;
import com.adamk33n3r.runelite.watchdog.notifications.Overlay;
import com.adamk33n3r.runelite.watchdog.notifications.ScreenFlash;
import com.adamk33n3r.runelite.watchdog.notifications.Sound;
import com.adamk33n3r.runelite.watchdog.notifications.SoundEffect;
import com.adamk33n3r.runelite.watchdog.notifications.TextToSpeech;
import com.adamk33n3r.runelite.watchdog.notifications.TrayNotification;
import com.adamk33n3r.runelite.watchdog.ui.panels.PanelUtils;

import net.runelite.client.config.ConfigManager;
import net.runelite.client.config.FlashNotification;

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
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Supplier;
import java.util.stream.Stream;

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

    public static final Type ALERT_TYPE;
    public static final Type ALERT_LIST_TYPE;

    static {
        ALERT_TYPE = new TypeToken<Alert>() {}.getType();
        ALERT_LIST_TYPE = new TypeToken<List<Alert>>() {}.getType();
    }

    @Inject
    private void init() {
        // Add new alert types here
        final RuntimeTypeAdapterFactory<Alert> alertTypeFactory = RuntimeTypeAdapterFactory.of(Alert.class)
            .ignoreSubtype("IdleAlert")
            .ignoreSubtype("ResourceAlert")
            .ignoreSubtype("SoundFiredAlert")
            .ignoreSubtype("AlertGroup")
            .registerSubtype(ChatAlert.class)
            .registerSubtype(NotificationFiredAlert.class)
            .registerSubtype(StatDrainAlert.class)
            .registerSubtype(StatChangedAlert.class)
            .registerSubtype(XPDropAlert.class)
            .registerSubtype(SpawnedAlert.class)
            .registerSubtype(InventoryAlert.class)
            .registerSubtype(AlertGroup.class);
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

    public Stream<Alert> getAllEnabledAlerts() {
        return this.getAllAlerts().filter(Alert::isEnabled);
    }

    public <T extends Alert> Stream<T> getAllEnabledAlertsOfType(Class<T> type) {
        return this.getAllEnabledAlerts()
            .filter(type::isInstance)
            .map(type::cast);
    }

    public Stream<Alert> getAllAlerts() {
        return this.getAllAlertsFrom(this.alerts.stream());
    }

    public Stream<Alert> getAllAlertsFrom(Stream<Alert> alerts) {
        return alerts.flatMap(alert -> {
            if (alert instanceof AlertGroup) {
                return getAllAlertsFrom(((AlertGroup) alert).getAlerts().stream());
            }
            return Stream.of(alert);
        });
    }

    public void addAlert(Alert alert) {
        this.alerts.add(alert);
        this.saveAlerts();

        SwingUtilities.invokeLater(this.watchdogPanel::rebuild);
    }

    public void removeAlert(Alert alert) {
        AlertGroup parent = alert.getParent();
        if (parent != null) {
            parent.getAlerts().remove(alert);
        } else {
            this.alerts.remove(alert);
        }
        this.saveAlerts();

        SwingUtilities.invokeLater(this.watchdogPanel::rebuild);
    }

    public Alert cloneAlert(Alert alert) {
        String json = this.gson.toJson(alert, ALERT_TYPE);
        Alert clonedAlert = this.gson.fromJson(json, ALERT_TYPE);
        clonedAlert.setName(clonedAlert.getName() + " Clone");
        if (clonedAlert instanceof AlertGroup) {
            Util.setParentsOnAlerts(Collections.singletonList(clonedAlert));
        }
        return clonedAlert;
    }

    public void moveAlertTo(Alert alert, int pos) {
        AlertGroup parent = alert.getParent();
        if (parent != null) {
            parent.getAlerts().remove(alert);
            parent.getAlerts().add(pos, alert);
        } else {
            this.alerts.remove(alert);
            this.alerts.add(pos, alert);
        }
        this.saveAlerts();
    }

    public void loadAlerts() {
        final String json = this.configManager.getConfiguration(WatchdogConfig.CONFIG_GROUP_NAME, WatchdogConfig.ALERTS);
        this.importAlerts(json, this.alerts, false, false);
        this.handleUpgrades();
    }

    public boolean importAlerts(String json, List<Alert> alerts, boolean append, boolean checkRegex) {
        if (Strings.isNullOrEmpty(json)) {
            return false;
        }

        if (!append) {
            alerts.clear();
        }

        List<Alert> importedAlerts = this.gson.fromJson(json, ALERT_LIST_TYPE);
        Supplier<Stream<Alert>> alertStream = () -> importedAlerts.stream().filter(Objects::nonNull);

        // Validate regex properties
        if (checkRegex && !alertStream.get().allMatch(alert -> {
            if (alert instanceof RegexMatcher) {
                RegexMatcher matcher = (RegexMatcher) alert;
                return PanelUtils.isPatternValid(this.watchdogPanel, matcher.getPattern(), matcher.isRegexEnabled());
            }

            return true;
        })) {
            return false;
        }

        alertStream.get().forEach(alerts::add);

        // Save immediately to save new properties
        this.saveAlerts();

        Util.setParentsOnAlerts(alerts);

        // Inject dependencies
        this.getAllAlertsFrom(alertStream.get())
            .filter(alert -> !(alert instanceof AlertGroup))
            .forEach(alert -> {
                WatchdogPlugin.getInstance().getInjector().injectMembers(alert);
                for (INotification notification : alert.getNotifications()) {
                    WatchdogPlugin.getInstance().getInjector().injectMembers(notification);
                    notification.setAlert(alert);
                }
            });

        SwingUtilities.invokeLater(this.watchdogPanel::rebuild);
        return true;
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
                    .filter(notification -> notification instanceof IAudioNotification)
                    .map(notification -> (IAudioNotification) notification)
                    .forEach(sound -> sound.setGain(Util.scale(sound.getGain(), -25, 5, 0, 10)));
            }

            if (configVersion.compareTo(new Version("2.8.0")) < 0) {
                this.alerts.stream()
                    .flatMap(alert -> alert.getNotifications().stream())
                    .filter(notification -> notification instanceof ScreenFlash)
                    .map(notification -> (ScreenFlash) notification)
                    .forEach(screenFlash -> {
                        FlashNotification oldEnum = screenFlash.getFlashNotification();
                        screenFlash.setFlashMode((oldEnum == FlashNotification.SOLID_TWO_SECONDS || oldEnum == FlashNotification.SOLID_UNTIL_CANCELLED) ? FlashMode.SOLID : FlashMode.FLASH);
                        screenFlash.setFlashDuration((oldEnum == FlashNotification.FLASH_TWO_SECONDS || oldEnum == FlashNotification.SOLID_TWO_SECONDS) ? 2 : 0);
                        screenFlash.setFlashNotification(null);
                    });
            }

            if (configVersion.compareTo(new Version("2.13.0")) < 0) {
                this.alerts.stream()
                    .flatMap(alert -> alert.getNotifications().stream())
                    .filter(notification -> notification instanceof Overlay)
                    .map(notification -> (Overlay) notification)
                    .forEach(overlay -> {
                        if (overlay.getTextColor() == null) {
                            overlay.setTextColor(WatchdogConfig.DEFAULT_NOTIFICATION_TEXT_COLOR);
                        }
                    });
            }

            this.configManager.setConfiguration(WatchdogConfig.CONFIG_GROUP_NAME, WatchdogConfig.PLUGIN_VERSION, currentVersion.getVersion());
            this.saveAlerts();
        }
    }
}
