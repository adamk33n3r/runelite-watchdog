package com.adamk33n3r.runelite.watchdog.ui.panels;

import com.adamk33n3r.runelite.watchdog.*;
import com.adamk33n3r.runelite.watchdog.ui.dropdownbutton.DropDownButtonFactory;
import com.adamk33n3r.runelite.watchdog.notifications.*;
import com.adamk33n3r.runelite.watchdog.ui.notifications.panels.MessageNotificationPanel;
import com.adamk33n3r.runelite.watchdog.ui.notifications.panels.ScreenFlashNotificationPanel;
import com.adamk33n3r.runelite.watchdog.ui.notifications.panels.SoundNotificationPanel;
import com.adamk33n3r.runelite.watchdog.ui.notifications.panels.TextToSpeechNotificationPanel;
import com.google.inject.Injector;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.ui.DynamicGridLayout;
import net.runelite.client.ui.components.colorpicker.ColorPickerManager;
import org.apache.commons.text.WordUtils;

import javax.inject.Inject;
import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionListener;
import java.util.List;

import static com.adamk33n3r.runelite.watchdog.WatchdogPanel.ADD_ICON;

@Slf4j
public class NotificationsPanel extends JPanel {
    @Getter
    private final List<Notification> notifications;
    @Inject
    private ColorPickerManager colorPickerManager;

    @Inject
    private AlertManager alertManager;

    private final JPanel notificationContainer;

    public NotificationsPanel(List<Notification> notifications) {
        this.notifications = notifications;
        this.setLayout(new DynamicGridLayout(0, 1, 3, 3));
        this.notificationContainer = new JPanel(new DynamicGridLayout(0, 1, 3, 3));

        JPopupMenu popupMenu = new JPopupMenu();
        ActionListener actionListener = e -> {
            JMenuItem menuItem = (JMenuItem) e.getSource();
            NotificationType nType = (NotificationType) menuItem.getClientProperty(NotificationType.class);
            this.createNotification(nType);
            this.addPanel(this.notifications.get(this.notifications.size() - 1));
            this.notificationContainer.revalidate();
            this.alertManager.saveAlerts();
        };
        for (NotificationType nType : NotificationType.values()) {
            JMenuItem c = new JMenuItem(WordUtils.capitalizeFully(nType.name().replace("_", " ")));
            c.putClientProperty(NotificationType.class, nType);
            c.addActionListener(actionListener);
            popupMenu.add(c);
        }
        JButton addDropDownButton = DropDownButtonFactory.createDropDownButton(ADD_ICON, popupMenu);
        addDropDownButton.setToolTipText("Create New Notification");
        JPanel buttonPanel = new JPanel(new BorderLayout());
        buttonPanel.add(new JLabel("Notifications"), BorderLayout.WEST);
        buttonPanel.add(addDropDownButton, BorderLayout.EAST);

        this.add(buttonPanel);
        this.add(notificationContainer);
    }

    // After inject, build
    @Inject
    private void rebuild() {
        this.notificationContainer.removeAll();

        for (Notification notification : this.notifications) {
            this.addPanel(notification);
        }

        this.notificationContainer.revalidate();
    }

    private void addPanel(Notification notification) {
        JPanel notificationPanel = new JPanel(new DynamicGridLayout(0, 1, 3, 3));
        this.notificationContainer.add(notificationPanel);

        PanelUtils.ButtonClickListener removeNotification = btn -> {
            this.notifications.remove(notification);
            this.notificationContainer.remove(notificationPanel);
            this.notificationContainer.revalidate();
            this.alertManager.saveAlerts();
        };

        if (notification instanceof GameMessage)
            notificationPanel.add(new MessageNotificationPanel((GameMessage)notification, this.alertManager::saveAlerts, removeNotification));
        else if (notification instanceof TextToSpeech)
            notificationPanel.add(new TextToSpeechNotificationPanel((TextToSpeech) notification, this.alertManager::saveAlerts, removeNotification));
        else if (notification instanceof Sound)
            notificationPanel.add(new SoundNotificationPanel((Sound)notification, this.alertManager::saveAlerts, removeNotification));
        else if (notification instanceof TrayNotification)
            notificationPanel.add(new MessageNotificationPanel((TrayNotification)notification, this.alertManager::saveAlerts, removeNotification));
        else if (notification instanceof ScreenFlash)
            notificationPanel.add(new ScreenFlashNotificationPanel((ScreenFlash) notification, this.colorPickerManager, this.alertManager::saveAlerts, removeNotification));
    }

    private void createNotification(NotificationType notificationType) {
        Injector injector = WatchdogPlugin.getInstance().getInjector();
        switch (notificationType) {
            case GAME_MESSAGE:
                this.notifications.add(injector.getInstance(GameMessage.class));
                break;
            case SCREEN_FLASH:
                this.notifications.add(injector.getInstance(ScreenFlash.class));
                break;
            case TEXT_TO_SPEECH:
                this.notifications.add(injector.getInstance(TextToSpeech.class));
                break;
            case SOUND:
                this.notifications.add(injector.getInstance(Sound.class));
                break;
            case TRAY_NOTIFICATION:
                this.notifications.add(injector.getInstance(TrayNotification.class));
                break;
        }
    }
}
