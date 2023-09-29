package com.adamk33n3r.runelite.watchdog.hub;

import com.adamk33n3r.runelite.watchdog.WatchdogPanel;
import com.adamk33n3r.runelite.watchdog.WatchdogPlugin;
import com.adamk33n3r.runelite.watchdog.ui.WrappingLabel;
import com.adamk33n3r.runelite.watchdog.ui.panels.PanelUtils;
import lombok.Getter;
import net.runelite.client.RuneLite;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.FontManager;
import net.runelite.client.util.ImageUtil;
import net.runelite.client.util.LinkBrowser;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.image.BufferedImage;

@Getter
public class AlertHubItem extends JPanel {
    private static final int LINE_HEIGHT = 16;

    private final AlertHubClient.AlertDisplayInfo alertDisplayInfo;

    public AlertHubItem(AlertHubClient.AlertDisplayInfo alertDisplayInfo) {
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

        alertName.setIcon(new ImageIcon(ImageUtil.loadImageResource(RuneLite.class, manifest.getCategory().getIcon())));

        JLabel compatVersion = new JLabel(manifest.getCompatibleVersion());
        compatVersion.setHorizontalAlignment(JLabel.RIGHT);
        compatVersion.setFont(FontManager.getRunescapeSmallFont());
        compatVersion.setToolTipText("Compatible with Watchdog v" + manifest.getCompatibleVersion());

        WrappingLabel alertDescLabel = new WrappingLabel(manifest.getDescription());

//        JLabel icon = new JLabel();
//        JLabel icon = new JLabel(new ImageIcon(ImageUtil.loadImageResource(WatchdogPlugin.class, "detail-test.png")));
//        icon.setHorizontalAlignment(JLabel.CENTER);
//        if (this.alertDisplayInfo.getIcon() != null) {
//            icon.setIcon(new ImageIcon(this.alertDisplayInfo.getIcon()));
//        }

        JButton moreInfoButton = PanelUtils.createActionButton(WatchdogPanel.HELP_ICON, WatchdogPanel.HELP_ICON_HOVER, "More info", (btn, mod) -> {
            LinkBrowser.browse(manifest.getRepo().toString());
        });
        if (manifest.getRepo() == null) {
            moreInfoButton.setVisible(false);
        }

        JButton addButton = new JButton();
        addButton.setText("Add");
        BufferedImage addIcon = ImageUtil.recolorImage(WatchdogPanel.ADD_ICON.getImage(), Color.white);
        addButton.setIcon(new ImageIcon(addIcon));
        addButton.setHorizontalTextPosition(SwingConstants.LEFT);
        addButton.setBackground(new Color(0x28BE28));
        addButton.setBorder(new LineBorder(addButton.getBackground().darker()));
        addButton.setFocusPainted(false);
        addButton.addActionListener((ev) -> {
            WatchdogPlugin.getInstance().getAlertManager().addAlert(manifest.getAlert());
            JOptionPane.showMessageDialog(this, "Added " + manifest.getDisplayName() + " to your alerts", "Successfully Added", JOptionPane.INFORMATION_MESSAGE);
        });

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
                .addGap(5)
            )
//            .addComponent(icon, 0, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE)
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
                )
                .addGap(5)
            )
//            .addGroup(layout.createSequentialGroup()
//                .addComponent(icon, 0, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
//            )
        );
    }
}
