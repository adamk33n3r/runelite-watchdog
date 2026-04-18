package com.adamk33n3r.runelite.watchdog.ui.nodegraph.displayview;

import com.adamk33n3r.runelite.watchdog.InventoryItemData;
import com.adamk33n3r.runelite.watchdog.ui.nodegraph.NodePanel;

import net.runelite.client.game.ItemManager;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.util.AsyncBufferedImage;

import javax.swing.*;
import java.awt.*;

/**
 * Renders an {@link InventoryItemData.InventoryItemDataMap} as a 4-column slot grid
 * matching the in-game inventory layout. Slot positions come directly from
 * {@link InventoryItemData.InventoryItemDataMap#getSlots()}.
 */
public class InventoryGridView extends JPanel {
    private static final int COLS = 4;
    private static final int CELL_SIZE = (NodePanel.PANEL_WIDTH - 20) / COLS; // ~40px per cell
    private static final Color EMPTY_CELL_COLOR = ColorScheme.DARKER_GRAY_COLOR;

    public InventoryGridView(InventoryItemData.InventoryItemDataMap map, ItemManager itemManager) {
        InventoryItemData[] slots = map.getSlots();
        int rowCount = 7;//slots.length == 0 ? 0 : (int) Math.ceil(slots.length / (double) COLS);
        setLayout(new GridLayout(rowCount, 4, 1, 1));
        setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(ColorScheme.MEDIUM_GRAY_COLOR, 1),
            "Inventory (" + map.getItemCount() + " items)"
        ));

        if (slots.length == 0) {
            // No slot data available — fall back to a simple text summary
            setLayout(new BorderLayout());
            add(new JLabel("No slot data"), BorderLayout.CENTER);
            return;
        }

        for (InventoryItemData data : slots) {
            if (data == null) {
                add(makeEmptyCell());
            } else {
                add(makeItemCell(data, itemManager));
            }
        }
    }

    private JPanel makeEmptyCell() {
        JPanel cell = new JPanel();
        cell.setBackground(EMPTY_CELL_COLOR);
        cell.setPreferredSize(new Dimension(CELL_SIZE, CELL_SIZE));
        return cell;
    }

    private JLabel makeItemCell(InventoryItemData data, ItemManager itemManager) {
        String name = data.getItemComposition().getName();
        int qty = data.getQuantity();
        JLabel cell = new JLabel();
        cell.setHorizontalAlignment(SwingConstants.CENTER);
        cell.setVerticalAlignment(SwingConstants.CENTER);
        cell.setPreferredSize(new Dimension(CELL_SIZE, CELL_SIZE));
        cell.setBackground(EMPTY_CELL_COLOR);
        cell.setOpaque(true);
        cell.setToolTipText(name + " × " + qty);

        if (itemManager != null) {
            try {
                int id = data.getItemComposition().getId();
                AsyncBufferedImage icon = itemManager.getImage(id, qty, qty > 1);
                icon.addTo(cell);
            } catch (Exception e) {
                cell.setText(abbreviate(name));
                cell.setFont(cell.getFont().deriveFont(9f));
                cell.setForeground(Color.WHITE);
            }
        } else {
            cell.setText(abbreviate(name));
            cell.setFont(cell.getFont().deriveFont(9f));
            cell.setForeground(Color.WHITE);
        }

        return cell;
    }

    /** Returns the first 4 chars of a name, for use in the text-only fallback. */
    public static String abbreviate(String name) {
        if (name == null || name.isEmpty()) return "?";
        return name.length() <= 4 ? name : name.substring(0, 4);
    }
}
