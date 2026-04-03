package com.adamk33n3r.runelite.watchdog;

import net.runelite.client.RuneLite;
import net.runelite.client.config.ConfigManager;

import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Comparator;
import java.util.zip.GZIPOutputStream;

@Slf4j
@Singleton
public class AlertBackupManager {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Inject
    private WatchdogConfig config;

    @Inject
    private ConfigManager configManager;

    private String getSafeProfileName() {
        String name = configManager.getProfile().getName();
        if (name == null || name.isEmpty()) {
            return "default";
        }
        return name.replaceAll("[\\\\/:*?\"<>|]", "_");
    }

    public File getBackupDir() {
        return new File(RuneLite.RUNELITE_DIR, "watchdog/backups/" + getSafeProfileName());
    }

    public void backup(String json) {
        if (!config.backupEnabled()) {
            return;
        }

        File backupDir = getBackupDir();
        if (!backupDir.exists() && !backupDir.mkdirs()) {
            log.warn("Failed to create backup directory: {}", backupDir.getAbsolutePath());
            return;
        }

        String filename = "alerts_" + LocalDate.now().format(DATE_FORMATTER) + ".json.gz";
        File backupFile = new File(backupDir, filename);

        try (FileOutputStream fos = new FileOutputStream(backupFile);
             GZIPOutputStream gzos = new GZIPOutputStream(fos)) {
            gzos.write(json.getBytes());
        } catch (IOException e) {
            log.warn("Failed to write alert backup to {}", backupFile.getAbsolutePath(), e);
            return;
        }

        pruneOldBackups(backupDir);
    }

    private void pruneOldBackups(File backupDir) {
        File[] files = backupDir.listFiles((dir, name) -> name.startsWith("alerts_") && name.endsWith(".json.gz"));
        if (files == null) {
            return;
        }

        Arrays.sort(files, Comparator.comparingLong(File::lastModified).reversed());

        int maxBackups = config.backupRetentionDays();
        for (int i = maxBackups; i < files.length; i++) {
            if (!files[i].delete()) {
                log.warn("Failed to delete old backup file: {}", files[i].getName());
            }
        }
    }
}
