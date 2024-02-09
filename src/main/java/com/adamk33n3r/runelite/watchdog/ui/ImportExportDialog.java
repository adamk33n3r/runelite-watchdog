package com.adamk33n3r.runelite.watchdog.ui;

import com.adamk33n3r.runelite.watchdog.WatchdogPlugin;
import com.adamk33n3r.runelite.watchdog.alerts.Alert;
import com.adamk33n3r.runelite.watchdog.ui.panels.PanelUtils;

import net.runelite.client.config.RuneLiteConfig;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionListener;
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
        this.setAlwaysOnTopIfNeeded();

        JPanel wrapper = this.createWrapper();
        this.add(wrapper);

        wrapper.add(new JLabel("Paste the Alert JSON here"), BorderLayout.NORTH);

        JTextArea textArea = new JTextArea();
        textArea.setLineWrap(true);
        JScrollPane scrollPane = new JScrollPane(textArea);
        wrapper.add(scrollPane, BorderLayout.CENTER);

        JPanel btnGroup = new JPanel(new GridLayout(1, 0, 25, 0));
        Function<Boolean, ActionListener> importAlertFn = append -> ev -> {
            if (!append && JOptionPane.showConfirmDialog(this, "Are you sure you wish to overwrite your alerts?", "Confirm Overwrite?", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
                return;
            }
            String json = textArea.getText();
            try {
                if (onImport.apply(json, append)) {
                    this.setVisible(false);
                }
            } catch (Exception ex) {
                log.error("Error parsing json: " + ex);
                JOptionPane.showMessageDialog(this, "There was an error parsing the alert json", "Error parsing JSON", JOptionPane.ERROR_MESSAGE);
            }
        };
        JButton importOverwriteBtn = new JButton("Import (Overwrite)");
        importOverwriteBtn.addActionListener(importAlertFn.apply(false));
        btnGroup.add(importOverwriteBtn);
        JButton importAppendBtn = new JButton("Import (Append)");
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
        this.show(parent, json, pretty);
    }

    public ImportExportDialog(Component parent, List<Alert> alerts) {
        Gson gson = WatchdogPlugin.getInstance().getAlertManager().getGson();
        String json = gson.toJson(alerts);
        String pretty = gson.newBuilder().setPrettyPrinting().create().toJson(alerts);
        this.show(parent, json, pretty);
    }

    public void show(Component parent, String exportString, String prettyExportString) {
        this.setTitle("Export");
        this.setSize(500, 250);
        this.setLocationRelativeTo(parent);
        this.setModal(true);
        this.setUndecorated(true);
        this.setAlwaysOnTopIfNeeded();

        JTextArea textArea = new JTextArea(exportString);
        textArea.setLineWrap(true);
        textArea.setEditable(false);
        SwingUtilities.invokeLater(textArea::requestFocusInWindow);

        JPanel wrapper = this.createWrapper();
        this.add(wrapper);
        JPanel top = new JPanel(new BorderLayout());
        top.add(new JLabel("Exported Alert JSON"), BorderLayout.WEST);
        JCheckBox prettyPrint = PanelUtils.createCheckbox("Pretty Print", "Pretty Print", false, (selected) -> {
            textArea.setText(selected ? prettyExportString : exportString);
            textArea.setCaretPosition(0);
            textArea.requestFocusInWindow();
        });
        top.add(prettyPrint, BorderLayout.EAST);
        wrapper.add(top, BorderLayout.NORTH);
        JPanel btnGroup = new JPanel(new GridLayout(1, 2, 25, 0));
        JButton copyBtn = new JButton("Copy to Clipboard");
        copyBtn.addActionListener(ev -> {
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(new StringSelection(prettyPrint.isSelected() ? prettyExportString : exportString), null);
            this.setVisible(false);
        });
        btnGroup.add(copyBtn);
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

    private void setAlwaysOnTopIfNeeded() {
        RuneLiteConfig runeLiteConfig = WatchdogPlugin.getInstance().getInjector().getInstance(RuneLiteConfig.class);
        if (runeLiteConfig.gameAlwaysOnTop() && this.isAlwaysOnTopSupported()) {
            this.setAlwaysOnTop(true);
        }
    }
}
