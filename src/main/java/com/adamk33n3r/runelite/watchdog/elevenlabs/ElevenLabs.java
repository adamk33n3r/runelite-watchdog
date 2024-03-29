package com.adamk33n3r.runelite.watchdog.elevenlabs;

import com.adamk33n3r.runelite.watchdog.WatchdogPlugin;

import lombok.extern.slf4j.Slf4j;
import okhttp3.*;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.function.Consumer;

import static net.runelite.http.api.RuneLiteAPI.JSON;

@Slf4j
public class ElevenLabs {
    private static final String BASE_URL = "https://api.elevenlabs.io/";

    public static void getVoice(OkHttpClient client, String voiceID, Consumer<Voice> callback) {
        if (voiceID == null) {
            return;
        }
        Request request = new Request.Builder()
            .url(BASE_URL + "v1/voices/" + voiceID)
            .addHeader("accept", "application/json")
            .addHeader("xi-api-key", WatchdogPlugin.getInstance().getConfig().elevenLabsAPIKey())
            .build();
        makeRequest(client, request, Voice.class, callback);
    }

    public static void getVoices(OkHttpClient client, Consumer<Voices> callback) {
        Request request = new Request.Builder()
            .url(BASE_URL + "v1/voices")
            .addHeader("accept", "application/json")
            .addHeader("xi-api-key", WatchdogPlugin.getInstance().getConfig().elevenLabsAPIKey())
            .build();
        makeRequest(client, request, Voices.class, callback);
    }

    public static void generateTTS(OkHttpClient client, Voice voice, String message, Consumer<File> callback) {
//        String body = WatchdogPlugin.getInstance().getAlertManager().getGson().toJson("");
        Request request = new Request.Builder()
            .url(BASE_URL + "v1/text-to-speech/" + voice.getVoiceId())
            .post(RequestBody.create(JSON, "{\n" +
                "  \"text\": \""+message+"\",\n" +
                "  \"model_id\": \"eleven_monolingual_v1\",\n" +
                "  \"voice_settings\": {\n" +
                "    \"stability\": 0.5,\n" +
                "    \"similarity_boost\": 0.5,\n" +
                "    \"style\": 0.5,\n" +
                "    \"use_speaker_boost\": true\n" +
                "  }\n" +
                "}"))
            .addHeader("xi-api-key", WatchdogPlugin.getInstance().getConfig().elevenLabsAPIKey())
            .build();
        makeRequest(client, request, File.class, callback);
//        download(client, "U4OFqla1WHSBijg88mGB", callback);
    }

    public static void download(OkHttpClient client, String id, Consumer<File> callback) {
        Request request = new Request.Builder()
            .url(BASE_URL + "v1/history/download")
            .post(RequestBody.create(JSON, "{\n" +
                "  \"history_item_ids\": [\n" +
                "    \""+id+"\"\n" +
                "  ]\n" +
                "}"))
            .addHeader("xi-api-key", WatchdogPlugin.getInstance().getConfig().elevenLabsAPIKey())
            .build();
        makeRequest(client, request, File.class, callback);
    }

    private static <T> void makeRequest(OkHttpClient client, Request request, Class<T> rType, Consumer<T> callback) {
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@Nonnull Call call, @Nonnull IOException e) {
                System.err.println("Error with request at: " + request.url());
                e.printStackTrace();
            }

            @Override
            public void onResponse(@Nonnull Call call, @Nonnull Response response) throws IOException {
                try (ResponseBody responseBody = response.body()) {
                    if (!response.isSuccessful()) {
                        log.error("Unexpected error code: " + response);
                        return;
                    }

                    if (responseBody == null) {
                        log.error("Response body is null: " + response);
                        return;
                    }

                    if (Objects.requireNonNull(response.header("content-type")).contains("application/json")) {
                        callback.accept(WatchdogPlugin.getInstance().getAlertManager().getGson().fromJson(responseBody.charStream(), rType));
    //                    Voice voice = voices.getVoices().get(voices.getVoices().size() - 1);
    //                    generateTTS(client, voice, "This is a test of the playback sound system");
    //                    download(client, "U4OFqla1WHSBijg88mGB");
                    } else if (rType.isAssignableFrom(File.class)) {
                        log.debug("got audio stream");
                        File tmpFile = File.createTempFile("watchdog", ".mp3");
    //                    BufferedInputStream bufferedInputStream = new BufferedInputStream(response.body().byteStream());
                        InputStream inputStream = responseBody.byteStream();
                        try (FileOutputStream fileOutputStream = new FileOutputStream(tmpFile)) {
                            byte[] buffer = new byte[8192];
                            int bytesRead;
                            while ((bytesRead = inputStream.read(buffer, 0, buffer.length)) != -1) {
                                fileOutputStream.write(buffer, 0, bytesRead);
                            }
                        }

                        log.debug("tmp file: " + tmpFile);
//                        WatchdogPlugin.getInstance().getSoundPlayer().play(tmpFile, 10);
//                        WatchdogPlugin.getInstance().getSoundPlayer().play(tmpFile, 5);
//                        WatchdogPlugin.getInstance().getSoundPlayer().play(tmpFile, 2);
//                        WatchdogPlugin.getInstance().getSoundPlayer().play(new File("C:\\Users\\adamg\\Music\\airplane_seatbelt.wav"), 10);
//                        WatchdogPlugin.getInstance().getSoundPlayer().play(tmpFile, 10);
                        callback.accept(rType.cast(tmpFile));
                    }
                }
            }
        });
    }
}
