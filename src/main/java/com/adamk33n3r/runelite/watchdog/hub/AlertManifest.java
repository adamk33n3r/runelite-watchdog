package com.adamk33n3r.runelite.watchdog.hub;

import lombok.Data;

import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Data
public class AlertManifest {
    private final String internalName;
    private final String commit;

    private final String displayName;
    private final String description;
    private final String compatibleVersion;
    private final String author;
    private final AlertHubCategory category;
    private final List<String> tags;
    private final URL repo;
    private final String json;
    private final boolean hasIcon;

    @Override
    public String toString()
    {
        return displayName;
    }

    public List<String> getKeywords() {
        Stream<String> keywords = Stream.of(
            this.getDisplayName(),
            this.getInternalName(),
            this.getAuthor()
//            this.getCategory().getName()
        );
        if (this.getTags() != null) {
            return Stream.concat(keywords, this.getTags().stream()).map(String::toUpperCase).collect(Collectors.toList());
        }
        return keywords.map(String::toUpperCase).collect(Collectors.toList());
    }
}
