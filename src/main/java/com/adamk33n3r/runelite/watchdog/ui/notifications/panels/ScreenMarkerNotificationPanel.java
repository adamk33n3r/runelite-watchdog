package com.adamk33n3r.runelite.watchdog.ui.notifications.panels;

import com.adamk33n3r.runelite.watchdog.LengthLimitFilter;
import com.adamk33n3r.runelite.watchdog.SimpleDocumentListener;
import com.adamk33n3r.runelite.watchdog.WatchdogPlugin;
import com.adamk33n3r.runelite.watchdog.notifications.ScreenMarker;
import com.adamk33n3r.runelite.watchdog.ui.FlatTextArea;
import com.adamk33n3r.runelite.watchdog.ui.Icons;
import com.adamk33n3r.runelite.watchdog.ui.notifications.screenmarker.ScreenMarkerOverlay;
import com.adamk33n3r.runelite.watchdog.ui.notifications.screenmarker.ScreenMarkerUtil;
import com.adamk33n3r.runelite.watchdog.ui.panels.NotificationsPanel;
import com.adamk33n3r.runelite.watchdog.ui.panels.PanelUtils;

import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.components.colorpicker.ColorPickerManager;

import javax.swing.*;
import javax.swing.text.AbstractDocument;
import java.awt.GridLayout;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

public class ScreenMarkerNotificationPanel extends NotificationPanel {
    private ScreenMarkerOverlay screenMarkerOverlay;
    private JButton setMarkerButton;
    private JPanel displayTime;
    private JPanel stickyId;

    public ScreenMarkerNotificationPanel(ScreenMarker notification, NotificationsPanel parentPanel, ColorPickerManager colorPickerManager, Runnable onChangeListener, PanelUtils.OnRemove onRemove) {
        super(notification, parentPanel, onChangeListener, onRemove);
        // Rebind onRemove to hook into it so that we can delete the screen marker when this notification is deleted
        // Perhaps this should be refactored
        this.onRemove = (ele) -> {
            ScreenMarkerUtil screenMarkerUtil = WatchdogPlugin.getInstance().getScreenMarkerUtil();
            if (this.screenMarkerOverlay != null) {
                screenMarkerUtil.deleteMarker(this.screenMarkerOverlay);
            }
            screenMarkerUtil.finishCreation(true);
            onRemove.elementRemoved(ele);
        };

        net.runelite.client.plugins.screenmarkers.ScreenMarker screenMarker = notification.getScreenMarker();

        FlatTextArea markerLabel = new FlatTextArea("Optional marker label...", true);
        markerLabel.setText(screenMarker.getName());
        ((AbstractDocument) markerLabel.getDocument()).setDocumentFilter(new LengthLimitFilter(200));
        markerLabel.getDocument().addDocumentListener((SimpleDocumentListener) ev -> {
            screenMarker.setName(markerLabel.getText());
            screenMarker.setLabelled(!markerLabel.getText().isEmpty());
        });
        markerLabel.getTextArea().addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                markerLabel.getTextArea().selectAll();
            }

            @Override
            public void focusLost(FocusEvent e) {
                onChangeListener.run();
            }
        });
        this.settings.add(markerLabel);

        this.settings.add(PanelUtils.createColorPicker(
            "Border Color",
            "The color of the border",
            "Border Color",
            this,
            notification.getScreenMarker().getColor(),
            colorPickerManager,
            true,
            val -> {
               screenMarker.setColor(val);
               onChangeListener.run();
            }));

        this.settings.add(PanelUtils.createColorPicker(
            "Fill Color",
            "The color of the interior",
            "Fill Color",
            this,
            notification.getScreenMarker().getFill(),
            colorPickerManager,
            true,
            val -> {
                screenMarker.setFill(val);
                onChangeListener.run();
            }));

        this.setMarkerButton = PanelUtils.createButton("Set Marker", "Set Marker", (btn, modifiers) -> {
            ScreenMarkerUtil screenMarkerUtil = WatchdogPlugin.getInstance().getScreenMarkerUtil();
            // Done
            if (screenMarkerUtil.isCreatingScreenMarker()) {
                this.screenMarkerOverlay = screenMarkerUtil.finishCreation(false);
                this.setMarkerButton.setText("Set Marker");
                this.setMarkerButton.setToolTipText("Set Marker");
                // Start
            } else {
                if (this.screenMarkerOverlay != null) {
                    screenMarkerUtil.deleteMarker(this.screenMarkerOverlay);
                }
                screenMarkerUtil.setMouseListenerEnabled(true);
                screenMarkerUtil.setCreatingScreenMarker(true);
                screenMarkerUtil.setCurrentMarker(notification);
                this.setMarkerButton.setText("Finish");
                this.setMarkerButton.setToolTipText("Finish");
            }
        });
        this.settings.add(this.setMarkerButton);

        JSpinner thickness = PanelUtils.createSpinner(
            screenMarker.getBorderThickness(),
            0,
            Integer.MAX_VALUE,
            1,
            val -> {
                screenMarker.setBorderThickness(val);
                onChangeListener.run();
            }
        );

        JCheckBox sticky = PanelUtils.createCheckbox("Sticky", "Set the notification to not expire", notification.isSticky(), val -> {
            notification.setSticky(val);
            if (val) {
                this.settings.remove(this.displayTime);
                this.settings.add(this.stickyId);
            } else {
                this.settings.remove(this.stickyId);
                this.settings.add(this.displayTime);
            }
            this.revalidate();
            onChangeListener.run();
        });

        JSpinner displayTime = PanelUtils.createSpinner(notification.getDisplayTime(), 0, 99, 1, val -> {
            notification.setDisplayTime(val);
            onChangeListener.run();
        });
        JPanel borderThickness = PanelUtils.createIconComponent(Icons.BORDER_OUTSIDE, "Border thickness", thickness);
        this.displayTime = PanelUtils.createIconComponent(Icons.CLOCK, "Time to display the marker in seconds.", displayTime);
        JPanel sub = new JPanel(new GridLayout(1, 2, 3, 3));
        sub.add(borderThickness);
        sub.add(sticky);
        sub.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        this.settings.add(sub);

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
            this.settings.add(this.stickyId);
        } else {
            this.settings.add(this.displayTime);
        }
    }
}
