package com.adamk33n3r.runelite.watchdog.ui.panels;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Arrays;
import java.util.stream.Collectors;

public class PanelUtils {
    private PanelUtils () {}

    public static JPanel createLabeledComponent(String label, Component component) {
        JPanel panel = new JPanel(new BorderLayout(5, 0));
        panel.add(new JLabel(label), BorderLayout.WEST);
        panel.add(component);
        return panel;
    }

    public static JPanel createFileChooser(String label, ActionListener actionListener, String path, String filterLabel, String... filters) {
        JPanel panel = new JPanel(new GridLayout(2, 1));
        panel.add(new JLabel(label), BorderLayout.WEST);
        JPanel chooserPanel = new JPanel(new BorderLayout(5, 0));
        panel.add(chooserPanel);
        JTextField pathField = new JTextField(path);
        pathField.setEditable(false);
        chooserPanel.add(pathField);
        JFileChooser fileChooser = new JFileChooser(path);
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.setFileFilter(new FileFilter() {
            @Override
            public boolean accept(File file) {
                if (file.isDirectory()) {
                    return true;
                }
                String fileName = file.getName();
                int i = fileName.lastIndexOf('.');
                String extension = null;
                if (i > 0 &&  i < fileName.length() - 1) {
                    extension = fileName.substring(i+1).toLowerCase();
                }
                if (extension != null) {
                    return Arrays.asList(filters).contains(extension);
                }
                return false;
            }

            @Override
            public String getDescription() {
                return filterLabel + Arrays.stream(filters).map(ft -> "*." + ft).collect(Collectors.joining(", ", " (", ")"));
            }
        });
        JButton fileChooserButton = new JButton("Browse...");
        fileChooserButton.addActionListener(e -> {
            int result = fileChooser.showOpenDialog(panel);
            if (result == JFileChooser.APPROVE_OPTION) {
                String absPath = fileChooser.getSelectedFile().getAbsolutePath();
                pathField.setText(absPath);
                pathField.setToolTipText(absPath);
                actionListener.actionPerformed(new ActionEvent(fileChooser, result, "selected"));
            }
        });
        chooserPanel.add(fileChooserButton, BorderLayout.EAST);
        return panel;
    }
}
