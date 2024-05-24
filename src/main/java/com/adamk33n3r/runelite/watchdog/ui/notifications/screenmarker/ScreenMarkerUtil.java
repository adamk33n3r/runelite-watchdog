package com.adamk33n3r.runelite.watchdog.ui.notifications.screenmarker;

import com.adamk33n3r.runelite.watchdog.notifications.ScreenMarker;

import net.runelite.client.config.ConfigManager;
import net.runelite.client.input.MouseManager;
import net.runelite.client.ui.components.colorpicker.ColorPickerManager;
import net.runelite.client.ui.overlay.OverlayManager;

import com.google.gson.Gson;
import lombok.Getter;
import lombok.Setter;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

@Singleton
public class ScreenMarkerUtil {
    private static final Dimension DEFAULT_SIZE = new Dimension(2, 2);

    @Getter
    private final List<ScreenMarkerOverlay> screenMarkers = new ArrayList<>();

    @Inject
    private ConfigManager configManager;

    @Inject
    private MouseManager mouseManager;

    @Inject
    private OverlayManager overlayManager;

    @Getter
    @Inject
    private ScreenMarkerCreationOverlay overlay;

    @Inject
    private Gson gson;

    @Getter
    @Inject
    private ColorPickerManager colorPickerManager;

    @Inject
    private ScreenMarkerWidgetHighlightOverlay widgetHighlight;

    private final ScreenMarkerMouseListener mouseListener;

    @Getter @Setter
    private ScreenMarker currentMarker;

    @Getter @Setter
    private boolean creatingScreenMarker = false;
    @Getter @Setter
    private boolean drawingScreenMarker = false;

    @Getter @Setter
    private Rectangle selectedWidgetBounds = null;
    private Point startLocation = null;

    public ScreenMarkerUtil() {
        this.mouseListener = new ScreenMarkerMouseListener(this);
    }

    public void startUp() {
        this.overlayManager.add(this.overlay);
        this.overlayManager.add(this.widgetHighlight);
    }

    public void setMouseListenerEnabled(boolean enabled) {
        if (enabled) {
            this.mouseManager.registerMouseListener(this.mouseListener);
        } else {
            this.mouseManager.unregisterMouseListener(this.mouseListener);
        }
    }

    public void startCreation(Point location)
    {
        startCreation(location, DEFAULT_SIZE);

        // Stop the highlighting so we don't get rectangles around widgets while trying to make normal screen markers
        if (selectedWidgetBounds == null)
        {
            drawingScreenMarker = true;
        }
    }

    public void startCreation(Point location, Dimension size)
    {
//        currentMarker = new ScreenMarker(
//            Instant.now().toEpochMilli(),
//            "Marker " + (screenMarkers.size() + 1),
//            3,
//            Color.GREEN,
//            new Color(0, 255, 0, 0),
////            pluginPanel.getSelectedBorderThickness(),
////            pluginPanel.getSelectedColor(),
////            pluginPanel.getSelectedFillColor(),
//            true,
//            false //marker.name.length > 1
//        );

        // Set overlay creator bounds to current position and default size
        startLocation = location;
        overlay.setPreferredLocation(location);
        overlay.setPreferredSize(size);
    }

    public ScreenMarkerOverlay finishCreation(boolean aborted)
    {
        ScreenMarker marker = currentMarker;
        ScreenMarkerOverlay screenMarkerOverlay = null;
        if (!aborted && marker != null)
        {
            screenMarkerOverlay = new ScreenMarkerOverlay(marker);
            screenMarkerOverlay.setPreferredLocation(overlay.getBounds().getLocation());
            screenMarkerOverlay.setPreferredSize(overlay.getBounds().getSize());

            screenMarkers.add(screenMarkerOverlay);
            overlayManager.saveOverlay(screenMarkerOverlay);
            overlayManager.add(screenMarkerOverlay);
//            pluginPanel.rebuild();
        }

        creatingScreenMarker = false;
        drawingScreenMarker = false;
        selectedWidgetBounds = null;
        startLocation = null;
        currentMarker = null;
        setMouseListenerEnabled(false);

//        pluginPanel.setCreation(false);
        return screenMarkerOverlay;
    }

    public void completeSelection()
    {
//        pluginPanel.getCreationPanel().unlockConfirm();
    }

    public void deleteMarker(final ScreenMarkerOverlay marker)
    {
        screenMarkers.remove(marker);
        overlayManager.remove(marker);
        overlayManager.resetOverlay(marker);
//        pluginPanel.rebuild();
    }

    void resizeMarker(Point point)
    {
        drawingScreenMarker = true;
        Rectangle bounds = new Rectangle(startLocation);
        bounds.add(point);
        overlay.setPreferredLocation(bounds.getLocation());
        overlay.setPreferredSize(bounds.getSize());
    }

    public void addScreenMarker(ScreenMarker screenMarker) {
        ScreenMarkerOverlay screenMarkerOverlay = new ScreenMarkerOverlay(screenMarker);
        this.screenMarkers.add(screenMarkerOverlay);
        this.overlayManager.add(screenMarkerOverlay);
    }

    public void removeScreenMarker(ScreenMarker screenMarker) {
        this.screenMarkers.removeIf(overlay -> overlay.getMarker() == screenMarker);
        this.overlayManager.removeIf(overlay -> overlay instanceof ScreenMarkerOverlay && ((ScreenMarkerOverlay) overlay).getMarker() == screenMarker);
    }

    public void removeAllMarkers() {
        this.screenMarkers.clear();
        this.overlayManager.removeIf(ScreenMarkerOverlay.class::isInstance);
    }

    public void shutDown() {
        overlayManager.remove(overlay);
        overlayManager.remove(widgetHighlight);
        overlayManager.removeIf(ScreenMarkerOverlay.class::isInstance);
        screenMarkers.clear();
        this.setMouseListenerEnabled(false);
        this.creatingScreenMarker = false;
        this.drawingScreenMarker = false;

        currentMarker = null;
        selectedWidgetBounds = null;
    }
}
