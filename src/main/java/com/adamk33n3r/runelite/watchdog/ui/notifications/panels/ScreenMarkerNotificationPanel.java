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

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.text.AbstractDocument;
import java.awt.GridLayout;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

public class ScreenMarkerNotificationPanel extends NotificationPanel {
    private ScreenMarkerOverlay screenMarkerOverlay;
    private JButton setMarkerButton;
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

        FlatTextArea flatTextArea = new FlatTextArea("Optional marker label...", true);
        flatTextArea.setText(screenMarker.getName());
        ((AbstractDocument) flatTextArea.getDocument()).setDocumentFilter(new LengthLimitFilter(200));
        flatTextArea.getDocument().addDocumentListener((SimpleDocumentListener) ev -> {
            screenMarker.setName(flatTextArea.getText());
            screenMarker.setLabelled(!flatTextArea.getText().isEmpty());
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
        JPanel iconComponent1 = PanelUtils.createIconComponent(Icons.BORDER_OUTSIDE, "Border thickness", thickness);

        JSpinner displayTime = PanelUtils.createSpinner(notification.getDisplayTime(), 0, 99, 1, val -> {
            notification.setDisplayTime(val);
            onChangeListener.run();
        });
        JPanel iconComponent = PanelUtils.createIconComponent(Icons.CLOCK, "Time to display the marker in seconds. If 0, will be sticky and can be dismissed with shift right-click", displayTime);
        JPanel sub = new JPanel(new GridLayout(1, 2, 3, 3));
        sub.add(iconComponent1);
        sub.add(iconComponent);
        sub.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        this.settings.add(sub);

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
    }
}
