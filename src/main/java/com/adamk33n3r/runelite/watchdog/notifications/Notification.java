package com.adamk33n3r.runelite.watchdog.notifications;

import com.adamk33n3r.runelite.watchdog.AlertManager;
import com.adamk33n3r.runelite.watchdog.NotificationType;
import com.adamk33n3r.runelite.watchdog.WatchdogConfig;
import com.adamk33n3r.runelite.watchdog.WatchdogPlugin;
import com.adamk33n3r.runelite.watchdog.alerts.Alert;

import net.runelite.api.Client;
import net.runelite.api.Constants;
import net.runelite.client.ui.ClientUI;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.inject.Inject;
import java.util.Arrays;

@NoArgsConstructor
public abstract class Notification implements INotification {
    @Inject
    protected transient ClientUI clientUI;

    @Inject
    protected transient Client client;

    @Inject
    protected transient AlertManager alertManager;

    @Inject
    protected transient WatchdogConfig watchdogConfig;

    @Getter @Setter
    private boolean fireWhenFocused = true;

    @Getter @Setter
    private boolean fireWhenAFK = false;
    @Getter @Setter
    private int fireWhenAFKForSeconds = 5;

    @Getter @Setter
    private int delayMilliseconds = 0;

    public boolean isDelayed() {
        return this.delayMilliseconds > 0;
    }

    @Setter
    private transient Alert alert;
    public Alert getAlert() {
        if (this.alert == null) {
            this.alert = this.alertManager.getAllAlerts()
                .filter(a -> a.getNotifications().contains(this)).findFirst().orElse(null);
        }

        return this.alert;
    }

    @Inject
    public Notification(WatchdogConfig config) {
        this.fireWhenAFK = config.defaultAFKMode();
        this.fireWhenAFKForSeconds = config.defaultAFKSeconds();
    }

    public boolean shouldFire() {
        if (WatchdogPlugin.getInstance().isInBannedArea()) {
            return false;
        }

        int afkTime = (int)Math.floor(Math.min(client.getKeyboardIdleTicks(), client.getMouseIdleTicks()) * Constants.CLIENT_TICK_LENGTH / 1000f);
        if (this.fireWhenAFK && afkTime < this.fireWhenAFKForSeconds) {
            return false;
        }
        return !this.clientUI.isFocused() || this.fireWhenFocused;
    }

    @Override
    public void fire(String[] triggerValues) {
        if (this.shouldFire()) {
            this.fireImpl(triggerValues);
        }
    }

    public void fireForced(String[] triggerValues) {
        this.fireImpl(triggerValues);
    }

    protected abstract void fireImpl(String[] triggerValues);

    public NotificationType getType() {
        return Arrays.stream(NotificationType.values())
            .filter(nType -> nType.getImplClass() == this.getClass())
            .findFirst()
            .orElse(null);
    }

    public void setDefaults() {
        this.setFireWhenAFK(this.watchdogConfig.defaultAFKMode());
        this.setFireWhenAFKForSeconds(this.watchdogConfig.defaultAFKSeconds());
    }
}
