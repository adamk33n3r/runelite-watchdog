package com.adamk33n3r.runelite.watchdog.ui.notifications.panels;

import com.adamk33n3r.runelite.watchdog.notifications.Notification;
import com.adamk33n3r.runelite.watchdog.ui.StretchedStackedLayout;

import net.runelite.client.ui.ColorScheme;

import lombok.Getter;

import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

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
        this.buildContent();
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
}
