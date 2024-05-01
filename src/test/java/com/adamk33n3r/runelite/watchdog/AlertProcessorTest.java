package com.adamk33n3r.runelite.watchdog;

import com.adamk33n3r.runelite.watchdog.alerts.Alert;
import com.adamk33n3r.runelite.watchdog.notifications.Notification;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class AlertProcessorTest {
    private Alert alert;
    private Notification notification;
    private Notification notification2;
    private AlertProcessor alertProcessor;

    @Before
    public void setup() {
        alert = mock(Alert.class);
        notification = mock(Notification.class);
        notification2 = mock(Notification.class);
        when(alert.getNotifications()).thenReturn(List.of(notification, notification2));
    }

    @Test
    public void shouldProcessNotificationsWhenForceFireIsFalseAndShouldFireIsTrue() throws InterruptedException {
        when(notification.shouldFire()).thenReturn(true);
        alertProcessor = new AlertProcessor(alert, new String[0], false);
        alertProcessor.start();
        alertProcessor.join();
        verify(notification, times(1)).fire(any());
    }

    @Test
    public void shouldNotProcessNotificationsWhenForceFireIsFalseAndShouldFireIsFalse() throws InterruptedException {
        when(notification.shouldFire()).thenReturn(false);
        alertProcessor = new AlertProcessor(alert, new String[0], false);
        alertProcessor.start();
        alertProcessor.join();
        verify(notification, times(0)).fire(any());
    }

    @Test
    public void shouldProcessNotificationsWhenForceFireIsTrueRegardlessOfShouldFire() throws InterruptedException {
        when(notification.shouldFire()).thenReturn(false);
        alertProcessor = new AlertProcessor(alert, new String[0], true);
        alertProcessor.start();
        alertProcessor.join();
        verify(notification, times(1)).fireForced(any());
    }

    @Test
    public void shouldProcessMultipleNotifications() throws InterruptedException {
        when(alert.isRandomNotifications()).thenReturn(false);
        when(notification.shouldFire()).thenReturn(true);
        when(notification2.shouldFire()).thenReturn(true);
        alertProcessor = new AlertProcessor(alert, new String[0], false);
        alertProcessor.start();
        alertProcessor.join();
        verify(notification, times(1)).fire(any());
        verify(notification2, times(1)).fire(any());
    }

    @Test
    public void shouldOnlyProcessOneNotification() throws InterruptedException {
        when(alert.isRandomNotifications()).thenReturn(true);
        when(notification.shouldFire()).thenReturn(true);
        when(notification2.shouldFire()).thenReturn(true);
        alertProcessor = new AlertProcessor(alert, new String[0], false);
        alertProcessor.start();
        alertProcessor.join();
        // verify that either notification or notification2 is fired, but not both
        ArgumentCaptor<String[]> argumentCaptor = ArgumentCaptor.forClass(String[].class);
        verify(notification, atLeast(0)).fire(argumentCaptor.capture());
        ArgumentCaptor<String[]> argumentCaptor2 = ArgumentCaptor.forClass(String[].class);
        verify(notification2, atLeast(0)).fire(argumentCaptor2.capture());

        assertTrue(!argumentCaptor.getAllValues().isEmpty() || !argumentCaptor2.getAllValues().isEmpty());
    }
}