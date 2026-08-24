package com.adamk33n3r.nodegraph.nodes;

import com.adamk33n3r.runelite.watchdog.notifications.MessageNotification;
import com.adamk33n3r.runelite.watchdog.notifications.Notification;

public final class ActionNodeFactory {
    private ActionNodeFactory() {}

    public static ActionNode create(Notification notification) {
        if (notification instanceof MessageNotification) {
            return new MessageActionNode((MessageNotification) notification);
        }

        return new ActionNode(notification);
    }
}
