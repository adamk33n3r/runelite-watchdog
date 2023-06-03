package com.adamk33n3r.runelite.watchdog.ui.notifications.panels;

import com.adamk33n3r.runelite.watchdog.LengthLimitFilter;
import com.adamk33n3r.runelite.watchdog.SimpleDocumentListener;
import com.adamk33n3r.runelite.watchdog.notifications.MessageNotification;
import com.adamk33n3r.runelite.watchdog.ui.FlatTextArea;
import com.adamk33n3r.runelite.watchdog.ui.panels.NotificationsPanel;
import com.adamk33n3r.runelite.watchdog.ui.panels.PanelUtils;

import javax.swing.text.AbstractDocument;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

public class MessageNotificationPanel extends NotificationPanel {
    public MessageNotificationPanel(MessageNotification notification, NotificationsPanel parentPanel, Runnable onChangeListener, PanelUtils.OnRemove onRemove) {
        super(notification, parentPanel, onChangeListener, onRemove);

        FlatTextArea flatTextArea = new FlatTextArea("Enter your message...", true);
        flatTextArea.setText(notification.getMessage());
        ((AbstractDocument) flatTextArea.getDocument()).setDocumentFilter(new LengthLimitFilter(200));
        flatTextArea.getDocument().addDocumentListener((SimpleDocumentListener) ev -> {
            notification.setMessage(flatTextArea.getText());
        });
        flatTextArea.getTextArea().addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                flatTextArea.getTextArea().selectAll();
            }

            @Override
            public void focusLost(FocusEvent e) {
                onChangeListener.run();
            }
        });
        this.settings.add(flatTextArea);
    }
}
