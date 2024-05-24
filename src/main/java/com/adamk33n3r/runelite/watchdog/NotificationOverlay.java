package com.adamk33n3r.runelite.watchdog;

import com.adamk33n3r.runelite.watchdog.notifications.Overlay;

import net.runelite.api.Client;
import net.runelite.client.ui.ClientUI;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.PanelComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;
import net.runelite.client.util.ImageUtil;

import javax.imageio.ImageIO;
import javax.inject.Inject;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

import static net.runelite.api.MenuAction.RUNELITE_OVERLAY;
import static net.runelite.api.MenuAction.RUNELITE_OVERLAY_CONFIG;
import static net.runelite.client.ui.overlay.OverlayManager.OPTION_CONFIGURE;
import static org.apache.commons.lang3.time.DurationFormatUtils.formatDuration;

public class NotificationOverlay extends OverlayPanel {
    @Inject
    private Client client;
    @Inject
    private ClientUI clientUI;
    @Inject
    private WatchdogConfig config;

    private final ConcurrentLinkedQueue<OverlayNotificationData> overlayNotificationQueue = new ConcurrentLinkedQueue<>();
    static final private Dimension DEFAULT_SIZE = new Dimension(250, 60);
    static final String CLEAR = "Clear All";

    private class OverlayNotificationData extends PanelComponent {
        private final Instant timeStarted;
        private final Overlay overlayNotification;
        private final String message;
        private BufferedImage image;

        public OverlayNotificationData(Overlay overlayNotification, String message) {
            this.overlayNotification = overlayNotification;
            if (overlayNotification.getImagePath() != null && !overlayNotification.getImagePath().isEmpty()) {
                try {
                    this.image = ImageUtil.resizeImage(ImageIO.read(new File(overlayNotification.getImagePath())), 128, 128, true);
                } catch(IOException e) {
                    e.printStackTrace();
                }
            }
            this.message = message;
            this.timeStarted = Instant.now();
            this.setWrap(false);
        }

        public boolean isExpired() {
            return !this.overlayNotification.isSticky() && this.timeStarted.plus(Duration.ofSeconds(this.overlayNotification.getTimeToLive())).isBefore(Instant.now());
        }

        @Override
        public Dimension render(Graphics2D graphics) {
            this.setBackgroundColor(this.overlayNotification.getColor());
            this.getChildren().clear();
            this.getChildren().add(WrappedTitleComponent.builder()
                .text(this.message)
                .color(this.overlayNotification.getTextColor())
                .preferredSize(this.getPreferredSize())
                .build());
            if (this.image != null) {
                this.getChildren().add(new CenteredImageComponent(this.image));
            }
            if (config.overlayShowTime()) {
                this.getChildren().add(WrappedTitleComponent.builder()
                    .text(formatDuration(ChronoUnit.MILLIS.between(this.timeStarted, Instant.now()), "m'm' s's' 'ago'"))
                    .color(this.overlayNotification.getTextColor())
                    .preferredSize(this.getPreferredSize())
                    .build());
            }

            return super.render(graphics);
        }
    }

    @Inject
    public NotificationOverlay(WatchdogPlugin plugin) {
        super(plugin);
        this.setPosition(OverlayPosition.TOP_LEFT);
        this.setResizable(true);
        this.setPriority(0);
        this.setClearChildren(true);
        this.setPreferredSize(DEFAULT_SIZE);

        this.panelComponent.setWrap(false);
        this.panelComponent.setBorder(new Rectangle(0, 0, 0, 0));
        this.panelComponent.setGap(new Point(0, 6));
        this.panelComponent.setBackgroundColor(new Color(0, 0, 0, 0));

        this.addMenuEntry(RUNELITE_OVERLAY_CONFIG, OPTION_CONFIGURE, "Watchdog Notification overlay");
        this.addMenuEntry(RUNELITE_OVERLAY, CLEAR, "Watchdog Notification overlay", (me) -> this.clear());
    }

    public void add(Overlay overlayNotification, String message) {
        this.overlayNotificationQueue.add(new OverlayNotificationData(overlayNotification, message));
    }

    public void clear() {
        this.overlayNotificationQueue.clear();
    }

    public void clearById(String id) {
        List<OverlayNotificationData> stickiesToDismiss = this.overlayNotificationQueue.stream()
            .filter(notif -> notif.overlayNotification.isSticky() && notif.overlayNotification.getId().equals(id))
            .collect(Collectors.toList());
        this.overlayNotificationQueue.removeAll(stickiesToDismiss);
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        this.setLayer(this.config.overlayLayer());
        graphics.setFont(this.config.overlayFontType().getFont());

        this.panelComponent.getChildren().add(TitleComponent.builder().text("").build());
        if (this.overlayNotificationQueue.isEmpty()) {
            return super.render(graphics);
        }

        // Keep default width
        if (getPreferredSize() == null) {
            this.setPreferredSize(DEFAULT_SIZE);
        }
        this.overlayNotificationQueue.removeIf(OverlayNotificationData::isExpired);

        while (this.overlayNotificationQueue.size() > 5) {
            this.overlayNotificationQueue.poll();
        }

        this.overlayNotificationQueue.forEach(notif -> this.panelComponent.getChildren().add(notif));

        return super.render(graphics);
    }
}
