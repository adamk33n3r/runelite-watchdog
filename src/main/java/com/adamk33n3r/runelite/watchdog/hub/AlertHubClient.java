package com.adamk33n3r.runelite.watchdog.hub;

import com.adamk33n3r.runelite.watchdog.WatchdogPlugin;

import net.runelite.client.util.ImageUtil;

import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;

import javax.annotation.Nonnull;
import javax.imageio.ImageIO;
import javax.inject.Inject;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Slf4j
public class AlertHubClient {
    private final OkHttpClient cachingClient;
    private static final HttpUrl GITHUB = Objects.requireNonNull(HttpUrl.parse("https://github.com/adamk33n3r/runelite-watchdog"));

    @Inject
    public AlertHubClient(OkHttpClient cachingClient) {
        this.cachingClient = cachingClient.newBuilder()
            .addInterceptor(new CacheInterceptor(15))
            .build();
    }

    public List<AlertDisplayInfo> downloadManifest(boolean forceDownload) throws IOException {
        HttpUrl allAlerts = GITHUB.newBuilder()
            .addPathSegment("archive")
            .addPathSegment("alert-hub.zip")
            .build();

        HashMap<String, AlertDisplayInfo> alerts = new HashMap<>();
        Request.Builder reqBuilder = new Request.Builder().url(allAlerts);
        if (forceDownload) {
            reqBuilder.cacheControl(CacheControl.FORCE_NETWORK);
        }
        try (Response res  = this.cachingClient.newCall(reqBuilder.build()).execute()) {
            if (res.code() != 200) {
                throw new IOException("Non-OK response code: " + res.code());
            }

            BufferedInputStream is = new BufferedInputStream(Objects.requireNonNull(res.body()).byteStream());
            ZipInputStream zipInputStream = new ZipInputStream(is);
            ZipEntry entry;
            while ((entry = zipInputStream.getNextEntry()) != null) {
                String filePath = entry.getName().replaceAll("runelite-watchdog-alert-hub/", "");
                String[] splitPath = filePath.split("/", 2);
                if (splitPath.length == 2) {
                    String alertName = splitPath[0];
                    // Don't need to show the example in the panel
                    if (alertName.equals("example") || alertName.equals(".vscode")) {
                        continue;
                    }
                    if (!alerts.containsKey(alertName)) {
                        alerts.put(alertName, new AlertDisplayInfo());
                    }
                    AlertDisplayInfo alertDisplayInfo = alerts.get(alertName);
                    String alertFile = splitPath[1];
                    if (alertFile.equals("alert.json")) {
                        String json = CharStreams.toString(new InputStreamReader(zipInputStream, Charsets.UTF_8));
                        AlertManifest alertManifest = WatchdogPlugin.getInstance().getAlertManager().getGson().fromJson(json, AlertManifest.class);
                        alertManifest.setInternalName(alertName);
                        HttpUrl repoPage = GITHUB.newBuilder()
                            .addPathSegment("tree")
                            .addPathSegment("alert-hub")
                            .addPathSegment(alertName)
                            .build();
                        alertManifest.setRepo(repoPage.url());
                        alertDisplayInfo.manifest = alertManifest;
                    } else if (alertFile.equals("icon.png")) {
                        BufferedImage icon = ImageIO.read(zipInputStream);
                        alertDisplayInfo.icon = ImageUtil.resizeImage(icon, 242, 182, true);
                    }
                }
            }

            return alerts.values().stream().sorted(Comparator.comparing(alert -> alert.manifest.getDisplayName()))
                .collect(Collectors.toList());
        }


//        return Arrays.asList(new AlertManifest(
//            "testAlert",
//            "284hfu43hhfiu24rf",
//            "Test Alert",
//            "This is a test alert on the hub",
//            "4",
//            "adamk33n3r",
//            AlertHubCategory.COMBAT,
//            Arrays.asList("afk", "combat"),
//            new URL("https://github.com/adamk33n3r/runelite-watchdog"),
//            null,
//            false
//        ), new AlertManifest(
//            "testAlert",
//            "284hfu43hhfiu24rf",
//            "Test Alert",
//            "This is a test alert on the hub",
//            "4",
//            "adamk33n3r",
//            AlertHubCategory.COMBAT,
//            Arrays.asList("afk", "combat"),
//            new URL("https://github.com/adamk33n3r/runelite-watchdog"),
//            null,
//            false
//        ), new AlertManifest(
//            "testAlert",
//            "284hfu43hhfiu24rf",
//            "Test Alert",
//            "This is a test alert on the hub",
//            "4",
//            "adamk33n3r",
//            AlertHubCategory.COMBAT,
//            Arrays.asList("afk", "combat"),
//            new URL("https://github.com/adamk33n3r/runelite-watchdog"),
//            null,
//            false
//        ), new AlertManifest(
//            "testAlert2",
//            "284hfu43hhfiu24rf",
//            "Test Alert 2",
//            "This is a test alert on the hub with an extra long description to test wrapping",
//            "4",
//            "adamk33n3r",
//            AlertHubCategory.SKILLING,
//            Arrays.asList("mining", "tts"),
//            new URL("https://github.com/adamk33n3r/runelite-watchdog"),
//            null,
//            false
//        ));
    }

    static class CacheInterceptor implements Interceptor {
        private final int minutes;
        public CacheInterceptor(int minutes) {
            this.minutes = minutes;
        }

        @Override
        @Nonnull
        public Response intercept(Chain chain) throws IOException {
            Response response = chain.proceed(chain.request());

            CacheControl cacheControl = new CacheControl.Builder()
                .maxAge(this.minutes, TimeUnit.MINUTES)
                .build();

            return response.newBuilder()
                .removeHeader("Pragma")
                .removeHeader("Cache-Control")
                .header("Cache-Control", cacheControl.toString())
                .build();
        }
    }

    @Getter
    public static class AlertDisplayInfo {
        private AlertManifest manifest;
        private BufferedImage icon;
    }
}
