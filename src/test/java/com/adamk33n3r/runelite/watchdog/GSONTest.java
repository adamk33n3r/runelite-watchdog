package com.adamk33n3r.runelite.watchdog;

import com.adamk33n3r.runelite.watchdog.alerts.*;
import com.adamk33n3r.runelite.watchdog.notifications.*;
import com.adamk33n3r.runelite.watchdog.notifications.tts.TTSSource;

import com.google.gson.Gson;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class GSONTest {
    @Test
    public void json_missing_property_with_initializer_test() throws Exception {
        final RuntimeTypeAdapterFactory<Alert> alertTypeFactory = RuntimeTypeAdapterFactory.of(Alert.class)
            .ignoreSubtype("IdleAlert")
            .ignoreSubtype("ResourceAlert")
            .recognizeSubtypes()
            .registerSubtype(ChatAlert.class)
            .registerSubtype(PlayerChatAlert.class)
            .registerSubtype(NotificationFiredAlert.class)
            .registerSubtype(StatDrainAlert.class)
            .registerSubtype(StatChangedAlert.class)
            .registerSubtype(XPDropAlert.class)
            .registerSubtype(SoundFiredAlert.class)
            .registerSubtype(SpawnedAlert.class)
            .registerSubtype(InventoryAlert.class)
            .registerSubtype(AlertGroup.class)
            .registerSubtype(LocationAlert.class);
        // Add new notification types here
        final RuntimeTypeAdapterFactory<Notification> notificationTypeFactory = RuntimeTypeAdapterFactory.of(Notification.class)
            .registerSubtype(TrayNotification.class)
            .registerSubtype(TextToSpeech.class)
            .registerSubtype(Sound.class)
            .registerSubtype(SoundEffect.class)
            .registerSubtype(ScreenFlash.class)
            .registerSubtype(GameMessage.class)
            .registerSubtype(Overhead.class)
            .registerSubtype(Overlay.class)
            .registerSubtype(RequestFocus.class)
            .registerSubtype(NotificationEvent.class)
            .registerSubtype(ScreenMarker.class)
            .registerSubtype(DismissOverlay.class);
        Gson gson = new Gson().newBuilder()
//            .serializeNulls()
            .registerTypeAdapterFactory(alertTypeFactory)
            .registerTypeAdapterFactory(notificationTypeFactory)
            .create();
        TextToSpeech tts = gson.fromJson("{\"type\":\"TextToSpeech\", \"message\":\"this is a test\",\"gain\":10,\"rate\":1,\"voice\":\"GEORGE\"}", TextToSpeech.class);

        assertEquals(TTSSource.LEGACY, tts.getSource());
        assertEquals(10, tts.getGain());
    }
}
