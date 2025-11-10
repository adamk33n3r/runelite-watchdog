package com.adamk33n3r.runelite.watchdog;

import com.adamk33n3r.runelite.watchdog.elevenlabs.ElevenLabs;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;

@RunWith(MockitoJUnitRunner.class)
public class ElevenLabsTest extends TestBase {
    private static String apiKey;

    @BeforeClass
    public static void setup() {
        Properties properties = new Properties();
        try (InputStream inputStream = ElevenLabsTest.class.getClassLoader().getResourceAsStream("test-secrets.properties")) {
            if (inputStream == null) {
                System.err.println("Required secrets file not found");
            } else {
                properties.load(inputStream);
                apiKey = properties.getProperty("elevenlabs.apiKey");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Before
    public void before() throws NoSuchFieldException {
        super.before();
        Mockito.when(this.watchdogConfig.elevenLabsAPIKey()).thenReturn(apiKey);
    }

    @Test
    public void test_get_voices() throws InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        ElevenLabs.getVoices(WatchdogPlugin.getInstance().getHttpClient(), voices -> {
            Assert.assertFalse(voices.getVoices().isEmpty());
            countDownLatch.countDown();
        }, Assert::fail);
        countDownLatch.await();
    }

    @Test
    public void test_generate_tts() throws InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        ElevenLabs.getVoices(WatchdogPlugin.getInstance().getHttpClient(), voices -> {
            ElevenLabs.generateTTS(WatchdogPlugin.getInstance().getHttpClient(), voices.getVoices().get(0), "This is a test", file -> {
                Assert.assertTrue(file.exists());
                if (!file.delete()) {
                    System.err.println("Failed to delete temp file");
                }
                countDownLatch.countDown();
            }, Assert::fail);
        }, Assert::fail);
        countDownLatch.await();
    }
}
