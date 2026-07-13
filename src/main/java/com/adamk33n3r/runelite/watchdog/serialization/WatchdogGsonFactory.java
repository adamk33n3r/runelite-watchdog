package com.adamk33n3r.runelite.watchdog.serialization;

import com.adamk33n3r.nodegraph.Graph;
import com.adamk33n3r.nodegraph.GraphSerializer;
import com.adamk33n3r.nodegraph.NodeTypeRegistry;
import com.adamk33n3r.nodegraph.nodes.ActionNode;
import com.adamk33n3r.nodegraph.nodes.TriggerNode;
import com.adamk33n3r.nodegraph.nodes.constants.*;
import com.adamk33n3r.nodegraph.nodes.flow.Branch;
import com.adamk33n3r.nodegraph.nodes.flow.DelayNode;
import com.adamk33n3r.nodegraph.nodes.flow.TimerNode;
import com.adamk33n3r.nodegraph.nodes.logic.BooleanGate;
import com.adamk33n3r.nodegraph.nodes.logic.Equality;
import com.adamk33n3r.nodegraph.nodes.logic.InventoryCheck;
import com.adamk33n3r.nodegraph.nodes.logic.LocationCompare;
import com.adamk33n3r.nodegraph.nodes.math.*;
import com.adamk33n3r.nodegraph.nodes.utility.DisplayNode;
import com.adamk33n3r.nodegraph.nodes.utility.NoteNode;
import com.adamk33n3r.nodegraph.nodes.utility.ToStringNode;
import com.adamk33n3r.runelite.watchdog.MixedCaseEnumAdapter;
import com.adamk33n3r.runelite.watchdog.RuntimeTypeAdapterFactory;
import com.adamk33n3r.runelite.watchdog.alerts.*;
import com.adamk33n3r.runelite.watchdog.hub.AlertHubCategory;
import com.adamk33n3r.runelite.watchdog.notifications.*;
import com.adamk33n3r.runelite.watchdog.notifications.objectmarkers.ObjectMarker;
import com.google.gson.Gson;

import javax.inject.Singleton;

@Singleton
public class WatchdogGsonFactory {

