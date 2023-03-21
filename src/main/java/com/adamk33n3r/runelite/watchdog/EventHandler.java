package com.adamk33n3r.runelite.watchdog;

import com.adamk33n3r.runelite.watchdog.alerts.Alert;
import com.adamk33n3r.runelite.watchdog.alerts.ChatAlert;
import com.adamk33n3r.runelite.watchdog.alerts.NotificationFiredAlert;
import com.adamk33n3r.runelite.watchdog.alerts.SoundFiredAlert;
import com.adamk33n3r.runelite.watchdog.alerts.StatChangedAlert;
import com.adamk33n3r.runelite.watchdog.alerts.XPDropAlert;
import com.adamk33n3r.runelite.watchdog.ui.panels.HistoryPanel;

import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.Skill;
import net.runelite.api.events.AreaSoundEffectPlayed;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.SoundEffectPlayed;
import net.runelite.api.events.StatChanged;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.NotificationFired;
import net.runelite.client.util.Text;

import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import javax.swing.SwingUtilities;
import java.awt.TrayIcon;
import java.time.Instant;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Singleton
public class EventHandler {
    @Inject
    private Client client;

    @Inject
    private AlertManager alertManager;

    @Inject
    private EventBus eventBus;

    @Inject
    private Provider<HistoryPanel> historyPanelProvider;

    private final Map<Alert, Instant> lastTriggered = new HashMap<>();

    private final Map<Skill, Integer> previousSkillLevelTable = new EnumMap<>(Skill.class);
    private final Map<Skill, Integer> previousSkillXPTable = new EnumMap<>(Skill.class);

    private boolean ignoreNotificationFired = false;

    public synchronized void notify(String message) {
        this.ignoreNotificationFired = true;
        // The event bus is synchronous
        this.eventBus.post(new NotificationFired(message, TrayIcon.MessageType.NONE));
        this.ignoreNotificationFired = false;
    }

