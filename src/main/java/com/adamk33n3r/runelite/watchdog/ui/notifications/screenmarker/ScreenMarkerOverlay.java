/*
 * Copyright (c) 2018, Kamiel, <https://github.com/Kamielvf>
 * Copyright (c) 2018, Adam <Adam@sigterm.info>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.adamk33n3r.runelite.watchdog.ui.notifications.screenmarker;

import com.adamk33n3r.runelite.watchdog.WatchdogConfig;
import com.adamk33n3r.runelite.watchdog.WatchdogPlugin;
import com.adamk33n3r.runelite.watchdog.notifications.ScreenMarker;

import net.runelite.api.Client;
import net.runelite.api.MenuAction;
import net.runelite.client.ui.ClientUI;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;

import lombok.Getter;
import lombok.NonNull;

import javax.inject.Inject;
import java.awt.BasicStroke;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.time.Duration;
import java.time.Instant;

public class ScreenMarkerOverlay extends Overlay {
    @Getter
    private final ScreenMarker marker;
    private final ScreenMarkerRenderable screenMarkerRenderable;
    private final Instant timeStarted;

    @Inject
    private Client client;

    @Inject
    private ClientUI clientUI;

    @Inject
    private WatchdogConfig config;

    private long mouseLastPressedMillis;

    ScreenMarkerOverlay(@NonNull ScreenMarker marker)
    {
        this.marker = marker;
        this.screenMarkerRenderable = new ScreenMarkerRenderable();
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ALWAYS_ON_TOP);
        setPriority(PRIORITY_HIGH);
        setMovable(true);
        setResizable(true);
        setMinimumSize(16);
        setResettable(false);
        this.addMenuEntry(MenuAction.RUNELITE_OVERLAY, "Dismiss", "Watchdog screen marker", (me) -> WatchdogPlugin.getInstance().getScreenMarkerUtil().removeScreenMarker(marker));
        this.timeStarted = Instant.now();
    }

    public boolean isExpired() {
        return this.timeStarted.plus(Duration.ofSeconds(this.marker.getDisplayTime())).isBefore(Instant.now());
    }

    @Override
    public String getName()
    {
        return "watchdog.marker" + marker.getScreenMarker().getId();
    }

    @Override
    public Dimension render(Graphics2D graphics)
    {
        net.runelite.client.plugins.screenmarkers.ScreenMarker marker = this.marker.getScreenMarker();

        if (this.marker.getDisplayTime() > 0 && this.isExpired()) {
            WatchdogPlugin.getInstance().getScreenMarkerUtil().removeScreenMarker(this.marker);
            return null;
        }

        if (!marker.isVisible())
        {
            return null;
        }

        Dimension preferredSize = getPreferredSize();
        if (preferredSize == null)
        {
            // overlay has no preferred size in the renderer configuration!
            return null;
        }

        screenMarkerRenderable.setBorderThickness(marker.getBorderThickness());
        screenMarkerRenderable.setColor(marker.getColor());
        screenMarkerRenderable.setFill(marker.getFill());
        screenMarkerRenderable.setStroke(new BasicStroke(marker.getBorderThickness()));
        screenMarkerRenderable.setSize(preferredSize);
        screenMarkerRenderable.setLabel(marker.isLabelled() ? marker.getName() : "");
        return screenMarkerRenderable.render(graphics);
    }
}
