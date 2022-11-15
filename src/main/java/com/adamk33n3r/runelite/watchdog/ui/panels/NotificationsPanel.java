package com.adamk33n3r.runelite.watchdog.ui.panels;

import com.adamk33n3r.runelite.watchdog.*;
import com.adamk33n3r.runelite.watchdog.ui.dropdownbutton.DropDownButtonFactory;
import com.adamk33n3r.runelite.watchdog.notifications.*;
import com.adamk33n3r.runelite.watchdog.notifications.tts.Voice;
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

    private final JPanel container;
    private final JPanel notificationContainer;

    public NotificationsPanel(List<Notification> notifications) {
        this.notifications.addAll(notifications);
        this.setLayout(new BorderLayout());
        this.container = new JPanel(new DynamicGridLayout(0, 1, 3, 3));
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

        this.container.add(buttonPanel);
        this.container.add(notificationContainer);
        this.add(container, BorderLayout.NORTH);
        this.rebuild();
    }

    private void rebuild() {
        this.notificationContainer.removeAll();

        for (Notification notification : this.notifications) {
            JPanel notificationPanel = new JPanel(new DynamicGridLayout(0, 1, 3, 3));
            notificationPanel.setBorder(new TitledBorder(new EtchedBorder(), Util.humanReadableClass(notification), TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, Color.WHITE));
            this.notificationContainer.add(notificationPanel);

            if (notification instanceof TextToSpeech && !WatchdogPlugin.getInstance().getConfig().ttsEnabled()) {
                JLabel ttsLabel = new JLabel("<html>Enable TTS in the config to use this Notification type</html>");
                ttsLabel.setFont(new Font(ttsLabel.getFont().getFontName(), Font.ITALIC | Font.BOLD, ttsLabel.getFont().getSize()));
                notificationPanel.add(ttsLabel);
            } else {
                JCheckBox fireWhenFocused = new JCheckBox("Fire when in focus", notification.isFireWhenFocused());
                fireWhenFocused.setToolTipText("Allows the notification to fire when the game is focused");
                fireWhenFocused.addChangeListener(ev -> {
                    notification.setFireWhenFocused(fireWhenFocused.isSelected());
                });
                notificationPanel.add(fireWhenFocused);

                if (notification instanceof IMessageNotification) {
                    IMessageNotification gameMessage = (IMessageNotification) notification;
                    JTextField notificationMessage = new JTextField(gameMessage.getMessage());
                    ((AbstractDocument) notificationMessage.getDocument()).setDocumentFilter(new LengthLimitFilter(200));
                    notificationMessage.getDocument().addDocumentListener((SimpleDocumentListener) ev -> {
                        gameMessage.setMessage(notificationMessage.getText());
                    });
                    notificationPanel.add(PanelUtils.createLabeledComponent("Message", "Message to use for the notification", notificationMessage));
                }

                if (notification instanceof Sound) {
                    Sound sound = (Sound) notification;
                    String[] supportedExtensions = Arrays.stream(AudioSystem.getAudioFileTypes()).map(AudioFileFormat.Type::getExtension).toArray(String[]::new);
                    notificationPanel.add(new JLabel("Supports " + String.join(", ", Arrays.stream(supportedExtensions).map(ext -> "."+ext).toArray(String[]::new))));
                    notificationPanel.add(PanelUtils.createFileChooser("Sound Path", "Path to the sound file", ev -> {
                        JFileChooser fileChooser = (JFileChooser) ev.getSource();
                        sound.path = fileChooser.getSelectedFile().getAbsolutePath();
                    }, sound.path, "Sound Files", supportedExtensions));
                } else if (notification instanceof TextToSpeech) {
                    TextToSpeech tts = (TextToSpeech) notification;
                    JSlider rateSlider = new JSlider(1, 5, tts.getRate());
                    rateSlider.addChangeListener(ev -> {
                        tts.setRate(rateSlider.getValue());
                    });
                    notificationPanel.add(PanelUtils.createLabeledComponent("Rate", "The speed of the generated speech", rateSlider));
                    JComboBox<Voice> voiceChooser = new JComboBox<>(Voice.values());
                    voiceChooser.setSelectedItem(tts.getVoice());
                    voiceChooser.setRenderer((list, value, index, isSelected, cellHasFocus) -> {
                        list.setToolTipText(value.toString());
                        return new DefaultListCellRenderer().getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                    });
                    voiceChooser.addActionListener(ev -> {
                        tts.setVoice(voiceChooser.getItemAt(voiceChooser.getSelectedIndex()));
                    });
                    notificationPanel.add(PanelUtils.createLabeledComponent("Voice", "The voice to generate speech with", voiceChooser));
                } else if (notification instanceof ScreenFlash) {
                    ScreenFlash screenFlash = (ScreenFlash) notification;
                    ColorJButton colorPickerBtn;
                    Color existing = screenFlash.color;
                    if (existing == null) {
                        colorPickerBtn = new ColorJButton("Pick a color", Color.BLACK);
                    } else {
                        String colorHex = "#" + ColorUtil.colorToAlphaHexCode(existing);
                        colorPickerBtn = new ColorJButton(colorHex, existing);
                    }
                    colorPickerBtn.setToolTipText("The color to flash the screen");
                    colorPickerBtn.setFocusable(false);
                    colorPickerBtn.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseClicked(MouseEvent e) {
                            RuneliteColorPicker colorPicker = colorPickerManager.create(
                                SwingUtilities.windowForComponent(NotificationsPanel.this),
                                colorPickerBtn.getColor(),
                                "Flash Color",
                                false);
                            colorPicker.setLocation(getLocationOnScreen());
                            colorPicker.setOnColorChange(c -> {
                                colorPickerBtn.setColor(c);
                                colorPickerBtn.setText("#" + ColorUtil.colorToAlphaHexCode(c).toUpperCase());
                            });
                            colorPicker.setOnClose(c -> screenFlash.color = c);
                            colorPicker.setVisible(true);
                        }
                    });
                    notificationPanel.add(colorPickerBtn);
                    JComboBox<FlashNotification> flashNotificationSelect = new JComboBox<>(Arrays.stream(FlashNotification.values()).filter(fn -> fn != FlashNotification.DISABLED).toArray(FlashNotification[]::new));
                    flashNotificationSelect.setToolTipText("The screen flash mode");
                    flashNotificationSelect.setSelectedItem(screenFlash.flashNotification);
                    flashNotificationSelect.setRenderer((list, value, index, isSelected, cellHasFocus) -> {
                        list.setToolTipText(value.toString());
                        return new DefaultListCellRenderer().getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                    });
                    flashNotificationSelect.addActionListener(e -> screenFlash.flashNotification = flashNotificationSelect.getItemAt(flashNotificationSelect.getSelectedIndex()));
                    notificationPanel.add(flashNotificationSelect);
                }

                if (notification instanceof IAudioNotification) {
                    IAudioNotification audioNotification = (IAudioNotification) notification;
                    JSlider volumeSlider = new JSlider(0, 10, (audioNotification.getGain() + 25) / 3);
                    //                volumeSlider.setPaintTicks(true);
                    volumeSlider.setSnapToTicks(true);
                    volumeSlider.setMajorTickSpacing(5);
                    volumeSlider.setMinorTickSpacing(1);
                    volumeSlider.addChangeListener(ev -> {
                        audioNotification.setGain(volumeSlider.getValue() * 3 - 25);
                    });
                    notificationPanel.add(PanelUtils.createLabeledComponent("Volume", "The volume to playback speech", volumeSlider));
                }

                JButton testButton = new JButton("Test");
                testButton.setToolTipText("Test the notification");
                testButton.addActionListener(ev -> {
                    boolean prev = notification.isFireWhenFocused();
                    notification.setFireWhenFocused(true);
                    notification.fire();
                    notification.setFireWhenFocused(prev);
                });
                notificationPanel.add(testButton);
            }

            JButton remove = new JButton("Remove");
            remove.setToolTipText("Remove this notification");
            remove.addActionListener(ev -> {
                this.notifications.remove(notification);
                this.notificationContainer.remove(notificationPanel);
                this.notificationContainer.revalidate();
            });
            notificationPanel.add(remove);
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
