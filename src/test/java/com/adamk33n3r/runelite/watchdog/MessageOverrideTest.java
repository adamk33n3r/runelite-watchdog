package com.adamk33n3r.runelite.watchdog;

import com.adamk33n3r.runelite.watchdog.notifications.TrayNotification;
import com.google.inject.testing.fieldbinder.Bind;

import net.runelite.client.Notifier;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.inject.Inject;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class MessageOverrideTest extends TestBase {
    @Mock
    @Bind
    Notifier notifier;

    @Inject
    TrayNotification trayNotification;

    private String capturedMessage() {
        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        verify(this.notifier).notify(any(net.runelite.client.config.Notification.class), messageCaptor.capture());
        return messageCaptor.getValue();
    }

    @Test
    public void fireWithOverride_usesOverrideInsteadOfConfiguredMessage() {
        this.trayNotification.setMessage("configured message");

        this.trayNotification.fireForcedWithMessage(new String[0], "dynamic message");

        assertEquals("dynamic message", this.capturedMessage());
    }

    @Test
    public void fireWithOverride_leavesConfiguredMessageUnmodified() {
        this.trayNotification.setMessage("configured message");

        this.trayNotification.fireForcedWithMessage(new String[0], "dynamic message");

        assertEquals("configured message", this.trayNotification.getMessage());
    }

    @Test
    public void fireWithOverride_substitutesCaptureGroups() {
        this.trayNotification.setMessage("configured $1");

        this.trayNotification.fireForcedWithMessage(new String[]{"world"}, "dynamic $1");

        assertEquals("dynamic world", this.capturedMessage());
    }
}
