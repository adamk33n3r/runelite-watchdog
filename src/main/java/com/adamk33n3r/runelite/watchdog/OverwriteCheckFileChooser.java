package com.adamk33n3r.runelite.watchdog;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;

public class OverwriteCheckFileChooser extends JFileChooser {
    private String extension;
    public OverwriteCheckFileChooser(String description, String extension) {
        super();
        this.extension = extension;
        this.setFileFilter(new FileNameExtensionFilter(description, extension));
    }

    @Override
    public File getSelectedFile() {
        File selectedFile = super.getSelectedFile();
        if (selectedFile != null) {
            String name = selectedFile.getName();
            if (!name.contains(".")) {
                selectedFile = new File(selectedFile.getAbsolutePath() + "." + this.extension);
            }
        }

        return selectedFile;
    }

    @Override
    public void approveSelection() {
        if (this.getDialogType() == SAVE_DIALOG) {
            File selectedFile = getSelectedFile();
            if (selectedFile != null && selectedFile.exists()) {
                int result = JOptionPane.showConfirmDialog(
                    this,
                    "Overwrite existing file?",
                    "Overwrite?",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
                );

                if (result != JOptionPane.YES_OPTION) {
                    return;
                }
            }
        }

        super.approveSelection();
    }
}
