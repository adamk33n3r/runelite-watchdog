package com.adamk33n3r.runelite.watchdog.hub;

import com.adamk33n3r.runelite.watchdog.WatchdogPanel;
import com.adamk33n3r.runelite.watchdog.WatchdogPlugin;
import com.adamk33n3r.runelite.watchdog.alerts.Alert;
import com.adamk33n3r.runelite.watchdog.alerts.AlertGroup;
import com.adamk33n3r.runelite.watchdog.ui.WrappingLabel;
import com.adamk33n3r.runelite.watchdog.ui.panels.PanelUtils;
import com.google.gson.Gson;
import lombok.Getter;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.PluginPanel;
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
//        this.setMaximumSize(new Dimension(PluginPanel.PANEL_WIDTH + PluginPanel.SCROLLBAR_WIDTH, Short.MAX_VALUE));

        GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);

        JLabel alertName = new JLabel(this.alertDisplayInfo.getManifest().getDisplayName());
        alertName.setFont(FontManager.getRunescapeBoldFont());
        alertName.setToolTipText(this.alertDisplayInfo.getManifest().getDisplayName());
        JLabel alertAuthor = new JLabel(this.alertDisplayInfo.getManifest().getAuthor());
        alertAuthor.setFont(FontManager.getRunescapeSmallFont());
        alertAuthor.setToolTipText(this.alertDisplayInfo.getManifest().getAuthor());
//        alertAuthor.setText("te9");

        JLabel compatVersion = new JLabel(this.alertDisplayInfo.getManifest().getCompatibleVersion());
        compatVersion.setFont(FontManager.getRunescapeSmallFont());
        compatVersion.setToolTipText("Compatible with Watchdog v" + this.alertDisplayInfo.getManifest().getCompatibleVersion());


//        JPanel titlePanel = new JPanel(new BorderLayout());
//        titlePanel.add(alertName, BorderLayout.WEST);
//        titlePanel.add(alertAuthor, BorderLayout.EAST);
//        titlePanel.setBackground(ColorScheme.DARKER_GRAY_COLOR);

        WrappingLabel alertDescLabel = new WrappingLabel(this.alertDisplayInfo.getManifest().getDescription());
        alertDescLabel.setForeground(Color.CYAN);

        JLabel icon = new JLabel(new ImageIcon(this.alertDisplayInfo.getIcon()));
        icon.setHorizontalAlignment(JLabel.CENTER);

        JButton moreInfoButton = PanelUtils.createActionButton(WatchdogPanel.HELP_ICON, WatchdogPanel.HELP_ICON_HOVER, "More info", (btn, mod) -> {
            LinkBrowser.browse(this.alertDisplayInfo.getManifest().getRepo().toString());
        });
        if (this.alertDisplayInfo.getManifest().getRepo() == null) {
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
            WatchdogPlugin.getInstance().getAlertManager().addAlert(this.alertDisplayInfo.getManifest().getAlert());
            JOptionPane.showMessageDialog(this, "Added " + this.alertDisplayInfo.getManifest().getDisplayName() + "to your alerts", "Successfully Added", JOptionPane.INFORMATION_MESSAGE);
        });

        layout.setAutoCreateContainerGaps(true);
        layout.setVerticalGroup(layout.createSequentialGroup()
            .addGap(5)
            .addGroup(layout.createParallelGroup()
                .addComponent(alertName)
                .addComponent(moreInfoButton, LINE_HEIGHT, LINE_HEIGHT, LINE_HEIGHT)
                .addComponent(addButton, LINE_HEIGHT, LINE_HEIGHT, LINE_HEIGHT))
            .addGap(5)
            .addGroup(layout.createParallelGroup()
                .addComponent(alertAuthor)
                .addComponent(compatVersion)
            )
            .addComponent(alertDescLabel)
            .addComponent(icon, 147, GroupLayout.DEFAULT_SIZE, 147)
        );

        layout.setHorizontalGroup(layout.createSequentialGroup()
            .addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                .addGroup(layout.createSequentialGroup()
                    .addComponent(alertName, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
                    .addComponent(moreInfoButton, 0, 24, 24)
                    .addComponent(addButton, 0, 50, GroupLayout.PREFERRED_SIZE)
                )
                .addGroup(layout.createSequentialGroup()
                    .addComponent(alertAuthor, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
                    .addComponent(compatVersion, 0, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                )
                .addComponent(alertDescLabel)
                .addComponent(icon, PluginPanel.PANEL_WIDTH, PluginPanel.PANEL_WIDTH, PluginPanel.PANEL_WIDTH)
            )
        );
    }
}
