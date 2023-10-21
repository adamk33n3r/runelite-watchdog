package com.adamk33n3r.runelite.watchdog;

import com.adamk33n3r.runelite.watchdog.alerts.*;
import com.adamk33n3r.runelite.watchdog.hub.AlertHubCategory;
import com.adamk33n3r.runelite.watchdog.notifications.*;
import com.adamk33n3r.runelite.watchdog.ui.panels.PanelUtils;

import net.runelite.client.config.ConfigManager;
import net.runelite.client.config.FlashNotification;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
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
import java.util.Objects;
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

    // TODO: Kinda weird this is in here...
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
            .recognizeSubtypes()
            .registerSubtype(ChatAlert.class)
            .registerSubtype(PlayerChatAlert.class)
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
            .registerTypeAdapter(AlertHubCategory.class, new MixedCaseEnumAdapter())
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
        return this.getAllAlertsFrom(this.alerts.stream(), false);
    }

    public <T extends Alert> Stream<T> getAllAlertsOfType(Class<T> type) {
        return this.getAllAlerts()
            .filter(type::isInstance)
            .map(type::cast);
    }

    public Stream<AlertGroup> getAllAlertGroups() {
        return this.getAllAlertsFrom(this.alerts.stream(), true)
            .filter(AlertGroup.class::isInstance)
            .map(AlertGroup.class::cast);
    }

    public Stream<Alert> getAllAlertsFrom(Stream<Alert> alerts, boolean includeGroups) {
        return alerts.flatMap(alert -> {
            if (alert instanceof AlertGroup) {
                Stream<Alert> children = this.getAllAlertsFrom(((AlertGroup) alert).getAlerts().stream(), includeGroups);
                if (includeGroups) {
                    return Stream.concat(Stream.of(alert), children);
                }
                return children;
            }
            return Stream.of(alert);
        });
    }

    public void addAlert(Alert alert) {
        this.alerts.add(alert);
        this.setUpAlert(alert);
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

    public boolean importAlerts(String json, List<Alert> alerts, boolean append, boolean checkRegex) throws JsonSyntaxException {
        if (Strings.isNullOrEmpty(json)) {
            return false;
        }

        if (!append) {
            alerts.clear();
        }

        Supplier<Stream<Alert>> alertStream = this.tryImport(json);

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

        // Inject dependencies
        this.getAllAlertsFrom(alertStream.get(), false)
            .forEach(this::setUpAlert);

        SwingUtilities.invokeLater(() -> {
            this.watchdogPanel.rebuild();
            SwingUtilities.invokeLater(this.watchdogPanel::scrollToBottom);
        });
        return true;
    }

    public void saveAlerts() {
        String json = this.gson.toJson(this.alerts, ALERT_LIST_TYPE);
        this.configManager.setConfiguration(WatchdogConfig.CONFIG_GROUP_NAME, WatchdogConfig.ALERTS, json);
    }

    public String toJSON() {
        return this.gson.toJson(this.alerts, ALERT_LIST_TYPE);
    }

    private Supplier<Stream<Alert>> tryImport(String json) throws JsonSyntaxException {
        // Single
        try {
            Alert importedAlert = this.gson.fromJson(json, ALERT_TYPE);
            return () -> Stream.of(importedAlert).filter(Objects::nonNull);
        } catch (JsonSyntaxException ignored) {
        }

        // Multiple
        List<Alert> importedAlerts = this.gson.fromJson(json, ALERT_LIST_TYPE);
        return () -> importedAlerts.stream().filter(Objects::nonNull);
    }

    private void setUpAlert(Alert alert) {
        WatchdogPlugin.getInstance().getInjector().injectMembers(alert);
        if (alert instanceof AlertGroup) {
            ((AlertGroup) alert).getAlerts().forEach(this::setUpAlert);
        } else {
            for (INotification notification : alert.getNotifications()) {
                WatchdogPlugin.getInstance().getInjector().injectMembers(notification);
                notification.setAlert(alert);
            }
        }
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
                log.debug("Need to convert flash notifications to new properties");
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
                log.debug("Need to set default overlay notification text color");
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
