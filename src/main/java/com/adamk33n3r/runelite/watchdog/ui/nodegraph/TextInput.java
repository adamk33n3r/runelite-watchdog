package com.adamk33n3r.runelite.watchdog.ui.nodegraph;

import com.adamk33n3r.runelite.watchdog.ui.panels.PanelUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class TextInput extends JPanel {
    private final JLabel label;
    private final JTextArea textField;

    public TextInput() {
        this("Text", "");
    }

    public TextInput(String label, String text) {
//        LookAndFeel lookAndFeel = UIManager.getLookAndFeel();
//        try {
//            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
//        } catch (UnsupportedLookAndFeelException | ClassNotFoundException | InstantiationException | IllegalAccessException e) {
//            e.printStackTrace();
//        }
        this.label = new JLabel(label);
//        this.textField = new JTextField(text);
        this.textField = PanelUtils.createTextArea(label, label, text, (v) -> {});
        // TODO: move this adapter to a separate class the implements all listeners
//        this.textField.addMouseListener(new MouseAdapter() {
//            @Override
//            public void mousePressed(MouseEvent e) {
//                Component source = (Component) e.getSource();
//                MouseEvent mouseEvent = SwingUtilities.convertMouseEvent(source, e, source.getParent());
//                source.getParent().dispatchEvent(mouseEvent);
//            }
//        });
        this.setLayout(new BorderLayout());
//        this.add(this.label, BorderLayout.WEST);
        this.add(this.textField);
//        try {
//            UIManager.setLookAndFeel(lookAndFeel);
//        } catch (UnsupportedLookAndFeelException e) {
//            e.printStackTrace();
//        }
    }

    public void setLabel(String label) {
        this.label.setText(label);
    }

    public void setText(String text) {
        this.textField.setText(text);
    }
}
