package com.adamk33n3r.runelite.watchdog.ui.nodegraph;

import com.adamk33n3r.nodegraph.Node;
import com.adamk33n3r.nodegraph.nodes.NotificationNode;
import com.adamk33n3r.nodegraph.nodes.TriggerNode;
import com.adamk33n3r.nodegraph.nodes.logic.BooleanGate;
import com.adamk33n3r.nodegraph.nodes.logic.Equality;
import com.adamk33n3r.runelite.watchdog.NotificationType;
import com.adamk33n3r.runelite.watchdog.TriggerType;
import com.adamk33n3r.runelite.watchdog.alerts.ChatAlert;
import com.adamk33n3r.runelite.watchdog.alerts.InventoryAlert;
import com.adamk33n3r.runelite.watchdog.alerts.LocationAlert;
import com.adamk33n3r.runelite.watchdog.alerts.NotificationFiredAlert;
import com.adamk33n3r.runelite.watchdog.alerts.OverheadTextAlert;
import com.adamk33n3r.runelite.watchdog.alerts.PlayerChatAlert;
import com.adamk33n3r.runelite.watchdog.alerts.SoundFiredAlert;
import com.adamk33n3r.runelite.watchdog.alerts.SpawnedAlert;
import com.adamk33n3r.runelite.watchdog.alerts.StatChangedAlert;
import com.adamk33n3r.runelite.watchdog.alerts.XPDropAlert;
import com.adamk33n3r.runelite.watchdog.notifications.AlertToggle;
import com.adamk33n3r.runelite.watchdog.notifications.Counter;
import com.adamk33n3r.runelite.watchdog.notifications.Dink;
import com.adamk33n3r.runelite.watchdog.notifications.DismissObjectMarker;
import com.adamk33n3r.runelite.watchdog.notifications.DismissOverlay;
import com.adamk33n3r.runelite.watchdog.notifications.DismissScreenMarker;
import com.adamk33n3r.runelite.watchdog.notifications.GameMessage;
import com.adamk33n3r.runelite.watchdog.notifications.NotificationEvent;
import com.adamk33n3r.runelite.watchdog.notifications.Overhead;
import com.adamk33n3r.runelite.watchdog.notifications.Overlay;
import com.adamk33n3r.runelite.watchdog.notifications.PluginMessage;
import com.adamk33n3r.runelite.watchdog.notifications.PluginToggle;
import com.adamk33n3r.runelite.watchdog.notifications.Popup;
import com.adamk33n3r.runelite.watchdog.notifications.RequestFocus;
import com.adamk33n3r.runelite.watchdog.notifications.ScreenFlash;
import com.adamk33n3r.runelite.watchdog.notifications.ShortestPath;
import com.adamk33n3r.runelite.watchdog.notifications.Sound;
import com.adamk33n3r.runelite.watchdog.notifications.SoundEffect;
import com.adamk33n3r.runelite.watchdog.notifications.TextToSpeech;
import com.adamk33n3r.runelite.watchdog.notifications.TrayNotification;
import com.adamk33n3r.runelite.watchdog.notifications.objectmarkers.ObjectMarker;
import com.google.inject.Injector;

import javax.annotation.Nullable;
import javax.inject.Inject;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Maps each node-type enum entry to a lightweight probe {@link Node} supplier.
 * The probe is used to inspect {@link Node#getInputs()} for type compatibility during popup filtering —
 * no Guice injection needed since we only read var metadata, not invoke plugin services.
 *
 * <p>{@code VariableNodeType} entries are intentionally omitted: Bool/Num are source-only nodes
 * whose panels do not expose {@code ConnectionPointIn} instances.
 * {@code TriggerType.ALERT_GROUP} and {@code TriggerType.ADVANCED_ALERT} are also omitted
 * (they are already filtered from the popup by existing exclusion logic).
 */
public class NodeProbeFactory {
    private final Map<Enum<?>, Supplier<Node>> PROBES = new IdentityHashMap<>();

    @Inject
    public NodeProbeFactory(Injector injector) {
        for (var tt : TriggerType.values()) {
            PROBES.put(tt, () -> new TriggerNode(injector.getInstance(tt.getImplClass())));
        }
        for (var nt : NotificationType.values()) {
            PROBES.put(nt, () -> new NotificationNode(injector.getInstance(nt.getImplClass())));
        }
        for (var lnt : LogicNodeType.values()) {
            PROBES.put(lnt, () -> injector.getInstance(lnt.getImplClass()));
        }
//        for (var vnt : VariableNodeType.values()) {
//            PROBES.put(vnt, () -> injector.getInstance(vnt.getImplClass()));
//        }
    }

    @Nullable
    public Node create(Enum<?> nodeType) {
        Supplier<Node> factory = PROBES.get(nodeType);
        if (factory != null) {
            return factory.get();
        }
        return null;
    }
}
