package com.adamk33n3r.runelite.watchdog.hub;

import com.adamk33n3r.runelite.watchdog.WatchdogPlugin;
import com.adamk33n3r.runelite.watchdog.alerts.Alert;
import com.adamk33n3r.runelite.watchdog.alerts.AlertGroup;
import com.adamk33n3r.runelite.watchdog.ui.WrappingLabel;
import com.google.gson.Gson;
import lombok.Getter;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.PluginPanel;

import javax.swing.*;
import java.awt.*;

@Getter
public class AlertHubItem extends JPanel {
    private final AlertHubClient.AlertDisplayInfo alertDisplayInfo;

    public AlertHubItem(AlertHubClient.AlertDisplayInfo alertDisplayInfo) {
        this.alertDisplayInfo = alertDisplayInfo;
        this.setBackground(ColorScheme.BRAND_ORANGE);
        this.setMaximumSize(new Dimension(PluginPanel.PANEL_WIDTH + PluginPanel.SCROLLBAR_WIDTH, Short.MAX_VALUE));

        GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);

        JLabel alertName = new JLabel(this.alertDisplayInfo.getManifest().toString());
        JLabel alertAuthor = new JLabel(this.alertDisplayInfo.getManifest().getAuthor());
        WrappingLabel alertDescLabel = new WrappingLabel(this.alertDisplayInfo.getManifest().getDescription());
        alertDescLabel.setForeground(Color.CYAN);

        JLabel icon = new JLabel(new ImageIcon(this.alertDisplayInfo.getIcon()));
        icon.setHorizontalAlignment(JLabel.CENTER);

        Gson gson = WatchdogPlugin.getInstance().getAlertManager().getGson().newBuilder().setPrettyPrinting().create();
        System.out.println(this.alertDisplayInfo.getManifest().getAlert().getClass().getCanonicalName());
        String s = gson.toJson(this.alertDisplayInfo.getManifest().getAlert());
        System.out.println(s);

        icon.setToolTipText("<html><pre style='font-size:10px'>"+s);

//        container.add(alertDescription);
//        this.add(alertDescription);

        layout.setVerticalGroup(layout.createSequentialGroup()
            .addGap(5)
            .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                .addComponent(alertName)
                .addComponent(alertAuthor))
            .addComponent(alertDescLabel)
            .addComponent(icon, 147, GroupLayout.DEFAULT_SIZE, 147)
        );

        layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
            .addGroup(layout.createSequentialGroup()
//                .addGap(5)
                .addComponent(alertName)
//                .addGap(50)
                .addComponent(alertAuthor))
//                .addGap(5))
            .addComponent(alertDescLabel)
            .addComponent(icon, PluginPanel.PANEL_WIDTH, PluginPanel.PANEL_WIDTH, PluginPanel.PANEL_WIDTH)
        );

//        this.setLayout(new DynamicGridLayout(0, 1, 5, 5));
//        this.add(new JLabel(this.manifest.toString()));
//        this.add(new JLabel(this.manifest.getAuthor()));
//        this.add(new JLabel(this.manifest.getDescription()));
//        this.add(new JLabel(this.manifest.getRepo().toString()));
    }
}
