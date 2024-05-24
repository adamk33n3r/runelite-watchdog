/*
 * Copyright (c) 2018, Jasper <Jasper0781@gmail.com>
 * Copyright (c) 2020, melky <https://github.com/melkypie>
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

import net.runelite.api.Client;
import net.runelite.api.MenuEntry;
import net.runelite.api.widgets.Widget;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;

import javax.inject.Inject;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;

class ScreenMarkerWidgetHighlightOverlay extends Overlay
{
    private final ScreenMarkerUtil util;
    private final Client client;

    @Inject
    private ScreenMarkerWidgetHighlightOverlay(final ScreenMarkerUtil util, final Client client)
    {
        this.util = util;
        this.client = client;
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ABOVE_WIDGETS);
        setPriority(PRIORITY_HIGH);
        setMovable(true);
    }

    @Override
    public Dimension render(Graphics2D graphics)
    {
        if (!util.isCreatingScreenMarker() || util.isDrawingScreenMarker())
        {
            return null;
        }

        final MenuEntry[] menuEntries = client.getMenuEntries();
        if (client.isMenuOpen() || menuEntries.length == 0)
        {
            util.setSelectedWidgetBounds(null);
            return null;
        }

        final MenuEntry menuEntry = menuEntries[menuEntries.length - 1];
        final int childIdx = menuEntry.getParam0();
        final int widgetId = menuEntry.getParam1();

        final Widget widget = client.getWidget(widgetId);
        if (widget == null)
        {
            util.setSelectedWidgetBounds(null);
            return null;
        }

        Rectangle bounds = null;
        if (childIdx > -1)
        {
            final Widget child = widget.getChild(childIdx);
            if (child != null)
            {
                bounds = child.getBounds();
            }
        }
        else
        {
            bounds = widget.getBounds();
        }

        if (bounds == null)
        {
            util.setSelectedWidgetBounds(null);
            return null;
        }

        drawHighlight(graphics, bounds);
        util.setSelectedWidgetBounds(bounds);

        return null;
    }

    private static void drawHighlight(Graphics2D graphics, Rectangle bounds)
    {
        graphics.setColor(Color.GREEN);
        graphics.draw(bounds);
    }
}
