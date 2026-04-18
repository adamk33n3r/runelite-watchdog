package com.adamk33n3r.runelite.watchdog.ui.alerts;

import com.adamk33n3r.runelite.watchdog.WatchdogPanel;
import com.adamk33n3r.runelite.watchdog.alerts.AlertGroup;
import com.adamk33n3r.runelite.watchdog.ui.HorizontalRuleBorder;
import com.adamk33n3r.runelite.watchdog.ui.panels.AlertContentPanel;
import com.adamk33n3r.runelite.watchdog.ui.panels.AlertListPanel;
import com.adamk33n3r.runelite.watchdog.ui.panels.PanelUtils;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;

public class AlertGroupPanel extends AlertContentPanel<AlertGroup> {
    private final WatchdogPanel watchdogPanel;

    public AlertGroupPanel(AlertGroup alert, WatchdogPanel watchdogPanel, Runnable onChange) {
        super(alert, onChange);
        this.watchdogPanel = watchdogPanel;
        this.init();
    }

    @Override
    public void buildTypeContent() {
    }

    @Override
    protected boolean isAlertGroup() {
        return true;
    }

    @Override
    protected boolean includeNotifications() {
        return false;
    }

    @Override
    protected boolean hasSubsection() {
        return true;
    }

    @Override
    protected JComponent getSubsection() {
        JPanel buttonPanel = new JPanel(new BorderLayout());
        buttonPanel.add(new JLabel("Alerts"), BorderLayout.WEST);
        buttonPanel.setBorder(new EmptyBorder(0, 5, 8, 0));

        JButton alertDropDownButton = PanelUtils.createAlertDropDownButton(createdAlert -> {
            this.alert.getAlerts().add(createdAlert);
            this.alertManager.saveAlerts();
            this.rebuild();
            this.watchdogPanel.openAlert(createdAlert);
        });

        buttonPanel.add(alertDropDownButton, BorderLayout.EAST);
        JPanel subGroupPanel = new JPanel(new BorderLayout());
        subGroupPanel.setBorder(new CompoundBorder(new EmptyBorder(0, 5, 0, 5), new HorizontalRuleBorder(10)));
        subGroupPanel.add(buttonPanel, BorderLayout.NORTH);

        AlertListPanel alertListPanel = new AlertListPanel(this.alert.getAlerts(), this.alert, this::rebuild);

        subGroupPanel.add(alertListPanel);
        return subGroupPanel;
    }
}