    //region Chat Message
    @Subscribe
    public void onChatMessage(ChatMessage chatMessage) {
        // Don't process messages sent by this plugin
        if (chatMessage.getName().equals(WatchdogPlugin.getInstance().getName())) {
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

        String unformattedMessage = Text.removeFormattingTags(chatMessage.getMessage());
        this.alertManager.getAlerts().stream()
            .filter(alert -> alert instanceof ChatAlert)
            .map(alert -> (ChatAlert) alert)
//            .filter(chatAlert -> chatAlert.getChatMessageType() == chatMessage.getType())
            .forEach(alert -> {
                String regex = alert.isRegexEnabled() ? alert.getMessage() : Util.createRegexFromGlob(alert.getMessage());
                Matcher matcher = Pattern.compile(regex, alert.isRegexEnabled() ? 0 : Pattern.CASE_INSENSITIVE).matcher(unformattedMessage);
                if (!matcher.matches()) return;

                String[] groups = new String[matcher.groupCount()];
                for (int i = 0; i < matcher.groupCount(); i++) {
                    groups[i] = matcher.group(i+1);
                }
                this.fireAlert(alert, groups);
            });
    }
    //endregion

    //region Notification
    @Subscribe
    public void onNotificationFired(NotificationFired notificationFired) {
        // This flag is set when we are firing our own events, so we don't cause an infinite loop/stack overflow
        if (this.ignoreNotificationFired) {
            return;
        }

        this.alertManager.getAlerts().stream()
            .filter(alert -> alert instanceof NotificationFiredAlert)
            .map(alert -> (NotificationFiredAlert) alert)
            .forEach(alert -> {
                String regex = alert.isRegexEnabled() ? alert.getMessage() : Util.createRegexFromGlob(alert.getMessage());
                Matcher matcher = Pattern.compile(regex, alert.isRegexEnabled() ? 0 : Pattern.CASE_INSENSITIVE).matcher(notificationFired.getMessage());
                if (!matcher.matches()) return;

                String[] groups = new String[matcher.groupCount()];
                for (int i = 0; i < matcher.groupCount(); i++) {
                    groups[i] = matcher.group(i + 1);
                }
                this.fireAlert(alert, groups);
            });
    }
    //endregion

    //region Stat Changed
    @Subscribe
    private void onGameStateChanged(GameStateChanged gameStateChanged) {
        if (gameStateChanged.getGameState() == GameState.LOGGED_IN) {
            for (Skill skill : Skill.values()) {
                this.previousSkillLevelTable.put(skill, this.client.getBoostedSkillLevel(skill));
                this.previousSkillXPTable.put(skill, this.client.getSkillExperience(skill));
            }
        }
    }

    @Subscribe
    public void onStatChanged(StatChanged statChanged) {
//        log.debug(String.format("%s: %s/%s", statChanged.getSkill().getName(), statChanged.getBoostedLevel(), statChanged.getLevel()));
        this.handleStatChanged(statChanged);
        this.handleXPDrop(statChanged);
    }

    private void handleStatChanged(StatChanged statChanged) {
        Integer previousLevel = this.previousSkillLevelTable.put(statChanged.getSkill(), statChanged.getBoostedLevel());
        if (previousLevel == null) {
            return;
        }

        this.alertManager.getAlerts().stream()
            .filter(alert -> alert instanceof StatChangedAlert)
            .map(alert -> (StatChangedAlert) alert)
            .filter(alert -> {
                boolean isSkill = alert.getSkill() == statChanged.getSkill();
                if (!isSkill) {
                    return false;
                }

                int targetLevel = statChanged.getLevel() + alert.getChangedAmount();
                boolean isNegative = alert.getChangedAmount() < 0;
                boolean isLower = statChanged.getBoostedLevel() <= targetLevel;
                boolean wasHigher = previousLevel > targetLevel;
                boolean isHigher = statChanged.getBoostedLevel() >= targetLevel;
                boolean wasLower = previousLevel < targetLevel;
//                log.debug("targetLevel: " + targetLevel);
//                log.debug("{}, {}, {}", isSkill, isLower, wasHigher);
                return (isNegative && isLower && wasHigher) || (!isNegative && isHigher && wasLower);
            })
            .forEach(alert -> this.fireAlert(alert, statChanged.getSkill().getName()));
    }

    private void handleXPDrop(StatChanged statChanged) {
        Integer previousXP = this.previousSkillXPTable.put(statChanged.getSkill(), statChanged.getXp());
        if (previousXP == null) {
            return;
        }

        this.alertManager.getAlerts().stream()
            .filter(alert -> alert instanceof XPDropAlert)
            .map(alert -> (XPDropAlert) alert)
            .filter(alert -> {
                boolean isSkill = alert.getSkill() == statChanged.getSkill();
                int gainedXP = statChanged.getXp() - previousXP;
                return isSkill && gainedXP >= alert.getGainedAmount();
            })
            .forEach(alert -> this.fireAlert(alert, statChanged.getSkill().getName()));
    }
    //endregion

    //region Sound Effects
    @Subscribe
    private void onSoundEffectPlayed(SoundEffectPlayed soundEffectPlayed) {
        this.handleSoundEffectPlayed(soundEffectPlayed.getSoundId());
    }

    @Subscribe
    private void onAreaSoundEffectPlayed(AreaSoundEffectPlayed areaSoundEffectPlayed) {
        this.handleSoundEffectPlayed(areaSoundEffectPlayed.getSoundId());
    }

    private void handleSoundEffectPlayed(int soundID) {
        this.alertManager.getAlerts().stream()
            .filter(alert -> alert instanceof SoundFiredAlert)
            .map(alert -> (SoundFiredAlert) alert)
            .filter(soundFiredAlert -> soundID == soundFiredAlert.getSoundID())
            .forEach(alert -> this.fireAlert(alert, "" + soundID));
    }
    //endregion

    private void fireAlert(Alert alert, String triggerValue) {
       this.fireAlert(alert, new String[] { triggerValue });
    }

    private void fireAlert(Alert alert, String[] triggerValues) {
        // Don't fire if it is disabled
        if (!alert.isEnabled()) return;

        // If the alert hasn't been fired yet, or has been enough time, set the last trigger time to now and fire.
        if (!this.lastTriggered.containsKey(alert) || Instant.now().compareTo(this.lastTriggered.get(alert).plusMillis(alert.getDebounceTime())) >= 0) {
            SwingUtilities.invokeLater(() -> {
                this.historyPanelProvider.get().addEntry(alert, triggerValues);
            });
            this.lastTriggered.put(alert, Instant.now());
            alert.getNotifications().forEach(notification -> notification.fire(triggerValues));
        }
    }
}
