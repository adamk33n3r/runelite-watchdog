package com.adamk33n3r.runelite.watchdog;

import lombok.AccessLevel;
import lombok.Getter;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class WatchdogProperties {
    @Getter(AccessLevel.PACKAGE)
    private static final Properties properties = new Properties();

    static {
        try (InputStream in = WatchdogProperties.class.getResourceAsStream("watchdog.properties")) {
            properties.load(in);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
}
