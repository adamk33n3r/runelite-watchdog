package com.adamk33n3r.runelite.watchdog.ui.notifications.panels;

import com.adamk33n3r.runelite.watchdog.LengthLimitFilter;
import com.adamk33n3r.runelite.watchdog.SimpleDocumentListener;
import com.adamk33n3r.runelite.watchdog.WatchdogPlugin;
import com.adamk33n3r.runelite.watchdog.notifications.objectmarkers.ObjectMarker;
import com.adamk33n3r.runelite.watchdog.notifications.objectmarkers.ObjectMarkerManager;
import com.adamk33n3r.runelite.watchdog.ui.FlatTextArea;
import com.adamk33n3r.runelite.watchdog.ui.Icons;
import com.adamk33n3r.runelite.watchdog.ui.panels.NotificationsPanel;
import com.adamk33n3r.runelite.watchdog.ui.panels.PanelUtils;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.components.colorpicker.ColorPickerManager;

import javax.swing.*;
import javax.swing.text.AbstractDocument;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

public class ObjectMarkerNotificationPanel extends NotificationPanel {
    private JButton setMarkerButton;
    private JPanel displayTime;
    private JPanel stickyId;

    public ObjectMarkerNotificationPanel(ObjectMarker notification, NotificationsPanel parentPanel, ColorPickerManager colorPickerManager, Runnable onChangeListener, PanelUtils.OnRemove onRemove) {
        super(notification, parentPanel, onChangeListener, onRemove);

        this.setMarkerButton = PanelUtils.createButton("Set Marker", "Set Marker", (btn, modifiers) -> {
            ObjectMarkerManager screenMarkerUtil = WatchdogPlugin.getInstance().getObjectMarkerManager();
            // Done
            if (screenMarkerUtil.isInObjectMarkerMode()) {
                this.setMarkerButton.setText("Set Marker");
                this.setMarkerButton.setToolTipText("Set Marker");
                screenMarkerUtil.turnOffObjectMarkerMode();
            // Start
            } else {
                this.setMarkerButton.setText("Finish");
                this.setMarkerButton.setToolTipText("Finish");
                screenMarkerUtil.turnOnObjectMarkerMode(notification);
            }
        });
        this.settings.add(this.setMarkerButton);

        this.settings.add(PanelUtils.createColorPicker(
            "Border Color",
            "The color of the border",
            "Border Color",
            this,
            notification.getBorderColor(),
            colorPickerManager,
            true,
            val -> {
                notification.setBorderColor(val);
                onChangeListener.run();
            }));

        this.settings.add(PanelUtils.createColorPicker(
            "Fill Color",
            "The color of the interior",
            "Fill Color",
            this,
            notification.getFillColor(),
            colorPickerManager,
            true,
            val -> {
                notification.setFillColor(val);
                onChangeListener.run();
            }));

        JPanel checkboxes = new JPanel(new GridLayout(2, 2, 5, 5));
        checkboxes.add(PanelUtils.createCheckbox("Hull", "Render Hull", notification.isHull(), val -> {
            notification.setHull(val);
            onChangeListener.run();
        }));
        checkboxes.add(PanelUtils.createCheckbox("Outline", "Render Outline", notification.isOutline(), val -> {
            notification.setOutline(val);
            onChangeListener.run();
        }));
        checkboxes.add(PanelUtils.createCheckbox("Clickbox", "Render Clickbox", notification.isClickbox(), val -> {
            notification.setClickbox(val);
            onChangeListener.run();
        }));
        checkboxes.add(PanelUtils.createCheckbox("Tile", "Render Tile", notification.isTile(), val -> {
            notification.setTile(val);
            onChangeListener.run();
        }));
        checkboxes.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        this.settings.add(checkboxes);

        JPanel feather = PanelUtils.createIconComponent(Icons.FEATHER, "Specify between 0-4 how much of the model outline should be faded.",
            PanelUtils.createSpinner(notification.getOutlineFeather(), 0, 4, 1, val -> {
                notification.setOutlineFeather(val);
                onChangeListener.run();
            }));
        this.settings.add(feather);

        JSpinner thickness = PanelUtils.createSpinnerDouble(
            notification.getBorderWidth(),
            0,
            Integer.MAX_VALUE,
            0.1d,
            val -> {
                notification.setBorderWidth(val);
                onChangeListener.run();
            }
        );

        JPanel borderThickness = PanelUtils.createIconComponent(Icons.BORDER_OUTSIDE, "Width of the marked object border.", thickness);
        JPanel sub = new JPanel(new GridLayout(1, 2, 3, 3));
        sub.add(borderThickness);
        sub.add(feather);
        sub.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        this.settings.add(sub);

        JPanel stickySub = new JPanel(new GridLayout(1, 2, 3, 3));
        stickySub.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        this.settings.add(stickySub);

        JCheckBox sticky = PanelUtils.createCheckbox("Sticky", "Set the notification to not expire", notification.isSticky(), val -> {
            notification.setSticky(val);
            System.out.println("sticky: " + val);
            if (val) {
                stickySub.remove(this.displayTime);
                stickySub.add(this.stickyId);
            } else {
                stickySub.remove(this.stickyId);
                stickySub.add(this.displayTime);
            }
            stickySub.revalidate();
            stickySub.repaint();
            onChangeListener.run();
        });
        stickySub.add(sticky);

        JSpinner displayTime = PanelUtils.createSpinner(notification.getDisplayTime(), 0, 99, 1, val -> {
            notification.setDisplayTime(val);
            onChangeListener.run();
        });
        this.displayTime = PanelUtils.createIconComponent(Icons.CLOCK, "Time to display the marker in seconds.", displayTime);

        FlatTextArea stickyId = new FlatTextArea("ID to use with Dismiss Overlay...", true);
        stickyId.setText(notification.getId());
        ((AbstractDocument) stickyId.getDocument()).setDocumentFilter(new LengthLimitFilter(200));
        stickyId.getDocument().addDocumentListener((SimpleDocumentListener) ev -> {
            notification.setId(stickyId.getText());
        });
        stickyId.getTextArea().addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                stickyId.getTextArea().selectAll();
            }

            @Override
            public void focusLost(FocusEvent e) {
                onChangeListener.run();
            }
        });
        this.stickyId = stickyId;

        if (notification.isSticky()) {
            stickySub.add(this.stickyId);
        } else {
            stickySub.add(this.displayTime);
        }
    }
}
