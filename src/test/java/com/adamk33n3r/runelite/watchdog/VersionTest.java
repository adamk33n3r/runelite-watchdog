package com.adamk33n3r.runelite.watchdog;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class VersionTest {
    @Test
    public void compare_versions() throws Exception {
        Version a = new Version("1.1");
        Version b = new Version("1.1.1");
        assert(a.compareTo(b) < 0);
        assert(!a.equals(b));

        a = new Version("2.0");
        b = new Version("1.9.9");
        assert(a.compareTo(b) > 0);
        assert(!a.equals(b));

        a = new Version("1.0");
        b = new Version("1");
        assert(a.compareTo(b) == 0);
        assert(a.equals(b));

        a = new Version("1");
        b = new Version(null);
        assert(a.compareTo(b) > 0);
        assert(!a.equals(b));

        List<Version> versions = new ArrayList<Version>();
        versions.add(new Version("2"));
        versions.add(new Version("1.0.5"));
        versions.add(new Version("1.01.0"));
        versions.add(new Version("1.00.1"));
        assert(Collections.min(versions).equals(new Version("1.0.1")));
        assert(Collections.max(versions).equals(new Version("2.0.0")));
    }
}
