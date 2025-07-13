package com.adamk33n3r.runelite.watchdog.ui.panels;

import com.adamk33n3r.runelite.watchdog.*;
import com.adamk33n3r.runelite.watchdog.alerts.Alert;
import com.adamk33n3r.runelite.watchdog.notifications.*;
import com.adamk33n3r.runelite.watchdog.notifications.Popup;
import com.adamk33n3r.runelite.watchdog.ui.Icons;
import com.adamk33n3r.runelite.watchdog.ui.StretchedStackedLayout;
import com.adamk33n3r.runelite.watchdog.ui.dropdownbutton.DropDownButtonFactory;
import com.adamk33n3r.runelite.watchdog.ui.notifications.panels.PluginMessageNotificationPanel;
import com.adamk33n3r.runelite.watchdog.ui.notifications.panels.*;

import net.runelite.client.config.ConfigManager;
import net.runelite.client.ui.DynamicGridLayout;
import net.runelite.client.ui.components.DragAndDropReorderPane;
import net.runelite.client.ui.components.colorpicker.ColorPickerManager;

import com.google.inject.Injector;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.Comparator;

@Slf4j
public class NotificationsPanel extends JPanel {
    private Alert alert;

    @Inject
    private ColorPickerManager colorPickerManager;

    @Inject
    private ConfigManager configManager;

    @Inject
    private WatchdogConfig config;

    @Inject
    private AlertManager alertManager;

    @Getter
    private final DragAndDropReorderPane notificationContainer;

    public NotificationsPanel() {
        this.setLayout(new BorderLayout(0, 5));
        this.notificationContainer = new DragAndDropReorderPane();
        this.notificationContainer.addDragListener((c) -> {
            int pos = this.notificationContainer.getPosition(c);
            NotificationPanel notificationPanel = (NotificationPanel) c;
            Notification notification = notificationPanel.getNotification();
//            log.debug("drag listener: " + notification.getType().getName() + " to " + pos);
            notification.getAlert().moveNotificationTo(notification, pos);
            this.alertManager.saveAlerts();
        });
    }

