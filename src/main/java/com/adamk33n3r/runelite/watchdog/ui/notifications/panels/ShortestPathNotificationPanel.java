package com.adamk33n3r.runelite.watchdog.ui.notifications.panels;

import com.adamk33n3r.runelite.watchdog.notifications.ShortestPath;
import com.adamk33n3r.runelite.watchdog.notifications.ShortestPathMode;
import com.adamk33n3r.runelite.watchdog.ui.panels.PanelUtils;

import net.runelite.api.coords.WorldPoint;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.ui.ColorScheme;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class ShortestPathNotificationPanel extends NotificationContentPanel<ShortestPath> {
    private final ConfigManager configManager;

    public ShortestPathNotificationPanel(ShortestPath notification, ConfigManager configManager, Runnable onChange) {
        super(notification, onChange);
        this.configManager = configManager;
        this.init();
    }

    @Override
    protected void buildContent() {
        String installedPlugins = this.configManager.getConfiguration("runelite", "externalPlugins");
        if (!installedPlugins.contains("shortest-path")) {
            JLabel installShortestPathLabel = new JLabel("<html>Install the Shortest Path plugin to use this Notification type</html>");
            installShortestPathLabel.setFont(new Font(installShortestPathLabel.getFont().getFontName(), Font.ITALIC | Font.BOLD, installShortestPathLabel.getFont().getSize()));
            this.add(installShortestPathLabel);
            return;
        }

        JComboBox<ShortestPathMode> modeSelect = PanelUtils.createSelect(ShortestPathMode.values(), this.notification.getMode(), selected -> {
            this.notification.setMode(selected);
            this.onChange.run();
            this.rebuild();
        });
        JPanel mode = PanelUtils.createLabeledComponent("Mode", "The mode for the Shortest Path plugin", modeSelect);
        mode.setBorder(null);
        mode.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        this.add(mode);

        if (this.notification.isClearMode()) {
            JLabel clearModeLabel = new JLabel("<html>Clear Mode is enabled. This will clear the Shortest Path plugin's path when this notification is fired.</html>");
            this.add(clearModeLabel);
            return;
        }

        JCheckBox useCurrentLocationForStart = PanelUtils.createCheckbox("Use current location", "Use the current location as the start point", this.notification.isUseCurrentLocationForStart(), selected -> {
            this.notification.setUseCurrentLocationForStart(selected);
            this.onChange.run();
            this.rebuild();
        });
        JPanel startPoint = PanelUtils.createLabeledComponent("Start", "The start point for the Shortest Path plugin", useCurrentLocationForStart);
        startPoint.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        startPoint.setBorder(null);
        this.add(startPoint);
        if (!this.notification.isUseCurrentLocationForStart()) {
            this.add(createPointPanel(this.notification::getStart, this.notification::setStart));
        }
        JPanel endPointPanel = PanelUtils.createLabeledComponent("End", "The end point for the Shortest Path plugin", createPointPanel(this.notification::getTarget, this.notification::setTarget), true);
        endPointPanel.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        this.add(endPointPanel);
    }

    private static JPanel createPointPanel(Supplier<WorldPoint> worldPointSupplier, Consumer<WorldPoint> worldPointConsumer) {
        JPanel pointPanel = new JPanel(new GridLayout(1, 3, 5, 5));
        pointPanel.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        JPanel xPos = PanelUtils.createLabeledComponent(
            "X Pos", "The X position",
            PanelUtils.createSpinner(worldPointSupplier.get().getX(), Integer.MIN_VALUE, Integer.MAX_VALUE, 1,
                val -> worldPointConsumer.accept(worldPointSupplier.get().dx(val - worldPointSupplier.get().getX()))),
            true);
        xPos.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        JPanel yPos = PanelUtils.createLabeledComponent(
            "Y Pos", "The Y position",
            PanelUtils.createSpinner(worldPointSupplier.get().getY(), Integer.MIN_VALUE, Integer.MAX_VALUE, 1,
                val -> worldPointConsumer.accept(worldPointSupplier.get().dy(val - worldPointSupplier.get().getY()))),
            true);
        yPos.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        JPanel plane = PanelUtils.createLabeledComponent(
            "Plane", "The plane number",
            PanelUtils.createSpinner(worldPointSupplier.get().getPlane(), Integer.MIN_VALUE, Integer.MAX_VALUE, 1,
                val -> worldPointConsumer.accept(worldPointSupplier.get().dz(val - worldPointSupplier.get().getPlane()))),
            true);
        plane.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        pointPanel.add(xPos);
        pointPanel.add(yPos);
        pointPanel.add(plane);
        return pointPanel;
    }
}
