package com.adamk33n3r.runelite.watchdog;

import com.adamk33n3r.runelite.watchdog.alerts.Alert;
import com.adamk33n3r.runelite.watchdog.alerts.ChatAlert;
import com.adamk33n3r.runelite.watchdog.alerts.NotificationFiredAlert;
import com.adamk33n3r.runelite.watchdog.alerts.SoundFiredAlert;
import com.adamk33n3r.runelite.watchdog.alerts.StatDrainAlert;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.Skill;
import net.runelite.api.events.AreaSoundEffectPlayed;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.SoundEffectPlayed;
import net.runelite.api.events.StatChanged;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.NotificationFired;
import net.runelite.client.util.Text;

import javax.inject.Inject;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class EventHandler {
    @Inject
    Client client;

    @Inject
    AlertManager alertManager;

    private final Map<Alert, Instant> lastTriggered = new HashMap<>();

    private final int[] previousLevels = new int[Skill.values().length];

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
        int[] boostedSkillLevels = this.client.getBoostedSkillLevels();
        for (Skill skill : Skill.values()) {
            this.previousLevels[skill.ordinal()] = boostedSkillLevels[skill.ordinal()];
        }
    }
    @Subscribe
    public void onStatChanged(StatChanged statChanged) {
//        log.debug(String.format("%s: %s/%s", statChanged.getSkill().getName(), statChanged.getBoostedLevel(), statChanged.getLevel()));
        int previousLevel = this.previousLevels[statChanged.getSkill().ordinal()];
        this.alertManager.getAlerts().stream()
            .filter(alert -> alert instanceof StatDrainAlert)
            .map(alert -> (StatDrainAlert) alert)
            .filter(alert -> {
                boolean isSkill = alert.getSkill() == statChanged.getSkill();
                int targetLevel = statChanged.getLevel() - alert.getDrainAmount();
                boolean isLower = statChanged.getBoostedLevel() <= targetLevel;
                boolean wasHigher = previousLevel > targetLevel;
//                log.debug("targetLevel: " + targetLevel);
//                log.debug("{}, {}, {}", isSkill, isLower, wasHigher);
                return isSkill && isLower && wasHigher;
            })
            .forEach(alert -> this.fireAlert(alert, statChanged.getSkill().getName()));

        int[] boostedSkillLevels = this.client.getBoostedSkillLevels();
        for (Skill skill : Skill.values()) {
            this.previousLevels[skill.ordinal()] = boostedSkillLevels[skill.ordinal()];
        }
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
            this.lastTriggered.put(alert, Instant.now());
            alert.getNotifications().forEach(notification -> notification.fire(triggerValues));
        }
    }
}
