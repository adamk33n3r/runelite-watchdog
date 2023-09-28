package com.adamk33n3r.runelite.watchdog.hub;

import com.adamk33n3r.runelite.watchdog.alerts.Alert;
import lombok.Data;
import lombok.Setter;

import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Data
public class AlertManifest {
    @Setter
    private String internalName;
    private URL repo;
    private String commit;

    private final String displayName;
    private final String description;
    private final String compatibleVersion;
    private final String author;
    private final AlertHubCategory category;
    private final List<String> tags;
    private final Alert alert;
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
            this.getAuthor(),
            this.getCategory().getName()
        ).filter(Objects::nonNull);
        if (this.getTags() != null) {
            System.out.println(this.getTags());
            return Stream.concat(keywords, this.getTags().stream()).map(String::toUpperCase).collect(Collectors.toList());
        }
        return keywords.map(String::toUpperCase).collect(Collectors.toList());
    }
}
