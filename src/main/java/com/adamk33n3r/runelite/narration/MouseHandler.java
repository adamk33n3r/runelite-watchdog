package com.adamk33n3r.runelite.narration;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.input.MouseListener;

import javax.inject.Inject;
import java.awt.*;
import java.awt.event.MouseEvent;

@Slf4j
public class MouseHandler implements MouseListener {
    @Inject
    private Client client;
    @Inject
    private ClientThread clientThread;
    @Inject
    private NarrationPlugin plugin;

    @Override
    public MouseEvent mouseClicked(MouseEvent mouseEvent) {
        return mouseEvent;
    }

    @Override
    public MouseEvent mousePressed(MouseEvent mouseEvent) {
        return mouseEvent;
    }

    @Override
    public MouseEvent mouseReleased(MouseEvent mouseEvent) {
        return mouseEvent;
    }

    @Override
    public MouseEvent mouseEntered(MouseEvent mouseEvent) {
        return mouseEvent;
    }

    @Override
    public MouseEvent mouseExited(MouseEvent mouseEvent) {
        return mouseEvent;
    }

    @Override
    public MouseEvent mouseDragged(MouseEvent mouseEvent) {
        this.updateMousePoint();
        return mouseEvent;
    }

    @Override
    public MouseEvent mouseMoved(MouseEvent mouseEvent) {
        this.updateMousePoint();
        return mouseEvent;
    }

    private void updateMousePoint() {
        if (!this.client.isMenuOpen()) {
            // Invoke on client thread because this was the previous frames position
            this.clientThread.invokeLater(() -> {
                this.plugin.setMenuOpenPoint(this.client.getMouseCanvasPosition());
            });
        }
    }
}
