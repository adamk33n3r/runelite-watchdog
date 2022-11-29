package com.adamk33n3r.runelite.watchdog.ui.panels;

import com.adamk33n3r.runelite.watchdog.*;
import com.adamk33n3r.runelite.watchdog.ui.dropdownbutton.DropDownButtonFactory;
import com.adamk33n3r.runelite.watchdog.notifications.*;
import com.adamk33n3r.runelite.watchdog.notifications.tts.Voice;
import com.adamk33n3r.runelite.watchdog.ui.notifications.panels.MessageNotificationPanel;
import com.adamk33n3r.runelite.watchdog.ui.notifications.panels.ScreenFlashNotificationPanel;
import com.adamk33n3r.runelite.watchdog.ui.notifications.panels.SoundNotificationPanel;
import com.adamk33n3r.runelite.watchdog.ui.notifications.panels.TextToSpeechNotificationPanel;
import com.google.inject.Injector;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.config.FlashNotification;
import net.runelite.client.ui.DynamicGridLayout;
import net.runelite.client.ui.components.ColorJButton;
import net.runelite.client.ui.components.colorpicker.ColorPickerManager;
import net.runelite.client.ui.components.colorpicker.RuneliteColorPicker;
import net.runelite.client.util.ColorUtil;
import org.apache.commons.text.WordUtils;

import javax.inject.Inject;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioSystem;
import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.text.AbstractDocument;

import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.adamk33n3r.runelite.watchdog.WatchdogPanel.ADD_ICON;

@Slf4j
public class NotificationsPanel extends JPanel {
    @Getter
    private final List<Notification> notifications = new ArrayList<>();
    @Inject
    private ColorPickerManager colorPickerManager;

    private final JPanel notificationContainer;

    public NotificationsPanel(List<Notification> notifications) {
        this.notifications.addAll(notifications);
        this.setLayout(new DynamicGridLayout(0, 1, 3, 3));
        this.notificationContainer = new JPanel(new DynamicGridLayout(0, 1, 3, 3));

        JPopupMenu popupMenu = new JPopupMenu();
        ActionListener actionListener = e -> {
            JMenuItem menuItem = (JMenuItem) e.getSource();
            NotificationType nType = (NotificationType) menuItem.getClientProperty(NotificationType.class);
            this.createNotification(nType);
            this.rebuild();
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
        buttonPanel.add(addDropDownButton, BorderLayout.EAST);

        this.add(buttonPanel);
        this.add(notificationContainer);
//        this.rebuild();
    }

    // After inject, build
    @Inject
    private void rebuild() {
        this.notificationContainer.removeAll();

        for (Notification notification : this.notifications) {

            JPanel notificationPanel = new JPanel(new DynamicGridLayout(0, 1, 3, 3));
//            notificationPanel.setBorder(new TitledBorder(new EtchedBorder(), Util.humanReadableClass(notification), TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, Color.WHITE));
            this.notificationContainer.add(notificationPanel);

            if (notification instanceof GameMessage)
                notificationPanel.add(new MessageNotificationPanel((GameMessage)notification));
            else if (notification instanceof TextToSpeech)
                notificationPanel.add(new TextToSpeechNotificationPanel((TextToSpeech) notification));
            else if (notification instanceof Sound)
                notificationPanel.add(new SoundNotificationPanel((Sound)notification));
            else if (notification instanceof TrayNotification)
                notificationPanel.add(new MessageNotificationPanel((TrayNotification)notification));
            else if (notification instanceof ScreenFlash)
                notificationPanel.add(new ScreenFlashNotificationPanel((ScreenFlash) notification, this.colorPickerManager));

            JPanel footer = new JPanel(new GridLayout(1, 2, 3, 3));
//            notificationPanel.add(footer);

            JButton testButton = new JButton("Test");
            testButton.setToolTipText("Test the notification");
            testButton.addActionListener(ev -> {
                boolean prev = notification.isFireWhenFocused();
                notification.setFireWhenFocused(true);
                notification.fire();
                notification.setFireWhenFocused(prev);
            });
            footer.add(testButton);

            JButton remove = new JButton("Remove");
            remove.setToolTipText("Remove this notification");
            remove.addActionListener(ev -> {
                this.notifications.remove(notification);
                this.notificationContainer.remove(notificationPanel);
                this.notificationContainer.revalidate();
            });
            footer.add(remove);
        }
        this.notificationContainer.revalidate();
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
