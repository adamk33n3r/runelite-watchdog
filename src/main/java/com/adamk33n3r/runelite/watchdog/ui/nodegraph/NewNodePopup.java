package com.adamk33n3r.runelite.watchdog.ui.nodegraph;

import com.adamk33n3r.runelite.watchdog.Displayable;
import com.adamk33n3r.runelite.watchdog.NotificationCategory;
import com.adamk33n3r.runelite.watchdog.NotificationType;
import com.adamk33n3r.runelite.watchdog.TriggerType;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableMap;
import net.runelite.client.util.Text;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.*;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.LinkedHashMap;

public class NewNodePopup extends JPopupMenu {
    public static final List<Class<? extends Enum<?>>> DEFAULT_ITEMS = Arrays.asList(
        TriggerType.class,
        NotificationType.class,
        VariableNodeType.class,
        LogicNodeType.class,
        MathNodeType.class
    );

    private static final int MAX_RECENT = 5;
    private static final LinkedList<Enum<?>> recentNodes = new LinkedList<>();

    private static final Splitter SPLITTER = Splitter.on(' ').trimResults().omitEmptyStrings();

    private static final Map<Class<? extends Enum<?>>, String> enumToCategory = ImmutableMap.of(
        TriggerType.class, "Alert",
        NotificationType.class, "Action",
        LogicNodeType.class, "Condition",
        VariableNodeType.class, "Variable",
        MathNodeType.class, "Math"
    );

    private Consumer<Object> onSelectCallback = obj -> {};
    private PopupMenuListener currentPopupListener;

    private final Predicate<Enum<?>> itemFilter;
    private final CustomList.Items[] items;
    private final JTextField search;
    private final JList<Object> itemList;
    private final JScrollPane searchScrollPane;
    private final JMenu[] categoryMenus;
    private final JMenu recentMenu;

