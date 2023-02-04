package com.adamk33n3r.runelite.watchdog;

import lombok.Getter;

public class Version implements Comparable<Version> {
    @Getter
    private final String version;

    public Version(String version) {
        if (version != null && !version.matches("[0-9]+(\\.[0-9]+)*")) {
            throw new IllegalArgumentException("Invalid version format");
        }
        this.version = version;
    }

    @Override
    public int compareTo(Version o) {
        if (o == null || o.version == null) {
            return 1;
        }

        if (this.version == null) {
            return -1;
        }

        String[] thisParts = this.version.split("\\.");
        String[] thatParts = o.version.split("\\.");
        int length = Math.max(thisParts.length, thatParts.length);
        for (int i = 0; i < length; i++) {
            int thisPart = i < thisParts.length ? Integer.parseInt(thisParts[i]) : 0;
            int thatPart = i < thatParts.length ? Integer.parseInt(thatParts[i]) : 0;
            if (thisPart < thatPart) {
                return -1;
            }
            if (thisPart > thatPart) {
                return 1;
            }
        }

        return 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null) {
            return false;
        }
        if (this.getClass() != o.getClass()) {
            return false;
        }

        return this.compareTo((Version) o) == 0;
    }

    @Override
    public String toString() {
        return this.version == null ? null : "v" + this.version;
    }
}
