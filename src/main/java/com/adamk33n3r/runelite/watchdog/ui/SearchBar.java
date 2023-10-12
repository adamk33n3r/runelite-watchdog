package com.adamk33n3r.runelite.watchdog.ui;

import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.PluginPanel;
import net.runelite.client.ui.components.IconTextField;

import javax.swing.ImageIcon;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.Dimension;
import java.util.Objects;
import java.util.function.Consumer;

public class SearchBar extends IconTextField {
    public SearchBar(Consumer<String> onSearch) {
        super();
        this.setIcon(new ImageIcon(Objects.requireNonNull(IconTextField.class.getResource(Icon.SEARCH.getFile()))));
        this.setPreferredSize(new Dimension(PluginPanel.PANEL_WIDTH - PluginPanel.SCROLLBAR_WIDTH, 30));
        this.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        this.setHoverBackgroundColor(ColorScheme.DARK_GRAY_HOVER_COLOR);
        this.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                onSearch.accept(getText());
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                onSearch.accept(getText());
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                onSearch.accept(getText());
            }
        });
    }
}
