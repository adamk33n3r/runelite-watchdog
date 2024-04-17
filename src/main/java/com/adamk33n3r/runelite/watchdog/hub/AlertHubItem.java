package com.adamk33n3r.runelite.watchdog.hub;

import com.adamk33n3r.runelite.watchdog.WatchdogConfig;
import com.adamk33n3r.runelite.watchdog.WatchdogPlugin;
import com.adamk33n3r.runelite.watchdog.ui.Icons;
import com.adamk33n3r.runelite.watchdog.ui.WrappingLabel;
import com.adamk33n3r.runelite.watchdog.ui.panels.PanelUtils;

import net.runelite.client.RuneLite;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.FontManager;
import net.runelite.client.util.ImageUtil;
import net.runelite.client.util.LinkBrowser;

import lombok.Getter;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.Color;
import java.awt.image.BufferedImage;

@Getter
public class AlertHubItem extends JPanel {
    private static final int LINE_HEIGHT = 16;

    private final AlertHubClient.AlertDisplayInfo alertDisplayInfo;

    public AlertHubItem(AlertHubClient.AlertDisplayInfo alertDisplayInfo, WatchdogConfig watchdogConfig) {
        this.alertDisplayInfo = alertDisplayInfo;
        this.setBackground(ColorScheme.DARKER_GRAY_COLOR);

        GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);

        AlertManifest manifest = this.alertDisplayInfo.getManifest();
        JLabel alertName = new JLabel(manifest.getDisplayName());
        alertName.setFont(FontManager.getRunescapeBoldFont());
        alertName.setToolTipText(manifest.getDisplayName());
        JLabel alertAuthor = new JLabel(manifest.getAuthor());
        alertAuthor.setFont(FontManager.getRunescapeSmallFont());
        alertAuthor.setToolTipText(manifest.getAuthor());

        try {
            alertName.setIcon(new ImageIcon(ImageUtil.loadImageResource(RuneLite.class, manifest.getCategory().getIcon())));
        } catch (Exception e) {
            // error loading icon
            alertName.setIcon(Icons.HELP);
        }

        JLabel compatVersion = new JLabel(manifest.getCompatibleVersion());
        compatVersion.setHorizontalAlignment(JLabel.RIGHT);
        compatVersion.setFont(FontManager.getRunescapeSmallFont());
        compatVersion.setToolTipText("Compatible with Watchdog v" + manifest.getCompatibleVersion());

        WrappingLabel alertDescLabel = new WrappingLabel(manifest.getDescription());

        JButton moreInfoButton = PanelUtils.createActionButton(Icons.HELP, Icons.HELP_HOVER, "More info", (btn, mod) -> {
            LinkBrowser.browse(manifest.getRepo().toString());
        });
        if (manifest.getRepo() == null) {
            moreInfoButton.setVisible(false);
        }

        JButton addButton = new JButton();
        addButton.setText("Add");
        BufferedImage addIcon = ImageUtil.recolorImage(Icons.ADD.getImage(), Color.WHITE);
        addButton.setIcon(new ImageIcon(addIcon));
        addButton.setHorizontalTextPosition(SwingConstants.LEFT);
        addButton.setBackground(new Color(0x28BE28));
        addButton.setBorder(new LineBorder(addButton.getBackground().darker()));
        addButton.setFocusPainted(false);
        addButton.addActionListener((ev) -> {
            WatchdogPlugin.getInstance().getAlertManager().addAlert(manifest.getAlert(), watchdogConfig.overrideImportsWithDefaults());
            JOptionPane.showMessageDialog(this, "Added " + manifest.getDisplayName() + " to your alerts", "Successfully Added", JOptionPane.INFORMATION_MESSAGE);
        });

        JLabel dependsOn = new JLabel();
        dependsOn.setFont(FontManager.getRunescapeSmallFont());
        if (manifest.getDependsOn() != null && !manifest.getDependsOn().isEmpty()) {
            dependsOn.setText("Depends On: " + String.join(", ", manifest.getDependsOn()));
        }

        layout.setVerticalGroup(layout.createSequentialGroup()
            .addGap(5)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(alertName, LINE_HEIGHT, LINE_HEIGHT, LINE_HEIGHT)
                    .addComponent(moreInfoButton, LINE_HEIGHT, LINE_HEIGHT, LINE_HEIGHT)
                    .addComponent(addButton, LINE_HEIGHT, LINE_HEIGHT, LINE_HEIGHT)
                )
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(alertAuthor, LINE_HEIGHT, LINE_HEIGHT, LINE_HEIGHT)
                    .addComponent(compatVersion, LINE_HEIGHT, LINE_HEIGHT, LINE_HEIGHT)
                )
                .addComponent(alertDescLabel, 0, GroupLayout.DEFAULT_SIZE, 96)
                .addComponent(dependsOn, 0, GroupLayout.DEFAULT_SIZE, LINE_HEIGHT)
                .addGap(5)
            )
        );

        layout.setHorizontalGroup(layout.createParallelGroup()
            // Info group
            .addGroup(layout.createSequentialGroup()
                .addGap(5)
                .addGroup(layout.createParallelGroup()
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(alertName, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
                        .addComponent(moreInfoButton, 24, 24, 24)
                        .addComponent(addButton, 50, 50, GroupLayout.PREFERRED_SIZE)
                    )
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(alertAuthor, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.PREFERRED_SIZE, 100)
                        .addComponent(compatVersion, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    )
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(alertDescLabel, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    )
                    .addComponent(dependsOn)
                )
                .addGap(5)
            )
        );
    }
}
