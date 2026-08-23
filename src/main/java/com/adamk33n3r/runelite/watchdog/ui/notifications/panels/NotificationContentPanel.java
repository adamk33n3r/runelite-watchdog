package com.adamk33n3r.runelite.watchdog.ui.notifications.panels;

import com.adamk33n3r.runelite.watchdog.notifications.Notification;
import com.adamk33n3r.runelite.watchdog.ui.FlatTextArea;
import com.adamk33n3r.runelite.watchdog.ui.StretchedStackedLayout;

import net.runelite.client.ui.ColorScheme;

import lombok.Getter;

import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.Component;

import javax.annotation.Nullable;

/**
 * Abstract base class for notification type-specific content panels.
 * <p>
 * Subclasses must set all their own fields and then call {@link #init()} as the
 * final statement in their constructor (after calling {@code super}).
 */
public abstract class NotificationContentPanel<T extends Notification> extends JPanel {
    @Getter
    protected T notification;
    protected Runnable onChange;
    private Runnable onRebuild;

    @Nullable
    private FlatTextArea messageField;
    private boolean messageFieldHidden;

    protected NotificationContentPanel(T notification, Runnable onChange) {
        this.notification = notification;
        this.onChange = onChange;
    }

    /**
     * Must be called at the end of each subclass constructor, after all fields are set.
     * Sets up layout/border/background and invokes {@link #buildContent()}.
     */
    protected void init() {
        this.setLayout(new StretchedStackedLayout(3));
        this.setBorder(new EmptyBorder(5, 10, 5, 10));
        this.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        this.buildContent();
    }

    /**
     * Subclasses implement this to add their type-specific controls to {@code this}.
     * Use {@code this.notification} and {@code this.onChange}.
     */
    protected abstract void buildContent();

    /**
     * Clears all content and rebuilds in-place. Panels with conditional UI call this
     * when a user action changes which controls are visible. Fires the optional
     * {@link #onRebuild} callback after rebuilding (e.g. so node panels can call {@code pack()}).
     */
    public void rebuild() {
        this.removeAll();
        this.messageField = null;
        this.buildContent();
        this.applyMessageFieldVisibility();
        this.revalidate();
        this.repaint();
        if (this.onRebuild != null) {
            this.onRebuild.run();
        }
    }

    /**
     * Sets a callback invoked after every {@link #rebuild()} call.
     * Node panels set this to {@code this::pack} so the node resizes after content changes.
     */
    public void setOnRebuild(Runnable onRebuild) {
        this.onRebuild = onRebuild;
    }

    /**
     * Registers the field holding this notification's message so {@link #setMessageFieldHidden}
     * can find its row. Returns the field so subclasses can wrap their {@code add} call.
     */
    protected FlatTextArea setMessageField(FlatTextArea messageField) {
        this.messageField = messageField;
        return messageField;
    }

    /**
     * Hides the message row. Node panels set this because the node graph puts the message control on
     * the "Message" connection line instead, right next to the pin that can override it.
     */
    public void setMessageFieldHidden(boolean hidden) {
        if (this.messageFieldHidden == hidden) {
            return;
        }

        this.messageFieldHidden = hidden;
        this.rebuild();
    }

    private void applyMessageFieldVisibility() {
        if (this.messageField == null || !this.messageFieldHidden) {
            return;
        }

        Component row = this.messageField;
        while (row != null && row.getParent() != this) {
            row = row.getParent();
        }

        if (row != null) {
            this.remove(row);
        }
    }
}
