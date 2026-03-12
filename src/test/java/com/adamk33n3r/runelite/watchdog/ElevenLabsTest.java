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
import java.util.Properties;
import java.util.concurrent.CountDownLatch;

@RunWith(MockitoJUnitRunner.class)
public class ElevenLabsTest extends TestBase {
    private static String apiKey;
    private static final String ENV_VAR_NAME = "ELEVEN_LABS_API_KEY";

    @BeforeClass
    public static void setup() {
        apiKey = System.getenv(ENV_VAR_NAME);
        if (apiKey == null) {
            System.err.println("API Key environment variable '" + ENV_VAR_NAME + "' is not set. Trying to load from test.properties");
            Properties properties = new Properties();
            try {
                properties.load(ElevenLabsTest.class.getResourceAsStream("/test.properties"));
                apiKey = properties.getProperty(ENV_VAR_NAME);
                if (apiKey == null) {
                    System.err.println("API Key property '" + ENV_VAR_NAME + "' is not set.");
                    throw new RuntimeException("API Key property '" + ENV_VAR_NAME + "' is not set.");
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
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
