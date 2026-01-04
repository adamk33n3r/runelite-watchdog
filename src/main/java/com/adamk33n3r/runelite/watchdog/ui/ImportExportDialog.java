package com.adamk33n3r.runelite.watchdog.ui;

import com.adamk33n3r.runelite.watchdog.OverwriteCheckFileChooser;
import com.adamk33n3r.runelite.watchdog.Util;
import com.adamk33n3r.runelite.watchdog.WatchdogPlugin;
import com.adamk33n3r.runelite.watchdog.alerts.Alert;
import com.adamk33n3r.runelite.watchdog.ui.panels.PanelUtils;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

@Slf4j
public class ImportExportDialog extends JDialog {
    // Import
    public ImportExportDialog(Component parent, BiFunction<String, Boolean, Boolean> onImport) {
        this.setTitle("Import");
        this.setSize(500, 250);
        this.setLocationRelativeTo(parent);
        this.setModal(true);
        this.setUndecorated(true);
        Util.syncAlwaysOnTop(this);

        JPanel wrapper = this.createWrapper();
        this.add(wrapper);

        wrapper.add(new JLabel("Paste the Alert here"), BorderLayout.NORTH);

        JTextArea textArea = new JTextArea();
        textArea.setLineWrap(true);
        PanelUtils.addPasteMenu(textArea);

        JScrollPane scrollPane = new JScrollPane(textArea);
        wrapper.add(scrollPane, BorderLayout.CENTER);

        JPanel btnGroup = new JPanel(new GridLayout(1, 0, 25, 0));
        Function<Boolean, ActionListener> importAlertFn = append -> ev -> {
            if (!append && JOptionPane.showConfirmDialog(this, "Are you sure you wish to replace all of the alerts?", "Confirm Replace?", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
                return;
            }
            String json = textArea.getText();

            if (Util.isCompressed(json)) {
                json = Util.decompressAlerts(json);
            }

            try {
                if (onImport.apply(json, append)) {
                    this.setVisible(false);
                }
            } catch (Exception ex) {
                log.error("Error parsing json", ex);
                JOptionPane.showMessageDialog(this, "There was an error parsing the alert json", "Error parsing JSON", JOptionPane.ERROR_MESSAGE);
            }
        };
        JButton importOverwriteBtn = new JButton("Delete All & Import");
        importOverwriteBtn.addActionListener(importAlertFn.apply(false));
        btnGroup.add(importOverwriteBtn);
        JButton importAppendBtn = new JButton("Import");
        importAppendBtn.addActionListener(importAlertFn.apply(true));
        btnGroup.add(importAppendBtn);
        JButton closeBtn = new JButton("Cancel");
        closeBtn.addActionListener(ev -> {
            this.setVisible(false);
        });
        btnGroup.add(closeBtn);
        wrapper.add(btnGroup, BorderLayout.SOUTH);
    }

    // Export
    public ImportExportDialog(Component parent, Alert alert) {
        Gson gson = WatchdogPlugin.getInstance().getAlertManager().getGson();
        String json = gson.toJson(alert);
        String pretty = gson.newBuilder().setPrettyPrinting().create().toJson(alert);
        this.showExport(parent, json, pretty);
    }

    public ImportExportDialog(Component parent, List<Alert> alerts) {
        Gson gson = WatchdogPlugin.getInstance().getAlertManager().getGson();
        String json = gson.toJson(alerts);
        String pretty = gson.newBuilder().setPrettyPrinting().create().toJson(alerts);
        this.showExport(parent, json, pretty);
    }

    private void showExport(Component parent, String exportString, String prettyExportString) {
        this.setTitle("Export");
        this.setSize(500, 250);
        this.setLocationRelativeTo(parent);
        this.setModal(true);
        this.setUndecorated(true);
        Util.syncAlwaysOnTop(this);
        var compressedExportString = Util.compressAlerts(exportString);

        JTextArea textArea = new JTextArea(exportString);
        textArea.setLineWrap(true);
        textArea.setEditable(false);
        SwingUtilities.invokeLater(textArea::requestFocusInWindow);

        JPanel wrapper = this.createWrapper();
        this.add(wrapper);
        JPanel top = new JPanel(new BorderLayout());
        top.add(new JLabel("Exported Alert"), BorderLayout.WEST);
        JCheckBox prettyPrint = PanelUtils.createCheckbox("Pretty Print", "Pretty Print", false, (selected) -> {
            textArea.setText(selected ? prettyExportString : exportString);
            textArea.setCaretPosition(0);
            textArea.requestFocusInWindow();
        });
        JCheckBox compress = PanelUtils.createCheckbox("Compress", "Compress with GZIP", false, (selected) -> {
            prettyPrint.setEnabled(!selected);
            textArea.setText(selected ? compressedExportString : (prettyPrint.isSelected() ? prettyExportString : exportString));
            textArea.setCaretPosition(0);
            textArea.requestFocusInWindow();
        });
        var checkboxes = new JPanel(new GridLayout(1, 2, 3, 3));
        checkboxes.add(compress);
        checkboxes.add(prettyPrint);
        top.add(checkboxes, BorderLayout.EAST);
        wrapper.add(top, BorderLayout.NORTH);
        JPanel btnGroup = new JPanel(new GridLayout(1, 2, 25, 0));
        JButton copyBtn = new JButton("Copy to Clipboard");
        copyBtn.addActionListener(ev -> {
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(new StringSelection(textArea.getText()), null);
            this.setVisible(false);
        });
        btnGroup.add(copyBtn);
        JButton saveToFileBtn = new JButton("Save to File");
        saveToFileBtn.addActionListener(ev -> {
            JFileChooser fileChooser = compress.isSelected()
                ? new OverwriteCheckFileChooser("Compressed JSON Files (*.json.gz)", "json.gz")
                : new OverwriteCheckFileChooser("JSON Files (*.json)", "json");
            int userSelection = fileChooser.showSaveDialog(this);
            if (userSelection == JFileChooser.APPROVE_OPTION) {
                File fileToSave = fileChooser.getSelectedFile();

                try {
                    if (compress.isSelected()) {
                        try (FileOutputStream fileOutputStream = new FileOutputStream(fileToSave)) {
                            fileOutputStream.write(Base64.getDecoder().decode(compressedExportString));
                        }
                    } else {
                        try (FileWriter fileWriter = new FileWriter(fileToSave)) {
                            fileWriter.write(prettyPrint.isSelected() ? prettyExportString : exportString);
                        }
                    }
                } catch (IOException e) {
                    log.error("Error writing file", e);
                    JOptionPane.showMessageDialog(this, "There was an error saving the file", "Error saving file", JOptionPane.ERROR_MESSAGE);
                }
                this.setVisible(false);
            }
        });
        btnGroup.add(saveToFileBtn);
        JButton closeBtn = new JButton("Close");
        btnGroup.add(closeBtn);
        closeBtn.addActionListener(ev -> {
            this.setVisible(false);
        });
        wrapper.add(btnGroup, BorderLayout.SOUTH);

        JScrollPane scrollPane = new JScrollPane(textArea);
        wrapper.add(scrollPane, BorderLayout.CENTER);
    }

    private JPanel createWrapper() {
        JPanel wrapper = new JPanel(new BorderLayout(5, 5));
        wrapper.setBorder(new EmptyBorder(10, 10, 10, 10));
        return wrapper;
    }
}
