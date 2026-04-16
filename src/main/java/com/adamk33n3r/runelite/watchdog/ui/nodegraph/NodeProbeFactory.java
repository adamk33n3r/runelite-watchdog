package com.adamk33n3r.runelite.watchdog.ui.nodegraph;

import com.adamk33n3r.nodegraph.Node;
import com.adamk33n3r.nodegraph.nodes.ActionNode;
import com.adamk33n3r.nodegraph.nodes.TriggerNode;
import com.adamk33n3r.runelite.watchdog.NotificationType;
import com.adamk33n3r.runelite.watchdog.TriggerType;
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
    private final Map<Enum<?>, Supplier<Node>> probes = new IdentityHashMap<>();

    @Inject
    public NodeProbeFactory(Injector injector) {
        for (var tt : TriggerType.values()) {
            this.probes.put(tt, () -> new TriggerNode(injector.getInstance(tt.getImplClass())));
        }
        for (var nt : NotificationType.values()) {
            this.probes.put(nt, () -> new ActionNode(injector.getInstance(nt.getImplClass())));
        }
        for (var lnt : LogicNodeType.values()) {
            this.probes.put(lnt, () -> injector.getInstance(lnt.getImplClass()));
        }
        for (var mnt : MathNodeType.values()) {
            this.probes.put(mnt, () -> injector.getInstance(mnt.getImplClass()));
        }
//        for (var vnt : VariableNodeType.values()) {
//            PROBES.put(vnt, () -> injector.getInstance(vnt.getImplClass()));
//        }
    }

    @Nullable
    public Node create(Enum<?> nodeType) {
        Supplier<Node> factory = this.probes.get(nodeType);
        if (factory != null) {
            return factory.get();
        }
        return null;
    }
}
