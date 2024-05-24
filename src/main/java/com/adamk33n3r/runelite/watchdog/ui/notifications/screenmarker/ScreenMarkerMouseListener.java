/*
 * Copyright (c) 2018, Kamiel, <https://github.com/Kamielvf>
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

import net.runelite.client.input.MouseAdapter;

import javax.swing.SwingUtilities;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;

class ScreenMarkerMouseListener extends MouseAdapter
{
    private final ScreenMarkerUtil util;

    ScreenMarkerMouseListener(ScreenMarkerUtil util)
    {
        this.util = util;
    }

    @Override
    public MouseEvent mouseClicked(MouseEvent event)
    {
        if (SwingUtilities.isMiddleMouseButton(event))
        {
            return event;
        }

        event.consume();
        return event;
    }

    @Override
    public MouseEvent mousePressed(MouseEvent event)
    {
        if (SwingUtilities.isMiddleMouseButton(event))
        {
            return event;
        }

        if (SwingUtilities.isLeftMouseButton(event))
        {
            final Rectangle bounds = util.getSelectedWidgetBounds();

            if (bounds != null)
            {
                util.startCreation(bounds.getLocation(), bounds.getSize());
            }
            else
            {
                util.startCreation(event.getPoint());
            }
        }
        else if (util.isCreatingScreenMarker())
        {
            util.finishCreation(true);
        }

        event.consume();
        return event;
    }

    @Override
    public MouseEvent mouseReleased(MouseEvent event)
    {
        if (SwingUtilities.isMiddleMouseButton(event))
        {
            return event;
        }

        if (SwingUtilities.isLeftMouseButton(event) && util.isCreatingScreenMarker())
        {
            /* Set the creation panel as "ready" (because the marker area as been drawn) */
            // Was probably a widget misclick, reset drawing state
            if (util.getOverlay().getPreferredSize().height == 2 && util.getOverlay().getPreferredSize().width == 2) {
                util.setDrawingScreenMarker(false);
            } else {
                util.completeSelection();
            }
        }

        event.consume();
        return event;
    }

    @Override
    public MouseEvent mouseDragged(MouseEvent event)
    {
        if (!util.isCreatingScreenMarker())
        {
            return event;
        }

        if (SwingUtilities.isLeftMouseButton(event))
        {
            util.resizeMarker(event.getPoint());
        }

        return event;
    }
}
