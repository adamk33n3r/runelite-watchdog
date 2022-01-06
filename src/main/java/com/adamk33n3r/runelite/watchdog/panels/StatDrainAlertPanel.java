package com.adamk33n3r.runelite.watchdog.panels;

import com.adamk33n3r.runelite.watchdog.Util;
import com.adamk33n3r.runelite.watchdog.WatchdogPlugin;
import com.adamk33n3r.runelite.watchdog.alerts.Alert;
import com.adamk33n3r.runelite.watchdog.alerts.StatDrainAlert;
import com.adamk33n3r.runelite.watchdog.notifications.INotification;
import com.adamk33n3r.runelite.watchdog.panels.notifications.NotificationsPanel;
import net.runelite.api.Skill;
import net.runelite.client.ui.DynamicGridLayout;
import net.runelite.client.ui.MultiplexingPluginPanel;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.List;

public class StatDrainAlertPanel extends AlertPanel {
    public StatDrainAlertPanel(StatDrainAlert statDrainAlert, MultiplexingPluginPanel muxer, WatchdogPlugin plugin) {
        // Can get list of alerts from plugin and then count the chat alerts to find the number
        super(statDrainAlert = Util.defaultArg(statDrainAlert, new StatDrainAlert("Stat Drain Alert")));
        JPanel wrapper = new JPanel(new BorderLayout());
        ScrollablePanel container = new ScrollablePanel(new DynamicGridLayout(0, 1, 3, 3));
        container.setScrollableWidth(ScrollablePanel.ScrollableSizeHint.FIT);
        container.setScrollableBlockIncrement(ScrollablePanel.VERTICAL, ScrollablePanel.IncrementType.PERCENT, 10);
        JScrollPane scroll = new JScrollPane(container, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        wrapper.add(scroll, BorderLayout.CENTER);
        container.setBorder(new TitledBorder(new EtchedBorder(), "Stat Drain Alert", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, Color.WHITE));

        JTextField nameTextField = new JTextField(statDrainAlert.getName());
        container.add(PanelUtils.createLabeledComponent("Name", nameTextField));

        JComboBox<Skill> skillSelect = new JComboBox<>(Skill.values());
        skillSelect.setSelectedItem(statDrainAlert.getSkill());
        skillSelect.setRenderer((list, value, index, isSelected, cellHasFocus) -> {
            String titleized = StringUtils.capitalize(StringUtils.lowerCase(value.name()));
            list.setToolTipText(titleized);
            return new DefaultListCellRenderer().getListCellRendererComponent(list, titleized, index, isSelected, cellHasFocus);
        });
        container.add(PanelUtils.createLabeledComponent("Skill", skillSelect));
        javax.swing.JSpinner drainAmount = new JSpinner(new SpinnerNumberModel(statDrainAlert.getDrainAmount(), 1, 99, 1));
        container.add(PanelUtils.createLabeledComponent("Drain Amount", drainAmount));

        NotificationsPanel notificationPanel = new NotificationsPanel(alert.getNotifications());
        notificationPanel.setBorder(new TitledBorder(new EtchedBorder(), "Notifications", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, Color.WHITE));
        container.add(notificationPanel);

        JPanel buttonPanel = new JPanel(new DynamicGridLayout(1, 2, 3, 3));
        JButton back = new JButton("Back");
        back.addActionListener(ev -> {
            muxer.popState();
        });
        buttonPanel.add(back);
        JButton save = new JButton("Save");
        StatDrainAlert finalStatDrainAlert = statDrainAlert;
        save.addActionListener(ev -> {
            List<INotification> notificationList = notificationPanel.getNotifications();
            finalStatDrainAlert.setName(nameTextField.getText());
            finalStatDrainAlert.setSkill(skillSelect.getItemAt(skillSelect.getSelectedIndex()));
            finalStatDrainAlert.setDrainAmount((Integer)drainAmount.getValue());
            finalStatDrainAlert.getNotifications().clear();
            finalStatDrainAlert.getNotifications().addAll(notificationList);
            List<Alert> alerts = plugin.getAlerts();
            if (!alerts.contains(finalStatDrainAlert)) {
                alerts.add(finalStatDrainAlert);
            }
            plugin.saveAlerts(alerts);
            muxer.popState();
        });
        buttonPanel.add(save);
        wrapper.add(buttonPanel, BorderLayout.SOUTH);
        this.add(wrapper, BorderLayout.CENTER);
    }
}
