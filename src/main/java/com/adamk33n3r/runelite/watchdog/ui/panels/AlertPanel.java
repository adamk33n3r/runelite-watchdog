package com.adamk33n3r.runelite.watchdog.ui.panels;

import com.adamk33n3r.runelite.watchdog.*;
import com.adamk33n3r.runelite.watchdog.alerts.AdvancedAlert;
import com.adamk33n3r.runelite.watchdog.alerts.Alert;
import com.adamk33n3r.runelite.watchdog.ui.*;

import net.runelite.client.ui.MultiplexingPluginPanel;
import net.runelite.client.ui.PluginPanel;

import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Sidebar chrome wrapper for an {@link AlertContentPanel}.
 * Provides the back button, type name label, test/import/export buttons, enable toggle,
 * and conditionally the {@link NotificationsPanel} for actions.
 * <p>
 * The type-specific content (including alert defaults) is supplied via the
 * {@link AlertContentPanel} passed at construction time.
 */
@Slf4j
public class AlertPanel<T extends Alert> extends PluginPanel {
    private final AlertContentPanel<T> contentPanel;
    private final WatchdogPanel watchdogPanel;
    private final WatchdogPlugin plugin;
    private final MultiplexingPluginPanel muxer;
    private final AlertManager alertManager;
    private JPanel centerContainer;

    public AlertPanel(WatchdogPanel watchdogPanel, AlertContentPanel<T> contentPanel) {
        super(false);

        this.watchdogPanel = watchdogPanel;
        this.muxer = watchdogPanel.getMuxer();
        this.contentPanel = contentPanel;
        this.plugin = WatchdogPlugin.getInstance();
        this.alertManager = plugin.getAlertManager();

        this.contentPanel.setOnRebuild(() -> this.rebuild(true));

        this.setLayout(new BorderLayout());
        this.buildChrome();
    }

    private void buildChrome() {
        Alert alert = this.contentPanel.alert;

        JPanel northPanel = new JPanel(new StretchedStackedLayout(3));
        this.add(northPanel, BorderLayout.NORTH);

        JPanel nameGroup = new JPanel(new BorderLayout());
        nameGroup.setBorder(new EmptyBorder(10, 5, 10, 5));

        TriggerType triggerType = alert.getType();
        JLabel nameLabel = new JLabel(triggerType.getName());
        nameLabel.setToolTipText(triggerType.getTooltip());
        nameLabel.setForeground(Color.WHITE);
        nameGroup.add(nameLabel, BorderLayout.CENTER);

        JPanel rightButtons = new JPanel(new GridLayout(1, 0));

        if (contentPanel.isAlertGroup()) {
            JButton importAlertBtn = PanelUtils.createActionButton(
                Icons.IMPORT,
                Icons.IMPORT_HOVER,
                "Import alert into this group",
                (btn, modifiers) -> {
                    ImportExportDialog importExportDialog = new ImportExportDialog(
                        SwingUtilities.getWindowAncestor(this),
                        (json, append) -> {
                            boolean result = this.alertManager.importAlerts(json, ((com.adamk33n3r.runelite.watchdog.alerts.AlertGroup) alert).getAlerts(), append, true, WatchdogPlugin.getInstance().getConfig().overrideImportsWithDefaults());
                            SwingUtilities.invokeLater(this::rebuild);
                            return result;
                        }
                    );
                    importExportDialog.setVisible(true);
                }
            );
            rightButtons.add(importAlertBtn);
        } else if (!(contentPanel.alert instanceof AdvancedAlert)) {
            JButton testAlert = PanelUtils.createActionButton(
                Icons.TEST,
                Icons.TEST_HOVER,
                "Test the whole alert",
                (btn, modifiers) -> {
                    String[] triggerValues = {"1", "2", "3", "4", "5"};
                    this.watchdogPanel.getHistoryPanelProvider().get().addEntry(alert, triggerValues);
                    this.plugin.processAlert(alert, triggerValues, true);
                }
            );
            rightButtons.add(testAlert);
        }

        JButton exportAlertBtn = PanelUtils.createActionButton(
            Icons.EXPORT,
            Icons.EXPORT_HOVER,
            "Export this alert",
            (btn, modifiers) -> {
                ImportExportDialog importExportDialog = new ImportExportDialog(
                    SwingUtilities.getWindowAncestor(this),
                    alert
                );
                importExportDialog.setVisible(true);
            }
        );
        rightButtons.add(exportAlertBtn);

        ToggleButton toggleButton = new ToggleButton();
        toggleButton.setSelected(alert.isEnabled());
        toggleButton.addItemListener(i -> {
            alert.setEnabled(toggleButton.isSelected());
            if (!toggleButton.isSelected()) {
                this.plugin.stopAlertProcessors(alert);
            }
            this.alertManager.saveAlerts();
        });
        rightButtons.add(toggleButton);

        nameGroup.add(rightButtons, BorderLayout.EAST);

        JButton backButton = PanelUtils.createActionButton(
            Icons.BACK,
            Icons.BACK_HOVER,
            "Back",
            (btn, modifiers) -> {
                WatchdogPlugin.getInstance().getScreenMarkerUtil().finishCreation(true);
                WatchdogPlugin.getInstance().getObjectMarkerManager().turnOffObjectMarkerMode();
                this.alertManager.saveAlerts();
                this.contentPanel.onBack();
                this.muxer.popState();

                // Workaround for the onActivate rebuild issue
                // TODO remove if it ever gets fixed https://github.com/runelite/runelite/issues/17712
                int componentCount = this.muxer.getComponentCount();
                Component component = this.muxer.getComponent(componentCount - 1);
                if (component instanceof AlertPanel) {
                    ((AlertPanel<?>) component).rebuild();
                }
            }
        );
        backButton.setPreferredSize(new Dimension(22, 16));
        backButton.setBorder(new EmptyBorder(0, 0, 0, 5));
        nameGroup.add(backButton, BorderLayout.WEST);

        northPanel.add(nameGroup);
        northPanel.add(this.contentPanel);

        this.centerContainer = new JPanel(new BorderLayout());
        this.add(this.centerContainer, BorderLayout.CENTER);

        if (this.contentPanel.hasSubsection()) {
            this.centerContainer.add(this.contentPanel.getSubsection());
        }

        if (this.contentPanel.includeNotifications()) {
            this.addNotifications(alert);
        }
    }

    private void addNotifications(Alert alert) {
        NotificationsPanel notificationPanel = WatchdogPlugin.getInstance().getInjector().getInstance(NotificationsPanel.class);
        notificationPanel.init(alert);
        notificationPanel.setBorder(new CompoundBorder(new EmptyBorder(0, 5, 0, 5), new HorizontalRuleBorder(10)));
        this.centerContainer.add(notificationPanel);
    }

    public void rebuild() {
        this.rebuild(false);
    }

    public void rebuild(boolean skipContent) {
        this.removeAll();
        if (!skipContent) {
            this.contentPanel.rebuild();
        }
        this.buildChrome();
        this.revalidate();
        this.repaint();
    }

    @Override
    public void onActivate() {
        // See commented-out code in original AlertPanel — resize issues when rebuilding on activate.
    }
}
