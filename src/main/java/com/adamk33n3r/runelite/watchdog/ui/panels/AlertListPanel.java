package com.adamk33n3r.runelite.watchdog.ui.panels;

import com.adamk33n3r.runelite.watchdog.AlertManager;
import com.adamk33n3r.runelite.watchdog.TriggerType;
import com.adamk33n3r.runelite.watchdog.WatchdogPlugin;
import com.adamk33n3r.runelite.watchdog.alerts.Alert;
import com.adamk33n3r.runelite.watchdog.ui.AlertListItemNew;

import com.adamk33n3r.runelite.watchdog.ui.SearchBar;
import com.google.common.base.Splitter;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.components.DragAndDropReorderPane;
import net.runelite.client.util.Text;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AlertListPanel extends JPanel {
    private String filterText = "";
    private static final Splitter SPLITTER = Splitter.on(" ").trimResults().omitEmptyStrings();
    private final List<AlertListItemNew> alertListItems = new ArrayList<>();
    private final DragAndDropReorderPane dragAndDropReorderPane = new DragAndDropReorderPane();

    public AlertListPanel(List<Alert> alerts, Runnable onChange) {
        AlertManager alertManager = WatchdogPlugin.getInstance().getAlertManager();
        this.dragAndDropReorderPane.setBackground(ColorScheme.GRAND_EXCHANGE_LIMIT);
        this.dragAndDropReorderPane.addDragListener((c) -> {
            int pos = this.dragAndDropReorderPane.getPosition(c);
            AlertListItemNew alertListItem = (AlertListItemNew) c;
            alertManager.moveAlertTo(alertListItem.getAlert(), pos);
        });

        this.setLayout(new BorderLayout());

        SearchBar searchBar = new SearchBar(this::filter);
        Arrays.stream(TriggerType.values()).map(TriggerType::getName).forEach(searchBar.getSuggestionListModel()::addElement);
        JPanel searchWrapper = new JPanel(new BorderLayout(0, 6));
        searchWrapper.add(searchBar);
        searchWrapper.setBorder(new EmptyBorder(0, 5, 0, 5));
        this.add(searchBar, BorderLayout.NORTH);
        // TODO: move the scroll pane in here so that the search bar doesn't scroll

        this.add(dragAndDropReorderPane, BorderLayout.CENTER);

        alerts.stream()
            .map(alert -> new AlertListItemNew(WatchdogPlugin.getInstance().getPanel(), alertManager, alert, dragAndDropReorderPane, onChange))
            .forEach(alertListItem -> {
                this.alertListItems.add(alertListItem);
                this.dragAndDropReorderPane.add(alertListItem);
            });
    }

    private void filter(String text) {
        this.filterText = text;
        this.dragAndDropReorderPane.removeAll();
        this.alertListItems.stream()
            .filter(alertListItem -> Text.matchesSearchTerms(SPLITTER.split(this.filterText.toUpperCase()), alertListItem.getAlert().getKeywords()))
            .forEach(this.dragAndDropReorderPane::add);
        this.revalidate();
    }
}
