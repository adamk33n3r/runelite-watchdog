package com.adamk33n3r.runelite.watchdog.ui.notifications.panels;

import com.adamk33n3r.runelite.watchdog.notifications.Notification;
import com.adamk33n3r.runelite.watchdog.ui.FlatTextArea;
import com.adamk33n3r.runelite.watchdog.ui.StretchedStackedLayout;

import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.FontManager;

import lombok.Getter;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.Color;
import java.awt.Component;

import javax.annotation.Nullable;

/**
 * Abstract base class for notification type-specific content panels.
 * <p>
 * Subclasses must set all their own fields and then call {@link #init()} as the
 * final statement in their constructor (after calling {@code super}).
 */
public abstract class NotificationContentPanel<T extends Notification> extends JPanel {
    private static final String OVERRIDDEN_HINT = "Overridden by the Message input";
    private static final String OVERRIDDEN_TOOLTIP = "A node graph input is supplying this message. Disconnect it to use the text below.";

    @Getter
    protected T notification;
    protected Runnable onChange;
    private Runnable onRebuild;

    @Nullable
    private FlatTextArea messageField;
    private Color messageFieldForeground;
    private String messageFieldTooltip;
    private JLabel messageOverrideHint;
    private boolean messageFieldEnabled = true;

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
        this.applyMessageFieldState();
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
        this.applyMessageFieldState();
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

    protected FlatTextArea setMessageField(FlatTextArea messageField) {
        this.messageField = messageField;
        this.messageFieldForeground = messageField.getTextArea().getForeground();
        this.messageFieldTooltip = messageField.getTextArea().getToolTipText();
        return messageField;
    }

    public void setMessageFieldEnabled(boolean enabled) {
        if (this.messageFieldEnabled == enabled) {
            return;
        }

        this.messageFieldEnabled = enabled;
        this.applyMessageFieldState();
        this.revalidate();
        this.repaint();
        if (this.onRebuild != null) {
            this.onRebuild.run();
        }
    }

    private void applyMessageFieldState() {
        if (this.messageField == null) {
            return;
        }

        this.applyFieldAppearance();
        if (this.messageFieldEnabled) {
            this.removeOverrideHint();
        } else {
            this.addOverrideHint();
        }
    }

    private void applyFieldAppearance() {
        this.messageField.setEditable(this.messageFieldEnabled);
        this.messageField.getTextArea().setForeground(
            this.messageFieldEnabled ? this.messageFieldForeground : ColorScheme.MEDIUM_GRAY_COLOR);
        this.messageField.setToolTipText(
            this.messageFieldEnabled ? this.messageFieldTooltip : OVERRIDDEN_TOOLTIP);
    }

    private void removeOverrideHint() {
        if (this.messageOverrideHint != null && this.messageOverrideHint.getParent() == this) {
            this.remove(this.messageOverrideHint);
        }
    }

    private void addOverrideHint() {
        if (this.messageOverrideHint == null) {
            this.messageOverrideHint = new JLabel(OVERRIDDEN_HINT);
            this.messageOverrideHint.setFont(FontManager.getRunescapeSmallFont());
            this.messageOverrideHint.setForeground(ColorScheme.BRAND_ORANGE);
            this.messageOverrideHint.setToolTipText(OVERRIDDEN_TOOLTIP);
        }

        if (this.messageOverrideHint.getParent() != this) {
            this.add(this.messageOverrideHint, this.messageFieldRowIndex());
        }
    }

    private int messageFieldRowIndex() {
        Component row = this.messageField;
        while (row != null && row.getParent() != this) {
            row = row.getParent();
        }
        return row == null ? -1 : this.getComponentZOrder(row);
    }
}
