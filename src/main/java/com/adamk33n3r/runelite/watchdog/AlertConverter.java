package com.adamk33n3r.runelite.watchdog;

import com.adamk33n3r.nodegraph.ExecSignal;
import com.adamk33n3r.nodegraph.Graph;
import com.adamk33n3r.nodegraph.VarOutput;
import com.adamk33n3r.nodegraph.nodes.ActionNode;
import com.adamk33n3r.nodegraph.nodes.ActionNodeFactory;
import com.adamk33n3r.nodegraph.nodes.TriggerNode;
import com.adamk33n3r.nodegraph.nodes.flow.DelayNode;
import com.adamk33n3r.runelite.watchdog.alerts.AdvancedAlert;
import com.adamk33n3r.runelite.watchdog.alerts.Alert;
import com.adamk33n3r.runelite.watchdog.alerts.AlertGroup;
import com.adamk33n3r.runelite.watchdog.notifications.Notification;
import com.adamk33n3r.runelite.watchdog.serialization.WatchdogGsonFactory;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Singleton
public class AlertConverter {

    static final int NODE_WIDTH = 300;
    static final int NODE_H_GAP = 80;
    static final int NODE_H_STEP = NODE_WIDTH + NODE_H_GAP;
    static final int NODE_ROW_HEIGHT = 300;
    static final int NODE_V_GAP = 60;
    static final int NODE_ROW_STEP = NODE_ROW_HEIGHT + NODE_V_GAP;
    static final int START_X = 50;
    static final int START_Y = 50;

    private final Gson gson;

    @Inject
    public AlertConverter(Gson gson) {
        this.gson = WatchdogGsonFactory.create(gson);
    }

    public static boolean hasNestedGroups(Alert alert) {
        if (!(alert instanceof AlertGroup)) return false;
        return ((AlertGroup) alert).getAlerts().stream().anyMatch(a -> a instanceof AlertGroup);
    }

    public AdvancedAlert convert(Alert source) {
        AdvancedAlert result = new AdvancedAlert(source.getName() + " (Advanced)");
        result.setEnabled(source.isEnabled());
        result.setDebounceTime(source.getDebounceTime());
        result.setDebounceResetTime(source.isDebounceResetTime());

        Graph graph = result.getGraph();
        List<Alert> leafAlerts = flattenAlerts(source);

        for (int row = 0; row < leafAlerts.size(); row++) {
            Alert alert = leafAlerts.get(row);
            int rowY = START_Y + row * NODE_ROW_STEP;

            TriggerNode triggerNode = new TriggerNode(deepCopyAlert(alert));
            triggerNode.setX(START_X);
            triggerNode.setY(rowY);
            graph.add(triggerNode);

            int col = 1;
            VarOutput<ExecSignal> prevExecOut = triggerNode.getExec();

            List<Notification> notifications = alert.getNotifications();
            if (notifications != null) {
                for (Notification notification : notifications) {
                    int nodeX = START_X + col * NODE_H_STEP;

                    if (notification.getDelayMilliseconds() > 0) {
                        DelayNode delayNode = new DelayNode();
                        delayNode.getDelayMs().setValue(notification.getDelayMilliseconds());
                        delayNode.setX(nodeX);
                        delayNode.setY(rowY);
                        graph.add(delayNode);
                        graph.connect(prevExecOut, delayNode.getExec());
                        prevExecOut = delayNode.getExecOut();
                        col++;
                        nodeX = START_X + col * NODE_H_STEP;
                    }

                    Notification notifCopy = deepCopyNotification(notification);
                    notifCopy.setDelayMilliseconds(0);
                    ActionNode actionNode = ActionNodeFactory.create(notifCopy);
                    actionNode.setX(nodeX);
                    actionNode.setY(rowY);
                    graph.add(actionNode);
                    graph.connect(prevExecOut, actionNode.getExec());
                    prevExecOut = actionNode.getExecOut();
                    col++;
                }
            }
        }

        return result;
    }

    private List<Alert> flattenAlerts(Alert alert) {
        if (alert instanceof AlertGroup) {
            List<Alert> result = new ArrayList<>();
            for (Alert child : ((AlertGroup) alert).getAlerts()) {
                result.addAll(flattenAlerts(child));
            }
            return result;
        }
        if (alert instanceof AdvancedAlert) {
            return Collections.emptyList();
        }
        return Collections.singletonList(alert);
    }

    private Alert deepCopyAlert(Alert alert) {
        return this.gson.fromJson(this.gson.toJsonTree(alert, Alert.class), Alert.class);
    }

    private Notification deepCopyNotification(Notification notification) {
        return this.gson.fromJson(this.gson.toJsonTree(notification, Notification.class), Notification.class);
    }
}
