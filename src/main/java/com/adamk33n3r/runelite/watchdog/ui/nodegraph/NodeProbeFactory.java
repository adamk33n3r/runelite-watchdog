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

import javax.annotation.Nullable;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Maps each node-type enum entry to a lightweight probe {@link Node} supplier.
 * The probe is used to inspect {@link Node#getInputs()} for type compatibility during popup filtering —
 * no Guice injection needed since we only read var metadata, not invoke plugin services.
 *
 * <p><b>Sync rule:</b> Every new popup-eligible node type must be registered here,
 * the same way new Node subclasses must be registered in {@code GraphSerializer}.
 *
 * <p>{@code VariableNodeType} entries are intentionally omitted: Bool/Num are source-only nodes
 * whose panels do not expose {@code ConnectionPointIn} instances.
 * {@code TriggerType.ALERT_GROUP} and {@code TriggerType.ADVANCED_ALERT} are also omitted
 * (they are already filtered from the popup by existing exclusion logic).
 */
public class NodeProbeFactory {

    private static final Map<Enum<?>, Supplier<Node>> PROBES = new IdentityHashMap<>();

    static {
        // NotificationType — per-entry so future notification-specific VarInputs are reflected
        PROBES.put(NotificationType.GAME_MESSAGE,        () -> new NotificationNode(new GameMessage()));
        PROBES.put(NotificationType.SCREEN_FLASH,        () -> new NotificationNode(new ScreenFlash()));
        PROBES.put(NotificationType.SOUND_EFFECT,        () -> new NotificationNode(new SoundEffect()));
        PROBES.put(NotificationType.SOUND,               () -> new NotificationNode(new Sound()));
        PROBES.put(NotificationType.TEXT_TO_SPEECH,      () -> new NotificationNode(new TextToSpeech()));
        PROBES.put(NotificationType.TRAY_NOTIFICATION,   () -> new NotificationNode(new TrayNotification()));
        PROBES.put(NotificationType.OVERHEAD,            () -> new NotificationNode(new Overhead()));
        PROBES.put(NotificationType.OVERLAY,             () -> new NotificationNode(new Overlay()));
        PROBES.put(NotificationType.POPUP,               () -> new NotificationNode(new Popup()));
        // SCREEN_MARKER omitted: its constructor requires WatchdogConfig injection;
        // NodeProbeFactory.create() falls back to NOTIFICATION_FALLBACK for it.
        PROBES.put(NotificationType.OBJECT_MARKER,       () -> new NotificationNode(new ObjectMarker()));
        PROBES.put(NotificationType.DINK,                () -> new NotificationNode(new Dink()));
        PROBES.put(NotificationType.COUNTER,             () -> new NotificationNode(new Counter()));
        PROBES.put(NotificationType.SHORTEST_PATH,       () -> new NotificationNode(new ShortestPath()));
        PROBES.put(NotificationType.ALERT_TOGGLE,        () -> new NotificationNode(new AlertToggle()));
        PROBES.put(NotificationType.PLUGIN_MESSAGE,      () -> new NotificationNode(new PluginMessage()));
        PROBES.put(NotificationType.PLUGIN_TOGGLE,       () -> new NotificationNode(new PluginToggle()));
        PROBES.put(NotificationType.DISMISS_OVERLAY,     () -> new NotificationNode(new DismissOverlay()));
        PROBES.put(NotificationType.DISMISS_SCREEN_MARKER,  () -> new NotificationNode(new DismissScreenMarker()));
        PROBES.put(NotificationType.DISMISS_OBJECT_MARKER,  () -> new NotificationNode(new DismissObjectMarker()));
        PROBES.put(NotificationType.REQUEST_FOCUS,       () -> new NotificationNode(new RequestFocus()));
        PROBES.put(NotificationType.NOTIFICATION_EVENT,  () -> new NotificationNode(new NotificationEvent()));

        // TriggerType — per-entry for the same reason; ALERT_GROUP and ADVANCED_ALERT intentionally omitted
        PROBES.put(TriggerType.GAME_MESSAGE,         () -> new TriggerNode(new ChatAlert()));
        PROBES.put(TriggerType.PLAYER_CHAT_MESSAGE,  () -> new TriggerNode(new PlayerChatAlert()));
        PROBES.put(TriggerType.OVERHEAD_TEXT,        () -> new TriggerNode(new OverheadTextAlert()));
        PROBES.put(TriggerType.STAT_CHANGED,         () -> new TriggerNode(new StatChangedAlert()));
        PROBES.put(TriggerType.XP_DROP,              () -> new TriggerNode(new XPDropAlert()));
        PROBES.put(TriggerType.SOUND_FIRED,          () -> new TriggerNode(new SoundFiredAlert()));
        PROBES.put(TriggerType.SPAWNED_OBJECT,       () -> new TriggerNode(new SpawnedAlert()));
        PROBES.put(TriggerType.INVENTORY,            () -> new TriggerNode(new InventoryAlert()));
        PROBES.put(TriggerType.LOCATION,             () -> new TriggerNode(new LocationAlert()));
        PROBES.put(TriggerType.NOTIFICATION_FIRED,   () -> new TriggerNode(new NotificationFiredAlert()));

        // LogicNodeType
        PROBES.put(LogicNodeType.BOOLEAN,  BooleanGate::new);
        PROBES.put(LogicNodeType.EQUALITY, Equality::new);
    }

    // Fallback for NotificationType entries whose notification class requires Guice injection to construct
    // (e.g. ScreenMarker requires WatchdogConfig). All NotificationNodes share identical VarInputs for now,
    // so this is semantically correct. When notification-specific VarInputs are added, the notification class
    // will also need a Guice-free constructor path.
    private static final Supplier<Node> NOTIFICATION_FALLBACK = () -> new NotificationNode(new ScreenFlash());

    @Nullable
    public static Node create(Enum<?> nodeType) {
        Supplier<Node> factory = PROBES.get(nodeType);
        if (factory != null) {
            return factory.get();
        }
        // Any NotificationType entry without a specific probe uses the shared fallback
        if (nodeType instanceof NotificationType) {
            return NOTIFICATION_FALLBACK.get();
        }
        return null;
    }
}
