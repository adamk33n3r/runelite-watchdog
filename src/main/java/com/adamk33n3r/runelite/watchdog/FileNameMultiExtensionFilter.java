package com.adamk33n3r.runelite.watchdog;

import lombok.Getter;

import javax.swing.filechooser.FileFilter;
import java.io.File;
import java.util.Locale;

/**
 * Mostly copied from {@link javax.swing.filechooser.FileNameExtensionFilter} but uses endsWith instead of equals
 * so that it can match a multi-part extension.
 */
public class FileNameMultiExtensionFilter extends FileFilter {
    @Getter
    private final String description;
    private final String[] extensions;
    private final String[] lowerCaseExtensions;

    public FileNameMultiExtensionFilter(String description, String... extensions) {
        if (extensions == null || extensions.length == 0) {
            throw new IllegalArgumentException(
                "Extensions must be non-null and not empty");
        }
        this.description = description;
        this.extensions = new String[extensions.length];
        this.lowerCaseExtensions = new String[extensions.length];
        for (int i = 0; i < extensions.length; i++) {
            if (extensions[i] == null || extensions[i].length() == 0) {
                throw new IllegalArgumentException(
                    "Each extension must be non-null and not empty");
            }
            this.extensions[i] = extensions[i];
            lowerCaseExtensions[i] = extensions[i].toLowerCase(Locale.ENGLISH);
        }
    }

    public boolean accept(File f) {
        if (f != null) {
            if (f.isDirectory()) {
                return true;
            }
            // NOTE: we tested implementations using Maps, binary search
            // on a sorted list and this implementation. All implementations
            // provided roughly the same speed, most likely because of
            // overhead associated with java.io.File. Therefor we've stuck
            // with the simple lightweight approach.
            String fileName = f.getName();
            String desiredExtension = fileName.toLowerCase(Locale.ENGLISH);
            for (String extension : lowerCaseExtensions) {
                if (desiredExtension.endsWith(extension)) {
                    return true;
                }
            }
        }
        return false;
    }
}
