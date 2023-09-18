package com.adamk33n3r.runelite.watchdog.hub;

import com.adamk33n3r.runelite.watchdog.ui.WrappingLabel;
import com.adamk33n3r.runelite.watchdog.ui.panels.PanelUtils;
import lombok.Getter;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.DynamicGridLayout;
import net.runelite.client.ui.PluginPanel;

import javax.swing.*;
import java.awt.*;

@Getter
public class AlertHubItem extends JPanel {
    private final AlertManifest manifest;

    public AlertHubItem(AlertManifest manifest) {
        this.manifest = manifest;
        this.setBackground(ColorScheme.BRAND_ORANGE);
        this.setMaximumSize(new Dimension(PluginPanel.PANEL_WIDTH + PluginPanel.SCROLLBAR_WIDTH, Short.MAX_VALUE));

        GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);

        JLabel alertName = new JLabel(this.manifest.toString());
        JLabel alertAuthor = new JLabel(this.manifest.getAuthor());
        WrappingLabel alertDescLabel = new WrappingLabel(this.manifest.getDescription());
        alertDescLabel.setForeground(Color.CYAN);

//        container.add(alertDescription);
//        this.add(alertDescription);

        layout.setVerticalGroup(layout.createSequentialGroup()
            .addGap(5)
            .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                .addComponent(alertName)
                .addComponent(alertAuthor))
            .addComponent(alertDescLabel)
        );

        layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
            .addGroup(layout.createSequentialGroup()
//                .addGap(5)
                .addComponent(alertName)
//                .addGap(50)
                .addComponent(alertAuthor))
//                .addGap(5))
            .addComponent(alertDescLabel)
        );

//        this.setLayout(new DynamicGridLayout(0, 1, 5, 5));
//        this.add(new JLabel(this.manifest.toString()));
//        this.add(new JLabel(this.manifest.getAuthor()));
//        this.add(new JLabel(this.manifest.getDescription()));
//        this.add(new JLabel(this.manifest.getRepo().toString()));
    }
}
