package com.adamk33n3r.runelite.watchdog.panels.notifications;

import com.adamk33n3r.runelite.watchdog.NotificationType;
import com.adamk33n3r.runelite.watchdog.SimpleDocumentListener;
import com.adamk33n3r.runelite.watchdog.WatchdogPanel;
import com.adamk33n3r.runelite.watchdog.WatchdogPlugin;
import com.adamk33n3r.runelite.watchdog.dropdownbutton.DropDownButtonFactory;
import com.adamk33n3r.runelite.watchdog.notifications.*;
import com.adamk33n3r.runelite.watchdog.panels.PanelUtils;
import com.google.inject.Injector;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.ui.DynamicGridLayout;
import org.apache.commons.text.WordUtils;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioSystem;
import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileFilter;

import java.awt.*;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.adamk33n3r.runelite.watchdog.WatchdogPanel.ADD_ICON;

@Slf4j
public class NotificationsPanel extends JPanel {
    @Getter
    private final List<INotification> notifications = new ArrayList<>();

    private final JPanel container;
    private final JPanel notificationContainer;

    public NotificationsPanel(List<INotification> notifications) {
        this.notifications.addAll(notifications);
        this.setLayout(new BorderLayout());
        this.container = new JPanel(new DynamicGridLayout(0, 1, 3, 3));
        this.notificationContainer = new JPanel(new GridLayout(0, 1, 3, 3));

        JPopupMenu popupMenu = new JPopupMenu();
        ActionListener actionListener = e -> {
            JMenuItem menuItem = (JMenuItem) e.getSource();
            NotificationType nType = (NotificationType) menuItem.getClientProperty(NotificationType.class);
            log.info("Create notification: " + nType.name());
            this.createNotification(nType);
            this.rebuild();
        };
        for (NotificationType nType : NotificationType.values()) {
            JMenuItem c = new JMenuItem(WordUtils.capitalizeFully(nType.name().replace("_", " ")));
            c.putClientProperty(NotificationType.class, nType);
            c.addActionListener(actionListener);
            popupMenu.add(c);
        }
        JButton addDropDownButton = DropDownButtonFactory.createDropDownButton(ADD_ICON, popupMenu, true);
        addDropDownButton.setToolTipText("Create New Notification");
        JPanel buttonPanel = new JPanel(new BorderLayout());
        buttonPanel.add(addDropDownButton, BorderLayout.EAST);

        this.container.add(buttonPanel);
        this.container.add(new JSeparator());
        this.container.add(notificationContainer);
        this.add(container, BorderLayout.NORTH);
        this.rebuild();
    }

    private void rebuild() {
        this.notificationContainer.removeAll();

        // TODO: add client-in-focus checkbox for each notification
        // TODO: or maybe a dropdown for in-focus, not-focus, both

        for (INotification notification : this.notifications) {
            JPanel notifPanel = new JPanel(new DynamicGridLayout(0, 1, 3, 3));
            notifPanel.setBorder(new TitledBorder(new EtchedBorder(), notification.getClass().getSimpleName(), TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, Color.WHITE));
            this.notificationContainer.add(notifPanel);
            if (notification instanceof NotificationWithMessage) {
                NotificationWithMessage gameMessage = (NotificationWithMessage) notification;
                log.info(gameMessage.message);
                JTextField notificationMessage = new JTextField(gameMessage.message);
                notificationMessage.getDocument().addDocumentListener((SimpleDocumentListener) ev -> {
                    gameMessage.message = notificationMessage.getText();
                });
                notifPanel.add(PanelUtils.createLabeledComponent("Message", notificationMessage));
                JButton remove = new JButton("Remove");
                remove.addActionListener(ev -> {
                    this.notifications.remove(notification);
                    this.notificationContainer.remove(notifPanel);
                    this.notificationContainer.revalidate();
                });
                notifPanel.add(remove);
            } else if (notification instanceof Sound) {
                Sound sound = (Sound) notification;
                JButton testButton = new JButton("Test");
                notifPanel.add(PanelUtils.createFileChooser("Sound Path", ev -> {
                    JFileChooser fileChooser = (JFileChooser) ev.getSource();
                    sound.path = fileChooser.getSelectedFile().getAbsolutePath();
                    log.info("selected a file");
                    log.info(sound.path);
                    testButton.setEnabled(sound.path != null);
                }, sound.path, "Sound Files", Arrays.stream(AudioSystem.getAudioFileTypes()).map(AudioFileFormat.Type::getExtension).toArray(String[]::new)));
                testButton.setEnabled(sound.path != null);
                testButton.addActionListener(ev -> {
                    sound.fire(WatchdogPlugin.getInstance());
                });
                notifPanel.add(testButton);
            }
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
