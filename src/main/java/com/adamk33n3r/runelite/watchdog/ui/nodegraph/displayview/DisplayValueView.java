package com.adamk33n3r.runelite.watchdog.ui.nodegraph.displayview;

import com.adamk33n3r.nodegraph.ExecSignal;
import com.adamk33n3r.runelite.watchdog.InventoryItemData;

import net.runelite.api.coords.WorldPoint;
import net.runelite.client.game.ItemManager;
import net.runelite.client.ui.ColorScheme;

import javax.swing.*;
import java.awt.*;
import java.text.NumberFormat;

/**
 * Renders the current value of a Display node's input using a type-specific view.
 * Replaces the truncated one-line string from ViewInput with a rich per-type renderer.
 */
public class DisplayValueView extends JPanel {
    private final ItemManager itemManager;

    public DisplayValueView(ItemManager itemManager) {
        this.itemManager = itemManager;
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(2, 4, 2, 4));
        render(null);
    }

    public void render(Object value) {
        removeAll();
        add(buildView(value), BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    public JComponent buildView(Object value) {
        if (value == null) {
            return italicLabel("null");
        }
        if (value instanceof Boolean) {
            return boolView((Boolean) value);
        }
        if (value instanceof Number) {
            return numberView((Number) value);
        }
        if (value instanceof String) {
            return stringView((String) value);
        }
        if (value instanceof WorldPoint) {
            return worldPointView((WorldPoint) value);
        }
        if (value instanceof ExecSignal) {
            return execView((ExecSignal) value);
        }
        if (value.getClass().isArray()) {
            return arrayView(value);
        }
        if (value instanceof InventoryItemData.InventoryItemDataMap) {
            return new InventoryGridView((InventoryItemData.InventoryItemDataMap) value, this.itemManager);
        }
        return fallbackLabel(value.toString());
    }

    // ── per-type renderers ────────────────────────────────────────────────────

    private JLabel italicLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(label.getFont().deriveFont(Font.ITALIC));
        label.setForeground(ColorScheme.LIGHT_GRAY_COLOR);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        return label;
    }

    private JLabel boolView(boolean value) {
        JLabel label = new JLabel(value ? "✔" : "✖");
        label.setFont(label.getFont().deriveFont(Font.BOLD, 20f));
        label.setForeground(value ? new Color(60, 180, 60) : new Color(210, 60, 60));
        label.setHorizontalAlignment(SwingConstants.CENTER);
        return label;
    }

    private JLabel numberView(Number value) {
        String text;
        if (value instanceof Integer || value instanceof Long) {
            text = NumberFormat.getInstance().format(value.longValue());
        } else {
            text = String.format("%.4g", value.doubleValue());
        }
        JLabel label = new JLabel(text);
        label.setHorizontalAlignment(SwingConstants.RIGHT);
        label.setFont(label.getFont().deriveFont(Font.PLAIN));
        return label;
    }

    private JScrollPane stringView(String value) {
        JTextArea area = new JTextArea(value);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setEditable(false);
        area.setRows(Math.min(6, (int) value.chars().filter(c -> c == '\n').count() + 1));
        area.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        area.setForeground(Color.WHITE);
        return new JScrollPane(area,
            ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
            ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
    }

    private JLabel worldPointView(WorldPoint wp) {
        JLabel label = new JLabel(String.format("(%d, %d, plane %d)", wp.getX(), wp.getY(), wp.getPlane()));
        label.setFont(new Font(Font.MONOSPACED, Font.PLAIN, label.getFont().getSize()));
        return label;
    }

    private JLabel execView(ExecSignal signal) {
        JLabel label = new JLabel("Exec " + signal.toString());
        label.setFont(label.getFont().deriveFont(Font.ITALIC));
        label.setForeground(ColorScheme.LIGHT_GRAY_COLOR);
        return label;
    }

    private JPanel arrayView(Object value) {
        Object[] arr = (Object[]) value;
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        JLabel header = new JLabel(value.getClass().getComponentType().getSimpleName() + "[" + arr.length + "]");
        header.setForeground(ColorScheme.LIGHT_GRAY_COLOR);
        panel.add(header);
        int limit = Math.min(5, arr.length);
        for (int i = 0; i < limit; i++) {
            JLabel row = new JLabel("  " + (arr[i] != null ? arr[i].toString() : "null"));
            row.setForeground(Color.WHITE);
            panel.add(row);
        }
        if (arr.length > 5) {
            panel.add(new JLabel("  … (" + (arr.length - 5) + " more)"));
        }
        return panel;
    }

    private JLabel fallbackLabel(String text) {
        return new JLabel(text);
    }
}
