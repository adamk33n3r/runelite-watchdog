package com.adamk33n3r.runelite.watchdog.ui;

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

        JPanel wrapper = this.createWrapper();
        this.add(wrapper);

        wrapper.add(new JLabel("Paste the Alert JSON here"), BorderLayout.NORTH);

        JTextArea textArea = new JTextArea();
        textArea.setLineWrap(true);
        JScrollPane scrollPane = new JScrollPane(textArea);
        wrapper.add(scrollPane, BorderLayout.CENTER);

        JPanel btnGroup = new JPanel(new GridLayout(1, 2, 25, 0));
        Function<Boolean, ActionListener> importAlertFn = append -> ev -> {
            if (!append && JOptionPane.showConfirmDialog(this, "Are you sure you wish to overwrite your alerts?", "Confirm Overwrite?", JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION) {
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
    public ImportExportDialog(Component parent, String exportString) {
        this.setTitle("Export");
        this.setSize(500, 250);
        this.setLocationRelativeTo(parent);
        this.setModal(true);
        this.setUndecorated(true);

        JPanel wrapper = this.createWrapper();
        this.add(wrapper);
        wrapper.add(new JLabel("Exported Alert JSON"), BorderLayout.NORTH);
        JPanel btnGroup = new JPanel(new GridLayout(1, 2, 25, 0));
        JButton copyBtn = new JButton("Copy to Clipboard");
        copyBtn.addActionListener(ev -> {
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(new StringSelection(exportString), null);
            this.setVisible(false);
        });
        btnGroup.add(copyBtn);
        JButton closeBtn = new JButton("Close");
        btnGroup.add(closeBtn);
        closeBtn.addActionListener(ev -> {
            this.setVisible(false);
        });
        wrapper.add(btnGroup, BorderLayout.SOUTH);

        JTextArea textArea = new JTextArea(exportString);
        textArea.setLineWrap(true);
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);
        wrapper.add(scrollPane, BorderLayout.CENTER);
    }

    private JPanel createWrapper() {
        JPanel wrapper = new JPanel(new BorderLayout(5, 5));
        wrapper.setBorder(new EmptyBorder(10, 10, 10, 10));
        return wrapper;
    }
}