    public void init(Alert alert) {
        this.alert = alert;

        JPopupMenu popupMenu = new JPopupMenu();
        ActionListener actionListener = e -> {
            JMenuItem menuItem = (JMenuItem) e.getSource();
            NotificationType nType = (NotificationType) menuItem.getClientProperty(NotificationType.class);
            this.addPanel(this.createNotification(nType));
            this.notificationContainer.revalidate();
            this.alertManager.saveAlerts();
        };

        if (this.config.enableNotificationCategories()) {
            Arrays.stream(NotificationCategory.values()).forEach(cat -> {
                JMenu subMenu = new JMenu(cat.getName());
                subMenu.setToolTipText(cat.getTooltip());
                popupMenu.add(subMenu);
                popupMenu.putClientProperty(cat.name(), subMenu);
            });
        }
        Arrays.stream(NotificationType.values()).sorted(Comparator.comparing(NotificationType::getName)).forEach(nType -> {
            JMenuItem c = new JMenuItem(nType.getName());
            c.setToolTipText(nType.getTooltip());
            c.putClientProperty(NotificationType.class, nType);
            c.addActionListener(actionListener);
            if (this.config.enableNotificationCategories()) {
                JMenu subMenu = (JMenu) popupMenu.getClientProperty(nType.getCategory().name());
                subMenu.add(c);
            } else {
                popupMenu.add(c);
            }
        });
        JButton addDropDownButton = DropDownButtonFactory.createDropDownButton(Icons.ADD, popupMenu);
        addDropDownButton.setPreferredSize(new Dimension(40, addDropDownButton.getPreferredSize().height));
        addDropDownButton.setToolTipText("Create New Notification");
        JPanel headerPanel = new JPanel(new BorderLayout());
        JPanel leftPanel = new JPanel(new DynamicGridLayout(1, 0, 5, 5));
        headerPanel.add(leftPanel, BorderLayout.WEST);
        leftPanel.add(new JLabel("Notifications"));
        JButton randomBtn = PanelUtils.createToggleActionButton(
            Icons.DICE_MULTIPLE,
            Icons.DICE_MULTIPLE_HOVER,
            Icons.DICE_MULTIPLE_OFF,
            Icons.DICE_MULTIPLE_OFF_HOVER,
            "Fire all notifications in sequence",
            "Fire a random notification",
            alert.isRandomNotifications(),
            (btn, mods) -> {
                alert.setRandomNotifications(!alert.isRandomNotifications());
                this.alertManager.saveAlerts();
            }
        );
        leftPanel.add(randomBtn);
        JPanel buttonPanel = new JPanel(new DynamicGridLayout(1, 0));
        buttonPanel.add(addDropDownButton);
        headerPanel.add(buttonPanel, BorderLayout.EAST);
        headerPanel.setBorder(new EmptyBorder(0, 5, 0, 0));

        this.add(headerPanel, BorderLayout.NORTH);

        ScrollablePanel scrollablePanel = new ScrollablePanel(new StretchedStackedLayout(3));
        scrollablePanel.setScrollableWidth(ScrollablePanel.ScrollableSizeHint.FIT);
        scrollablePanel.setScrollableHeight(ScrollablePanel.ScrollableSizeHint.STRETCH);
        scrollablePanel.setScrollableBlockIncrement(ScrollablePanel.VERTICAL, ScrollablePanel.IncrementType.PERCENT, 10);
        scrollablePanel.add(this.notificationContainer);
        JScrollPane scrollPane = new JScrollPane(scrollablePanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        this.add(scrollPane, BorderLayout.CENTER);

        for (Notification notification : this.alert.getNotifications()) {
            this.addPanel(notification);
        }
    }

    private void addPanel(Notification notification) {
        PanelUtils.OnRemove removeNotification = (removedPanel) -> {
            this.alert.getNotifications().remove(notification);
            this.notificationContainer.remove(removedPanel);
            this.notificationContainer.revalidate();
            this.alertManager.saveAlerts();
        };

        NotificationPanel notificationPanel = null;
        if (notification instanceof GameMessage) {
            notificationPanel = new MessageNotificationPanel((GameMessage) notification, true, this, this.alertManager::saveAlerts, removeNotification);
        } else if (notification instanceof TextToSpeech)
            notificationPanel = new TextToSpeechNotificationPanel((TextToSpeech) notification, this, this.alertManager::saveAlerts, removeNotification);
        else if (notification instanceof Sound)
            notificationPanel = new SoundNotificationPanel((Sound)notification, this, this.alertManager::saveAlerts, removeNotification);
        else if (notification instanceof SoundEffect)
            notificationPanel = new SoundEffectNotificationPanel((SoundEffect)notification, this, this.alertManager::saveAlerts, removeNotification);
        else if (notification instanceof TrayNotification)
            notificationPanel = new MessageNotificationPanel((TrayNotification)notification, this, this.alertManager::saveAlerts, removeNotification);
        else if (notification instanceof ScreenFlash)
            notificationPanel = new ScreenFlashNotificationPanel((ScreenFlash) notification, this, this.colorPickerManager, this.alertManager::saveAlerts, removeNotification);
        else if (notification instanceof Overhead)
            notificationPanel = new OverheadNotificationPanel((Overhead) notification, this, this.colorPickerManager, this.alertManager::saveAlerts, removeNotification);
        else if (notification instanceof Overlay)
            notificationPanel = new OverlayNotificationPanel((Overlay) notification, this, this.colorPickerManager, this.alertManager::saveAlerts, removeNotification);
        else if (notification instanceof Popup)
            notificationPanel = new PopupNotificationPanel((Popup) notification, this, this.colorPickerManager, this.alertManager::saveAlerts, removeNotification);
        else if (notification instanceof RequestFocus)
            notificationPanel = new RequestFocusNotificationPanel((RequestFocus) notification, this, this.alertManager::saveAlerts, removeNotification);
        else if (notification instanceof NotificationEvent)
            notificationPanel = new MessageNotificationPanel((NotificationEvent) notification, this, this.alertManager::saveAlerts, removeNotification);
        else if (notification instanceof DismissOverlay)
            notificationPanel = new DismissOverlayNotificationPanel((DismissOverlay) notification, this, this.alertManager::saveAlerts, removeNotification);
        else if (notification instanceof DismissScreenMarker)
            notificationPanel = new DismissScreenMarkerNotificationPanel((DismissScreenMarker) notification, this, this.alertManager::saveAlerts, removeNotification);
        else if (notification instanceof ScreenMarker)
            notificationPanel = new ScreenMarkerNotificationPanel((ScreenMarker) notification, this, this.colorPickerManager, this.alertManager::saveAlerts, removeNotification);
        else if (notification instanceof Dink)
            notificationPanel = new DinkNotificationPanel((Dink) notification, this, this.configManager, this.alertManager::saveAlerts, removeNotification);
        else if (notification instanceof PluginMessage)
            notificationPanel = new PluginMessageNotificationPanel((PluginMessage) notification, this, this.alertManager::saveAlerts, removeNotification);

        if (notificationPanel != null)
            this.notificationContainer.add(notificationPanel);
    }

    private Notification createNotification(NotificationType notificationType) {
        Injector injector = WatchdogPlugin.getInstance().getInjector();
        Notification notification = injector.getInstance(notificationType.getImplClass());
        notification.setAlert(this.alert);
        this.alert.getNotifications().add(notification);
        return notification;
    }
}
