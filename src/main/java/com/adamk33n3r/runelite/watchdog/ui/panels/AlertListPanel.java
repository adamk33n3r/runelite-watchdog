package com.adamk33n3r.runelite.watchdog.ui.panels;

import com.adamk33n3r.runelite.watchdog.AlertManager;
import com.adamk33n3r.runelite.watchdog.TriggerType;
import com.adamk33n3r.runelite.watchdog.Util;
import com.adamk33n3r.runelite.watchdog.WatchdogPlugin;
import com.adamk33n3r.runelite.watchdog.alerts.Alert;
import com.adamk33n3r.runelite.watchdog.alerts.AlertGroup;
import com.adamk33n3r.runelite.watchdog.ui.*;

import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.DynamicGridLayout;
import net.runelite.client.ui.components.DragAndDropReorderPane;

import com.google.common.base.Splitter;
import lombok.Getter;

import javax.annotation.Nullable;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class AlertListPanel extends JPanel {
    private String filterText = "";
    private static final Splitter SPLITTER = Splitter.on(" ").trimResults().omitEmptyStrings();
    private final List<AlertListItem> alertListItems = new ArrayList<>();
    private final DragAndDropReorderPane dragAndDropReorderPane = new DragAndDropReorderPane();
    @Getter
    private final JScrollPane scrollPane;
    private boolean selectMode = false;
    private final AlertManager alertManager;
    private final Runnable onChange;
    private final List<Alert> alerts;
    private final AlertGroup parent;

    public AlertListPanel(List<Alert> alerts, @Nullable AlertGroup parent, Runnable onChange) {
        this.alerts = alerts;
        this.parent = parent;
        this.onChange = onChange;
        this.alertManager = WatchdogPlugin.getInstance().getAlertManager();
        this.dragAndDropReorderPane.setBackground(ColorScheme.GRAND_EXCHANGE_LIMIT);
        this.dragAndDropReorderPane.addDragListener((c) -> {
            int pos = this.dragAndDropReorderPane.getPosition(c);
            AlertListItem alertListItem = (AlertListItem) c;
            this.alertManager.moveAlertTo(alertListItem.getAlert(), pos);
        });

        this.setLayout(new BorderLayout());

        ScrollablePanel scrollablePanel = new ScrollablePanel(new StretchedStackedLayout(3));
        scrollablePanel.setScrollableWidth(ScrollablePanel.ScrollableSizeHint.FIT);
        scrollablePanel.setScrollableHeight(ScrollablePanel.ScrollableSizeHint.STRETCH);
        scrollablePanel.setScrollableBlockIncrement(ScrollablePanel.VERTICAL, ScrollablePanel.IncrementType.PERCENT, 10);
        scrollablePanel.add(this.dragAndDropReorderPane);
        this.scrollPane = new JScrollPane(scrollablePanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        this.rebuild();
    }

    private void rebuild() {
        this.removeAll();
        this.dragAndDropReorderPane.removeAll();

        SearchBar searchBar = new SearchBar(this::filter);
        Arrays.stream(TriggerType.values()).map(TriggerType::getName).forEach(searchBar.getSuggestionListModel()::addElement);
        JPanel searchWrapper = new JPanel(new BorderLayout());
        searchWrapper.add(searchBar);
        SwingUtilities.invokeLater(() -> {
            searchWrapper.setBorder(new EmptyBorder(0, 0, 2, this.scrollPane.getVerticalScrollBar().isVisible() ? 7 : 0));
        });
        this.add(searchWrapper, BorderLayout.NORTH);
        this.add(this.scrollPane, BorderLayout.CENTER);

        final JPanel multiSelect = new JPanel(new BorderLayout());
        multiSelect.setPreferredSize(new Dimension(0, 25));
        multiSelect.setBorder(new EmptyBorder(0, 10, 0, 0));
        final JPanel toggleGroup = new JPanel(new DynamicGridLayout(1, 2, 3, 3));
        toggleGroup.setBorder(new EmptyBorder(4, 0, 0, 0));
        final ToggleButton selectModeToggle = new ToggleButton("Disable Select Mode", "Enable Select Mode");
        selectModeToggle.setSelected(this.selectMode);
        selectModeToggle.addItemListener((i) -> {
            this.selectMode = selectModeToggle.isSelected();
            // Unselect all alerts when leaving select mode
            if (!this.selectMode) {
                this.alertListItems.forEach((ali) -> ali.setSelected(false));
            }
            this.rebuild();
        });
        toggleGroup.add(selectModeToggle);
        JLabel selectModeLabel = new JLabel("Select Mode");
        selectModeLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                selectModeToggle.setSelected(!selectModeToggle.isSelected());
                AlertListPanel.this.revalidate();
            }
        });
        toggleGroup.add(selectModeLabel);
        multiSelect.add(toggleGroup, BorderLayout.WEST);

        final JPanel multiSelectActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 3, 0));
        multiSelectActions.setBorder(new EmptyBorder(6, 0, 0, 0));
        multiSelectActions.add(PanelUtils.createActionButton(Icons.IMPORT, Icons.IMPORT_HOVER, "Move selected Alerts to new Alert Group", (btn, modifiers) -> {
            long count = this.alertListItems.stream()
                .filter(AlertListItem::isSelected)
                .count();
            if (count == 0) {
                return;
            }
            AlertGroup group = this.alertManager.createAlert(AlertGroup.class);
            if (this.parent == null) {
                this.alertManager.addAlert(group, false);
            } else {
                this.parent.getAlerts().add(group);
            }
            this.alertListItems.stream()
                .filter(AlertListItem::isSelected)
                .forEach((ali) -> {
                    this.alertManager.removeAlert(ali.getAlert(), false);
                    group.getAlerts().add(ali.getAlert());
                });
            WatchdogPlugin.getInstance().getPanel().openAlert(group);
        })).setEnabled(this.selectMode);
        if (this.parent != null) {
            multiSelectActions.add(PanelUtils.createActionButton(Icons.BACK, Icons.BACK_HOVER, "Move selected Alerts back a level", (btn, modifiers) -> {
                long count = this.alertListItems.stream()
                    .filter(AlertListItem::isSelected)
                    .count();
                if (count == 0) {
                    return;
                }
                AlertGroup alertGroupParent = this.parent.getParent();
                this.alertListItems.stream()
                    .filter(AlertListItem::isSelected)
                    .forEach((ali) -> {
                        this.alertManager.removeAlert(ali.getAlert(), false);
                        if (alertGroupParent == null) {
                            this.alertManager.addAlert(ali.getAlert(), false);
                        } else {
                            alertGroupParent.getAlerts().add(ali.getAlert());
                        }
                    });
                WatchdogPlugin.getInstance().getPanel().getMuxer().popState();
            })).setEnabled(this.selectMode);
        }
        multiSelectActions.add(PanelUtils.createActionButton(Icons.EXPORT, Icons.EXPORT_HOVER, "Export selected Alerts", (btn, modifiers) -> {
            List<Alert> selectedAlerts = this.alertListItems.stream()
                .filter(AlertListItem::isSelected)
                .map(AlertListItem::getAlert)
                .collect(Collectors.toList());
            if (selectedAlerts.isEmpty()) {
                return;
            }
            ImportExportDialog importExportDialog = new ImportExportDialog(SwingUtilities.getWindowAncestor(this), selectedAlerts);
            importExportDialog.setVisible(true);
        })).setEnabled(this.selectMode);
        multiSelectActions.add(PanelUtils.createActionButton(Icons.DELETE, Icons.DELETE_HOVER, "Delete selected Alerts", (btn, modifiers) -> {
            long count = this.alertListItems.stream()
                .filter(AlertListItem::isSelected)
                .count();
            if (count == 0) {
                return;
            }
            int result = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete the " + count + " selected alerts?", "Delete?", JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE);
            if (result == JOptionPane.YES_OPTION) {
                this.alertListItems.stream()
                    .filter(AlertListItem::isSelected)
                    .forEach((ali) -> this.alertManager.removeAlert(ali.getAlert()));
                onChange.run();
            }
        })).setEnabled(this.selectMode);
        multiSelect.add(multiSelectActions, BorderLayout.EAST);
        searchWrapper.add(multiSelect, BorderLayout.SOUTH);

        this.alerts.stream()
            .map(alert -> new AlertListItem(WatchdogPlugin.getInstance().getPanel(), this.alertManager, alert, this.dragAndDropReorderPane, onChange))
            .forEach(alertListItem -> {
                alertListItem.setSelectMode(this.selectMode);
                this.alertListItems.add(alertListItem);
                this.dragAndDropReorderPane.add(alertListItem);
            });
    }

    private void filter(String text) {
        this.filterText = text;
        this.dragAndDropReorderPane.removeAll();
        this.alertListItems.stream()
            .filter(alertListItem -> Util.searchText(this.filterText, alertListItem.getAlert().getKeywords()))
            .forEach(this.dragAndDropReorderPane::add);
        this.revalidate();
    }
}