    /**
     * Builds the fully-configured Gson for Watchdog alert/notification serialization.
     *
     * <p>The build is two-step: an intermediate Gson (with alert and notification type adapters)
     * is constructed first so that {@link GraphSerializer} can embed Alert/Notification objects
     * inside graph node JSON. The final Gson wraps that serializer on top.
     *
     * @param clientGson the base RuneLite Gson to extend
     * @return the watchdog-configured Gson instance
     */
    public static Gson create(Gson clientGson) {
        final RuntimeTypeAdapterFactory<Alert> alertTypeFactory = RuntimeTypeAdapterFactory.of(Alert.class)
            .ignoreSubtype("IdleAlert")
            .ignoreSubtype("ResourceAlert")
            .recognizeSubtypes()
            .registerSubtype(ChatAlert.class)
            .registerSubtype(PlayerChatAlert.class)
            .registerSubtype(OverheadTextAlert.class)
            .registerSubtype(NotificationFiredAlert.class)
            .registerSubtype(StatDrainAlert.class)
            .registerSubtype(StatChangedAlert.class)
            .registerSubtype(XPDropAlert.class)
            .registerSubtype(SoundFiredAlert.class)
            .registerSubtype(SpawnedAlert.class)
            .registerSubtype(InventoryAlert.class)
                .registerSubtype(BankAlert.class)
            .registerSubtype(AlertGroup.class)
            .registerSubtype(LocationAlert.class)
            .registerSubtype(AdvancedAlert.class);

        final RuntimeTypeAdapterFactory<Notification> notificationTypeFactory = RuntimeTypeAdapterFactory.of(Notification.class)
            .registerSubtype(TrayNotification.class)
            .registerSubtype(TextToSpeech.class)
            .registerSubtype(Sound.class)
            .registerSubtype(SoundEffect.class)
            .registerSubtype(ScreenFlash.class)
            .registerSubtype(GameMessage.class)
            .registerSubtype(Overhead.class)
            .registerSubtype(Overlay.class)
            .registerSubtype(Popup.class)
            .registerSubtype(RequestFocus.class)
            .registerSubtype(NotificationEvent.class)
            .registerSubtype(ScreenMarker.class)
            .registerSubtype(ObjectMarker.class)
            .registerSubtype(Dink.class)
            .registerSubtype(Counter.class)
            .registerSubtype(ShortestPath.class)
            .registerSubtype(PluginMessage.class)
            .registerSubtype(PluginToggle.class)
            .registerSubtype(AlertToggle.class)
            .registerSubtype(DismissObjectMarker.class)
            .registerSubtype(DismissOverlay.class)
            .registerSubtype(DismissScreenMarker.class);

        NodeTypeRegistry nodeRegistry = new NodeTypeRegistry()
            .registerSubtype(TriggerNode.class,
                (json, gson) -> new TriggerNode(gson.fromJson(json.get("alert"), Alert.class)),
                (node, obj, gson) -> obj.add("alert", gson.toJsonTree(node.getAlert(), Alert.class)))
            .registerSubtype(ActionNode.class,
                (json, gson) -> new ActionNode(gson.fromJson(json.get("notification"), Notification.class)),
                (node, obj, gson) -> obj.add("notification", gson.toJsonTree(node.getNotification(), Notification.class)))
            .registerAlias("NotificationNode", ActionNode.class)
            .registerSubtype(Add.class, Add::new)
            .registerSubtype(Subtract.class, Subtract::new)
            .registerSubtype(Multiply.class, Multiply::new)
            .registerSubtype(Divide.class, Divide::new)
            .registerSubtype(Min.class, Min::new)
            .registerSubtype(Max.class, Max::new)
            .registerSubtype(Clamp.class, Clamp::new)
            .registerSubtype(Floor.class, Floor::new)
            .registerSubtype(Ceiling.class, Ceiling::new)
            .registerSubtype(Round.class, Round::new)
            .registerSubtype(BooleanGate.class, BooleanGate::new)
            .registerSubtype(Equality.class, Equality::new)
            .registerSubtype(Bool.class, Bool::new)
            .registerSubtype(Num.class, Num::new)
            .registerSubtype(Location.class, Location::new)
            .registerSubtype(Inventory.class, Inventory::new)
            .registerSubtype(PluginState.class, PluginState::new)
            .registerAlias("PluginVar", PluginState.class)
            .registerSubtype(InventoryCheck.class, InventoryCheck::new)
            .registerAlias("InventoryVar", InventoryCheck.class)
            .registerSubtype(LocationCompare.class, LocationCompare::new)
            .registerSubtype(DelayNode.class, DelayNode::new)
            .registerAlias("Delay", DelayNode.class)
            .registerSubtype(com.adamk33n3r.nodegraph.nodes.flow.Counter.class, com.adamk33n3r.nodegraph.nodes.flow.Counter::new)
            .registerSubtype(TimerNode.class, TimerNode::new)
            .registerAlias("Timer", TimerNode.class)
            .registerSubtype(Branch.class, Branch::new)
            .registerSubtype(DisplayNode.class, DisplayNode::new)
            .registerSubtype(NoteNode.class, NoteNode::new)
            .registerSubtype(ToStringNode.class, ToStringNode::new);

        Gson intermediateGson = clientGson.newBuilder()
            .registerTypeAdapterFactory(alertTypeFactory)
            .registerTypeAdapterFactory(notificationTypeFactory)
            .registerTypeAdapter(AlertHubCategory.class, new MixedCaseEnumAdapter())
            .create();

        GraphSerializer graphSerializer = new GraphSerializer(intermediateGson, nodeRegistry);

        return intermediateGson.newBuilder()
            .registerTypeAdapter(Graph.class, graphSerializer)
            .create();
    }
}
