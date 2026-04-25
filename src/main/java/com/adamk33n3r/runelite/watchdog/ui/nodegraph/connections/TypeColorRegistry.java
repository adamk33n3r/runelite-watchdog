package com.adamk33n3r.runelite.watchdog.ui.nodegraph.connections;

import com.adamk33n3r.runelite.watchdog.InventoryItemData.InventoryItemDataMap;
import net.runelite.api.coords.WorldPoint;

import java.awt.Color;
import java.util.LinkedHashMap;
import java.util.Map;

public final class TypeColorRegistry {
    public static final Color DEFAULT_COLOR = new Color(255, 0, 255); // bright magenta — unmapped type
    public static final Color EXEC_COLOR = new Color(220, 220, 220);
    public static final Color DISCONNECTED_COLOR = new Color(75, 75, 75);

    private static final Map<Class<?>, Color> COLORS = new LinkedHashMap<>();

    static {
        COLORS.put(Number.class,     new Color(0,   180, 200)); // muted cyan
        COLORS.put(Boolean.class,    new Color(60,  180, 60));  // muted green
        COLORS.put(WorldPoint.class, new Color(200, 175, 0));   // muted yellow/gold
        COLORS.put(String.class,     new Color(210, 120, 30));  // muted orange
        COLORS.put(InventoryItemDataMap.class, new Color(0, 102, 204)); // my fave blue
        COLORS.put(Object.class,     new Color(150, 150, 150)); // grey
//        COLORS.put()
    }

    public static Color getColor(Class<?> type) {
        Color c = COLORS.get(type);
        if (c != null) return c;
        // Walk registered supertypes — handles Integer/Double → Number, etc.
        // Skip Object.class here so it only matches via exact lookup above.
        for (Map.Entry<Class<?>, Color> entry : COLORS.entrySet()) {
            if (entry.getKey() != Object.class && entry.getKey().isAssignableFrom(type)) return entry.getValue();
        }
        return DEFAULT_COLOR;
    }

    private TypeColorRegistry() {}
}
