package com.adamk33n3r.runelite.watchdog.alerts;

import com.adamk33n3r.nodegraph.nodes.NotificationNode;
import com.adamk33n3r.nodegraph.nodes.TriggerNode;
import com.adamk33n3r.runelite.watchdog.TriggerType;
import com.adamk33n3r.runelite.watchdog.WatchdogPlugin;
import com.adamk33n3r.runelite.watchdog.notifications.MessageNotification;
import com.adamk33n3r.runelite.watchdog.notifications.Notification;

import lombok.*;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Getter
@Setter
@Accessors(chain = true)
public abstract class Alert {
    private boolean enabled = true;
    private String name;
    private int debounceTime;
    private boolean debounceResetTime = false;
    private boolean randomNotifications = false;
    private AlertMode alertMode = AlertMode.MULTI;

    @Nullable
    private transient AlertGroup parent;
    public @Nullable AlertGroup getParent() {
        if (this.parent == null) {
            this.parent = WatchdogPlugin.getInstance()
                .getAlertManager()
                .getAllAlertGroups()
                .filter(alertGroup -> alertGroup.getAlerts().contains(this))
                .findFirst()
                .orElse(null);
        }

        return this.parent;
    }

    @Nullable
    @Setter(AccessLevel.PROTECTED)
    private List<Notification> notifications = new ArrayList<>();

    public Alert addNotification(Notification notification) {
        if (this.notifications != null) {
            this.notifications.add(notification);
        }
        return this;
    }

    public Alert addNotifications(Notification... notifications) {
        if (this.notifications != null) {
            this.notifications.addAll(Arrays.asList(notifications));
        }
        return this;
    }

    public Alert(String name) {
        this.name = name;
        this.debounceTime = 0;
    }

    public TriggerType getType() {
        return Arrays.stream(TriggerType.values())
            .filter(tType -> tType.getImplClass() == this.getClass())
            .findFirst()
            .orElse(null);
    }

    public void moveNotificationTo(Notification notification, int pos) {
        if (this.notifications == null) {
            return;
        }
        this.notifications.remove(notification);
        this.notifications.add(pos, notification);
    }

    public void moveNotificationToTop(Notification notification) {
        if (this.notifications == null) {
            return;
        }
        this.notifications.remove(notification);
        this.notifications.add(0, notification);
    }

    public void moveNotificationToBottom(Notification notification) {
        if (this.notifications == null) {
            return;
        }
        this.notifications.remove(notification);
        this.notifications.add(notification);
    }

    public void moveNotificationUp(Notification notification) {
        if (this.notifications == null) {
            return;
        }
        int curIdx = this.notifications.indexOf(notification);
        int newIdx = curIdx - 1;

        if (newIdx < 0) {
            return;
        }

        this.notifications.remove(notification);
        this.notifications.add(newIdx, notification);
    }

    public void moveNotificationDown(Notification notification) {
        if (this.notifications == null) {
            return;
        }
        int curIdx = this.notifications.indexOf(notification);
        int newIdx = curIdx + 1;

        if (newIdx >= this.notifications.size()) {
            return;
        }

        this.notifications.remove(notification);
        this.notifications.add(newIdx, notification);
    }

    @Nullable
    public List<AlertGroup> getAncestors() {
        if (this.getParent() == null) {
            return null;
        }

        ArrayList<AlertGroup> ancestors = new ArrayList<>();
        AlertGroup alertGroup = this.getParent();
        do {
            ancestors.add(0, alertGroup);
        } while ((alertGroup = alertGroup.getParent()) != null);

        return ancestors;
    }

    public List<String> getKeywords() {
        Stream<String> selfKeywords = Stream.of(
            this.getName(),
            this.getType().getName()
        );

        if (this instanceof AlertGroup) {
            return Stream.concat(selfKeywords, ((AlertGroup) this).getAlerts().stream().flatMap(alert -> alert.getKeywords().stream()))
                .collect(Collectors.toList());
        } else if (this instanceof AdvancedAlert) {
            return Stream.concat(selfKeywords, ((AdvancedAlert) this).getGraph().getNodes().stream().flatMap(node -> {
                if (node instanceof TriggerNode) {
                    return ((TriggerNode) node).getAlert().getKeywords().stream();
                } else if (node instanceof NotificationNode) {
                    Notification notification = ((NotificationNode) node).getNotification();
                    if (notification instanceof MessageNotification) {
                        return Stream.of(notification.getType().getName(), ((MessageNotification) notification).getMessage());
                    }
                    return Stream.of(notification.getType().getName());
                }
                return Stream.empty();
            })).collect(Collectors.toList());
        } else {
            if (this.getNotifications() == null) {
                log.error("Could not get notifications for keywords");
                return selfKeywords.collect(Collectors.toList());
            }
            return Stream.concat(
                selfKeywords,
                this.getNotifications().stream()
                    .flatMap(notification -> {
                        if (notification instanceof MessageNotification) {
                            return Stream.of(notification.getType().getName(), ((MessageNotification) notification).getMessage());
                        }
                        return Stream.of(notification.getType().getName());
                    }))
                .collect(Collectors.toList());
        }
    }
}
