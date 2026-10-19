package com.adamk33n3r.runelite.watchdog;

import com.adamk33n3r.runelite.watchdog.notifications.GameMessage;
import com.adamk33n3r.runelite.watchdog.ui.FlatTextArea;
import com.adamk33n3r.runelite.watchdog.ui.notifications.panels.NotificationContentPanel;
import com.adamk33n3r.runelite.watchdog.ui.panels.PanelUtils;

import org.junit.Test;

import javax.swing.JButton;
import javax.swing.JComponent;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

/**
 * Tests the message-field-suffix registry that {@code MessageActionNodePanel} reads to move a
 * notification's suffix control onto the node graph. No live Swing layout is exercised.
 */
public class NotificationContentPanelSuffixTest {
    private static class BarePanel extends NotificationContentPanel<GameMessage> {
        BarePanel() {
            super(new GameMessage(), () -> {});
            this.init();
        }

        @Override
        protected void buildContent() {
            this.add(this.setMessageField(newTextArea()));
        }
    }

    private static class SuffixedPanel extends NotificationContentPanel<GameMessage> {
        private JButton registeredSuffix;

        SuffixedPanel() {
            super(new GameMessage(), () -> {});
            this.init();
        }

        @Override
        protected void buildContent() {
            FlatTextArea messageField = this.setMessageField(newTextArea());
            this.registeredSuffix = this.setMessageFieldSuffix(new JButton("reset"));
            this.add(PanelUtils.createInputGroupWithSuffix(messageField, this.registeredSuffix));
        }
    }

    private static FlatTextArea newTextArea() {
        return PanelUtils.createTextField("placeholder", "tooltip", "", (val) -> {});
    }

    @Test
    public void suffix_isNullWhenPanelRegistersNone() {
        assertNull(new BarePanel().getMessageFieldSuffix());
    }

    @Test
    public void suffix_isTheControlThePanelRegistered() {
        SuffixedPanel panel = new SuffixedPanel();

        assertSame(panel.registeredSuffix, panel.getMessageFieldSuffix());
    }

    @Test
    public void suffix_survivesHidingTheMessageField() {
        SuffixedPanel panel = new SuffixedPanel();

        panel.setMessageFieldHidden(true);

        assertNotNull(panel.getMessageFieldSuffix());
        assertSame(panel.registeredSuffix, panel.getMessageFieldSuffix());
    }

    @Test
    public void suffix_isReplacedOnRebuild() {
        SuffixedPanel panel = new SuffixedPanel();
        JComponent original = panel.getMessageFieldSuffix();

        panel.rebuild();

        assertNotSame(original, panel.getMessageFieldSuffix());
        assertSame(panel.registeredSuffix, panel.getMessageFieldSuffix());
    }
}
