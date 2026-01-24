package com.adamk33n3r.runelite.watchdog;

import com.adamk33n3r.runelite.watchdog.alerts.ChatAlert;
import com.adamk33n3r.runelite.watchdog.notifications.Dink;
import net.runelite.api.ChatMessageType;
import net.runelite.api.events.ChatMessage;
import net.runelite.client.events.PluginMessage;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Map;
import java.util.function.BooleanSupplier;
import java.util.stream.Stream;

@RunWith(MockitoJUnitRunner.class)
public class DinkNotificationTest extends AlertTestBase {

    @InjectMocks
    EventHandler eventHandler;

    @Test
    public void testDinkNotification() {
        String alertName = "Test Alert";
        ChatAlert chatAlert = new ChatAlert(alertName);
        chatAlert.setGameMessageType(GameMessageType.GAME_MESSAGE);
        chatAlert.setPattern("*");
        Dink dink = new Dink();
        chatAlert.addNotification(dink);
        String dinkMessage = "Test Message";
        dink.setMessage(dinkMessage);
        dink.setIncludeScreenshot(true);

        watchdogPlugin.getInjector().injectMembers(chatAlert);
        watchdogPlugin.getInjector().injectMembers(dink);

        Mockito.when(alertManager.getAllAlerts()).thenAnswer(invocation -> Stream.of(chatAlert));
        Mockito.when(alertManager.getAllEnabledAlertsOfType(ChatAlert.class)).thenAnswer(invocation -> Stream.of(chatAlert));

        ChatMessage testMessage = Mockito.mock(ChatMessage.class);
        Mockito.when(testMessage.getName()).thenReturn("Test");
        Mockito.when(testMessage.getMessage()).thenReturn("Test Message");
        Mockito.when(testMessage.getType()).thenReturn(ChatMessageType.GAMEMESSAGE);
        eventHandler.onChatMessage(testMessage);
        Mockito.verify(eventBus, Mockito.timeout(100)).post(new PluginMessage("dink", "notify", Map.of(
            "text", dinkMessage,
            "title", alertName,
            "sourcePlugin", watchdogPlugin.getName(),
            "imageRequested", true
        )));
    }
}
