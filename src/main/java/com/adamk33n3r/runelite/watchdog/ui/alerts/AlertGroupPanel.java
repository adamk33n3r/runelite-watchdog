package com.adamk33n3r.runelite.watchdog.ui.alerts;

import com.adamk33n3r.runelite.watchdog.AlertManager;
import com.adamk33n3r.runelite.watchdog.WatchdogPanel;
import com.adamk33n3r.runelite.watchdog.WatchdogPlugin;
import com.adamk33n3r.runelite.watchdog.alerts.AlertGroup;
import com.adamk33n3r.runelite.watchdog.ui.AlertListItem;
import com.adamk33n3r.runelite.watchdog.ui.AlertListItemNew;
import com.adamk33n3r.runelite.watchdog.ui.HorizontalRuleBorder;
import com.adamk33n3r.runelite.watchdog.ui.StretchedStackedLayout;
import com.adamk33n3r.runelite.watchdog.ui.panels.AlertListPanel;
import com.adamk33n3r.runelite.watchdog.ui.panels.AlertPanel;
import com.adamk33n3r.runelite.watchdog.ui.panels.PanelUtils;

import net.runelite.client.ui.DynamicGridLayout;
import net.runelite.client.ui.components.DragAndDropReorderPane;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;

public class AlertGroupPanel extends AlertPanel<AlertGroup> {
    public AlertGroupPanel(WatchdogPanel watchdogPanel, AlertGroup alert) {
        super(watchdogPanel, alert);
    }

    @Override
    protected void build() {
        AlertManager alertManager = WatchdogPlugin.getInstance().getAlertManager();
        this.addAlertDefaults();
        JPanel buttonPanel = new JPanel(new BorderLayout());
        buttonPanel.add(new JLabel("Alerts"), BorderLayout.WEST);

        JButton alertDropDownButton = PanelUtils.createAlertDropDownButton(createdAlert -> {
            this.alert.getAlerts().add(createdAlert);
            alertManager.saveAlerts();
            this.rebuild();
            this.watchdogPanel.openAlert(createdAlert);
        });

        buttonPanel.add(alertDropDownButton, BorderLayout.EAST);
        JPanel subGroupPanel = new JPanel(new StretchedStackedLayout(3, 3));
        subGroupPanel.setBorder(new HorizontalRuleBorder(10));
        subGroupPanel.add(buttonPanel);
        this.addSubPanel(subGroupPanel);

        DragAndDropReorderPane dragAndDropReorderPane = new DragAndDropReorderPane();
        dragAndDropReorderPane.addDragListener((c) -> {
            int pos = dragAndDropReorderPane.getPosition(c);
            AlertListItemNew alertListItem = (AlertListItemNew) c;
            this.alert.getAlerts().remove(alertListItem.getAlert());
            this.alert.getAlerts().add(pos, alertListItem.getAlert());
            alertManager.saveAlerts();
        });
        subGroupPanel.add(new AlertListPanel(this.alert.getAlerts(), dragAndDropReorderPane, this::rebuild));
    }
}
