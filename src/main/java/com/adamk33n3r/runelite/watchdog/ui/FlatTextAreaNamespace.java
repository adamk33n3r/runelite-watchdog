package com.adamk33n3r.runelite.watchdog.ui;

import com.google.common.base.Strings;
import lombok.Getter;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.DynamicGridLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * This component is a JTextArea with a flat design look.
 */
@Getter
public class FlatTextAreaNamespace extends FlatTextArea {
    private final PlaceholderTextArea prefixTextArea;
    private final String splitter;

    public FlatTextAreaNamespace(String firstPlaceholder, String splitter, String secondPlaceholder) {
        this(firstPlaceholder, splitter, secondPlaceholder, false);
    }

    public FlatTextAreaNamespace(String prefixPlaceholder, String splitter, String secondPlaceholder, boolean preventNewline) {
        super(secondPlaceholder, preventNewline);
        this.setLayout(new DynamicGridLayout(1, 3));
        this.splitter = splitter;

        this.prefixTextArea = new PlaceholderTextArea();
        this.prefixTextArea.setPlaceholder(prefixPlaceholder);
        this.prefixTextArea.setOpaque(false);
        this.prefixTextArea.setSelectedTextColor(Color.WHITE);
        this.prefixTextArea.setSelectionColor(ColorScheme.BRAND_ORANGE_TRANSPARENT);
        this.prefixTextArea.setLineWrap(true);
        this.prefixTextArea.setWrapStyleWord(true);
        this.prefixTextArea.setMargin(new Insets(4, 6, 5, 6));
//        this.prefixTextArea.setPreferredSize(new Dimension(60, 0));

        this.removeAll();
        this.add(this.prefixTextArea);

        this.prefixTextArea.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent mouseEvent) {
                if (blocked) {
                    return;
                }

                if (hoverBackgroundColor != null) {
                    setBackground(hoverBackgroundColor, false);
                }
            }

            @Override
            public void mouseExited(MouseEvent mouseEvent) {
                setBackground(backgroundColor);
            }
        });

        var splitterLabel = new JLabel(":");
        splitterLabel.setMaximumSize(new Dimension(10, 0));
        this.add(splitterLabel);
        this.add(this.textArea);

        this.setUpKeymaps(preventNewline);
        this.setUpKeymaps(preventNewline, this.prefixTextArea);
    }

    public String getText() {
        if (this.prefixTextArea.getText().isEmpty() && this.textArea.getText().isEmpty()) {
            return "";
        }
        return this.prefixTextArea.getText() + this.splitter + this.textArea.getText();
    }

    public void setText(String text) {
        if (Strings.isNullOrEmpty(text)) {
            return;
        }
        String[] split = text.split(this.splitter);
        if (split.length != 2) {
            return;
        }
        this.prefixTextArea.setText(split[0]);
        this.textArea.setText(split[1]);
    }
}
