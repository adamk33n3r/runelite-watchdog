package com.adamk33n3r.runelite.watchdog.ui.panels;

import com.adamk33n3r.runelite.watchdog.AlertManager;
import com.adamk33n3r.runelite.watchdog.WatchdogPlugin;
import com.adamk33n3r.runelite.watchdog.alerts.Alert;
import com.adamk33n3r.runelite.watchdog.ui.AlertListItem;

import net.runelite.client.ui.components.DragAndDropReorderPane;

import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.util.List;

public class AlertListPanel extends JPanel {
    public AlertListPanel(List<Alert> alerts, DragAndDropReorderPane dragAndDropReorderPane, Runnable onChange) {
        this.setLayout(new BorderLayout());
        AlertManager alertManager = WatchdogPlugin.getInstance().getAlertManager();
        alerts.forEach((subAlert) -> {
            AlertListItem alertListItem = new AlertListItem(WatchdogPlugin.getInstance().getPanel(), alertManager, subAlert, alerts, dragAndDropReorderPane, onChange);
            dragAndDropReorderPane.add(alertListItem);
        });
        this.add(dragAndDropReorderPane, BorderLayout.NORTH);
    }
}