    public NewNodePopup(Predicate<Enum<?>> itemFilter) {
        this.itemFilter = itemFilter;

        this.items = DEFAULT_ITEMS.stream()
            .map(item -> new CustomList.Items(
                item,
                enumToCategory.get(item),
                e -> !(e == TriggerType.ALERT_GROUP || e == TriggerType.ADVANCED_ALERT) && itemFilter.test(e)))
            .toArray(CustomList.Items[]::new);

        // Search field
        this.search = new JTextField();
        this.search.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { NewNodePopup.this.updateDisplay(); }
            @Override public void removeUpdate(DocumentEvent e) { NewNodePopup.this.updateDisplay(); }
            @Override public void changedUpdate(DocumentEvent e) { NewNodePopup.this.updateDisplay(); }
        });

        // Flat list for search mode
        this.itemList = new CustomList(this.items) {
            @Override
            public String getToolTipText(MouseEvent e) {
                int index = locationToIndex(e.getPoint());
                if (index >= 0) {
                    Object value = getModel().getElementAt(index);
                    if (value instanceof Displayable) {
                        return ((Displayable) value).getTooltip();
                    }
                }
                return null;
            }
        };
        ToolTipManager.sharedInstance().registerComponent(this.itemList);
        this.itemList.addListSelectionListener(ls -> {
            Object value = this.itemList.getSelectedValue();
            if (value == null || value.toString().startsWith("c:")) return;
            this.selectItem((Enum<?>) value);
        });
        this.searchScrollPane = new JScrollPane(this.itemList);
        this.searchScrollPane.getVerticalScrollBar().setBlockIncrement(1);

        // Category JMenus for browse mode
        this.categoryMenus = Arrays.stream(this.items)
            .map(categoryItems -> {
                JMenu menu = new JMenu(categoryItems.getCategory());
                boolean isNotificationType = categoryItems.getItems().length > 0
                    && categoryItems.getItems()[0] instanceof NotificationType;
                if (isNotificationType) {
                    // Group Action items into NotificationCategory sub-menus
                    Map<NotificationCategory, JMenu> subMenus = new LinkedHashMap<>();
                    for (NotificationCategory cat : NotificationCategory.values()) {
                        JMenu subMenu = new JMenu(cat.getName());
                        subMenu.setToolTipText(cat.getTooltip());
                        subMenus.put(cat, subMenu);
                    }
                    for (Enum<?> item : categoryItems.getItems()) {
                        NotificationType nt = (NotificationType) item;
                        JMenuItem menuItem = new JMenuItem(nt.getName());
                        menuItem.setToolTipText(nt.getTooltip());
                        menuItem.addActionListener(e -> this.selectItem(item));
                        subMenus.get(nt.getCategory()).add(menuItem);
                    }
                    for (JMenu subMenu : subMenus.values()) {
                        if (subMenu.getItemCount() > 0) {
                            menu.add(subMenu);
                        }
                    }
                } else {
                    for (Enum<?> item : categoryItems.getItems()) {
                        JMenuItem menuItem = new JMenuItem(((Displayable) item).getName());
                        menuItem.setToolTipText(((Displayable) item).getTooltip());
                        menuItem.addActionListener(e -> this.selectItem(item));
                        menu.add(menuItem);
                    }
                }
                return menu;
            })
            .toArray(JMenu[]::new);

        // Recent category (populated in show())
        this.recentMenu = new JMenu("Recent");
    }

    public void show(Component invoker, int x, int y, Consumer<Object> onSelect, Runnable onDismiss) {
        this.onSelectCallback = onSelect;

        // Rebuild recent menu, applying the same exclusions as the category items
        this.recentMenu.removeAll();
        recentNodes.stream()
            .filter(item -> !(item == TriggerType.ALERT_GROUP || item == TriggerType.ADVANCED_ALERT)
                && this.itemFilter.test(item))
            .forEach(item -> {
                JMenuItem mi = new JMenuItem(((Displayable) item).getName());
                mi.setToolTipText(((Displayable) item).getTooltip());
                mi.addActionListener(e -> this.selectItem(item));
                this.recentMenu.add(mi);
            });

        this.removePopupMenuListener(this.currentPopupListener);
        this.currentPopupListener = new PopupMenuListener() {
            @Override public void popupMenuWillBecomeVisible(PopupMenuEvent e) {}

            @Override
            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
                onDismiss.run();
            }

            @Override public void popupMenuCanceled(PopupMenuEvent e) {}
        };
        this.addPopupMenuListener(this.currentPopupListener);

        this.search.setText("");
        this.updateDisplay();
        super.show(invoker, x, y);

        this.maybeAutoOpen();
    }

    private void selectItem(Enum<?> item) {
        addRecent(item);
        this.onSelectCallback.accept(item);
        this.setVisible(false);
        this.itemList.clearSelection();
        this.itemList.dispatchEvent(new MouseEvent(
            this.itemList, MouseEvent.MOUSE_EXITED, System.currentTimeMillis(), 0, 0, 0, 0, false));
    }

    private static void addRecent(Enum<?> item) {
        recentNodes.remove(item);
        recentNodes.addFirst(item);
        if (recentNodes.size() > MAX_RECENT) recentNodes.removeLast();
    }

    private void updateDisplay() {
        this.removeAll();
        this.add(this.search);

        if (this.search.getText().isEmpty()) {
            if (this.recentMenu.getItemCount() > 0) {
                this.add(this.recentMenu);
            }
            for (JMenu menu : this.categoryMenus) {
                if (menu.getItemCount() > 0) {
                    this.add(menu);
                }
            }
        } else {
            this.filterList();
            this.add(this.searchScrollPane);
        }

        this.pack();
    }

    private void filterList() {
        this.itemList.setListData(Arrays.stream(this.items).flatMap(i -> {
            Object[] matching = Arrays.stream(i.getItems())
                .filter(item -> Text.matchesSearchTerms(SPLITTER.split(this.search.getText().toUpperCase()),
                    Collections.singleton(item.toString().toUpperCase())))
                .toArray();
            if (matching.length == 0) return Stream.empty();
            return Stream.concat(Stream.of("c:" + i.getCategory()), Arrays.stream(matching));
        }).toArray(Object[]::new));
    }

    // When dragging a connection causes all-but-one category to be filtered out,
    // automatically open the remaining category's submenu so the user lands right on
    // the compatible items without any extra hover.
    private void maybeAutoOpen() {
        List<JMenu> nonEmpty = Arrays.stream(this.categoryMenus)
            .filter(m -> m.getItemCount() > 0)
            .collect(Collectors.toList());
        if (nonEmpty.size() == 1) {
            JMenu target = nonEmpty.get(0);
            SwingUtilities.invokeLater(() ->
                MenuSelectionManager.defaultManager().setSelectedPath(
                    new MenuElement[]{this, target, target.getPopupMenu()}
                )
            );
        }
    }
}
