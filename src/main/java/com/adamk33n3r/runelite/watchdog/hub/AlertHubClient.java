package com.adamk33n3r.runelite.watchdog.hub;

import com.google.gson.reflect.TypeToken;
import lombok.extern.slf4j.Slf4j;
import net.runelite.http.api.RuneLiteAPI;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import javax.imageio.ImageIO;
import javax.inject.Inject;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Slf4j
public class AlertHubClient {
    private final OkHttpClient cachingClient;

    @Inject
    public AlertHubClient(OkHttpClient cachingClient) {
        this.cachingClient = cachingClient;
    }

    public List<AlertManifest> downloadManifest() throws IOException {
        HttpUrl manifest = Objects.requireNonNull(HttpUrl.parse("https://raw.githubusercontent.com/melkypie/resource-packs"))
            .newBuilder()
            .addPathSegment("github-actions")
            .addPathSegment("manifest.js")
            .build();

        return Arrays.asList(new AlertManifest(
            "testAlert",
            "284hfu43hhfiu24rf",
            "Test Alert",
            "This is a test alert on the hub",
            "4",
            "adamk33n3r",
            AlertHubCategory.COMBAT,
            Arrays.asList("afk", "combat"),
            new URL("https://github.com/adamk33n3r/runelite-watchdog"),
            "[]",
            false
        ), new AlertManifest(
            "testAlert2",
            "284hfu43hhfiu24rf",
            "Test Alert 2",
            "This is a test alert on the hub",
            "4",
            "adamk33n3r",
            AlertHubCategory.SKILLING,
            Arrays.asList("mining", "tts"),
            new URL("https://github.com/adamk33n3r/runelite-watchdog"),
            "[]",
            false
        ));
//        try (Response res  = cachingClient.newCall(new Request.Builder().url(manifest).build()).execute()) {
//            if (res.code() != 200) {
//                throw new IOException("Non-OK response code: " + res.code());
//            }
//
//            String data = Objects.requireNonNull(res.body()).string();
//
//            return RuneLiteAPI.GSON.fromJson(data, new TypeToken<List<AlertManifest>>() {}.getType());
//        }
    }

    public BufferedImage downloadIcon(AlertManifest alertManifest) throws IOException {
        // TODO: Use defaults
        if (!alertManifest.isHasIcon()) {
            return null;
        }

        HttpUrl url = Objects.requireNonNull(HttpUrl.parse("https://raw.githubusercontent.com/melkypie/resource-packs"))
            .newBuilder()
            .addPathSegment(alertManifest.getCommit())
            .addPathSegment("icon.png")
            .build();

        try (Response res  = cachingClient.newCall(new Request.Builder().url(url).build()).execute()) {
            if (res.code() != 200) {
                throw new IOException("Non-OK response code: " + res.code());
            }

            byte[] bytes = Objects.requireNonNull(res.body()).bytes();
            synchronized (ImageIO.class) {
                return ImageIO.read(new ByteArrayInputStream(bytes));
            }
        }
    }
}
