package com.adamk33n3r.runelite.watchdog.ui.panels;

import com.adamk33n3r.runelite.watchdog.ui.PlaceholderTextArea;
import net.runelite.client.ui.DynamicGridLayout;
import net.runelite.client.util.ImageUtil;
import net.runelite.client.util.SwingUtil;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.filechooser.FileFilter;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Arrays;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class PanelUtils {
    private static final ImageIcon FOLDER_ICON;
    static {
        final BufferedImage folderImg = ImageUtil.loadImageResource(PanelUtils.class, "folder_icon.png");

        FOLDER_ICON = new ImageIcon(folderImg);
    }

    private PanelUtils () {}

    public static JPanel createLabeledComponent(String label, String tooltip, Component component) {
        return createLabeledComponent(label, tooltip, component, false);
    }

    public static JPanel createLabeledComponent(String label, String tooltip, Component component, boolean twoLines) {
        JPanel panel = new JPanel();
        if (twoLines) {
            panel.setLayout(new DynamicGridLayout(2, 0, 5, 5));
        } else {
            panel.setLayout(new BorderLayout(5, 0));
        }
        JLabel jLabel = new JLabel(label);
        jLabel.setToolTipText(tooltip);
        panel.add(jLabel, BorderLayout.WEST);
        panel.add(component);
        return panel;
    }

    public static JPanel createIconComponent(ImageIcon icon, String tooltip, Component component) {
        JPanel panel = new JPanel(new BorderLayout(5, 0));
        panel.setBackground(null);
        JLabel jLabel = new JLabel(icon);
        jLabel.setToolTipText(tooltip);
        panel.add(jLabel, BorderLayout.WEST);
        panel.add(component);
        return panel;
    }

    public static JPanel createFileChooser(String label, String tooltip, ActionListener actionListener, String path, String filterLabel, String... filters) {
        JPanel panel = new JPanel(new GridLayout(1, 1));
        panel.setBackground(null);
        if (label != null) {
            panel.setLayout(new GridLayout(2, 1));
            JLabel jLabel = new JLabel(label);
            jLabel.setToolTipText(tooltip);
            panel.add(jLabel);
        }
        JPanel chooserPanel = new JPanel(new BorderLayout(5, 0));
        chooserPanel.setBackground(null);
        panel.add(chooserPanel);
        JTextField pathField = new JTextField(path);
        pathField.setToolTipText(path);
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
        JButton fileChooserButton = new JButton(null, FOLDER_ICON);
        fileChooserButton.setToolTipText(tooltip);
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

    public static JButton createActionButton(ImageIcon icon, ImageIcon rolloverIcon, String tooltip, ButtonClickListener listener) {
        JButton actionButton = new JButton();
        SwingUtil.removeButtonDecorations(actionButton);
        actionButton.setPreferredSize(new Dimension(16, 16));
        actionButton.setIcon(icon);
        actionButton.setRolloverIcon(rolloverIcon);
        actionButton.setToolTipText(tooltip);
        actionButton.addActionListener(ev -> listener.clickPerformed(actionButton));
        return actionButton;
    }

    public interface ButtonClickListener {
        void clickPerformed(JButton button);
    }

    public static JButton createToggleActionButton(ImageIcon onIcon, ImageIcon onRolloverIcon, ImageIcon offIcon, ImageIcon offRolloverIcon, String onTooltip, String offTooltip, boolean initialValue, ButtonClickListener listener) {
        JButton actionButton = createActionButton(offIcon, offRolloverIcon, offTooltip, btn -> {
            btn.setSelected(!btn.isSelected());
            listener.clickPerformed(btn);
        });
        SwingUtil.addModalTooltip(actionButton, onTooltip, offTooltip);
        actionButton.setSelectedIcon(onIcon);
        actionButton.setRolloverSelectedIcon(onRolloverIcon);
        actionButton.setSelected(initialValue);
        return actionButton;
    }

    public static JCheckBox createCheckbox(String name, String tooltip, boolean initialValue, Consumer<Boolean> onChange) {
        JCheckBox checkbox = new JCheckBox(name, initialValue);
        checkbox.setToolTipText(tooltip);
        checkbox.addChangeListener(ev -> {
            onChange.accept(checkbox.isSelected());
        });
        return checkbox;
    }


    public static JTextArea createTextArea(String placeholder, String tooltip, String initialValue, Consumer<String> onChange) {
        PlaceholderTextArea textArea = new PlaceholderTextArea(initialValue);
        textArea.setPlaceholder(placeholder);
        textArea.setToolTipText(tooltip);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setMargin(new Insets(4, 6, 5, 6));
        textArea.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                textArea.selectAll();
            }

            @Override
            public void focusLost(FocusEvent e) {
                onChange.accept(textArea.getText());
            }
        });

        return textArea;
    }

    public static JSpinner createSpinner(int initialValue, int min, int max, int step, Consumer<Integer> onChange) {
        JSpinner spinner = new JSpinner(new SpinnerNumberModel(initialValue, min, max, step));
        spinner.addChangeListener(e -> {
            onChange.accept((Integer) spinner.getValue());
        });

        return spinner;
    }
}
