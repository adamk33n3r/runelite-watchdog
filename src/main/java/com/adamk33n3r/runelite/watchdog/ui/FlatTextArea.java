package com.adamk33n3r.runelite.watchdog.ui;

import net.runelite.client.ui.ColorScheme;

import lombok.Getter;

import javax.swing.*;
import javax.swing.text.Document;
import java.awt.*;
import java.awt.event.*;

/**
 * This component is a JTextArea with a flat design look.
 */
@Getter
public class FlatTextArea extends JPanel {
    protected final PlaceholderTextArea textArea;

    //the default background color, this needs to be stored for hover effects
    protected Color backgroundColor = ColorScheme.DARKER_GRAY_COLOR;

    //the default hover background color, this needs to be stored for hover effects
    protected Color hoverBackgroundColor;

    // the input can be blocked (no clicking, no editing, no hover effects)
    protected boolean blocked;

    public FlatTextArea(String placeholder) {
        this(placeholder, false);
    }

    public FlatTextArea(String placeholder, boolean preventNewline) {
        this.setLayout(new BorderLayout());
//        this.setBorder(new EmptyBorder(0, 10, 0, 0));
        this.setBackground(this.backgroundColor);
//        setBorder(null);

        this.textArea = new PlaceholderTextArea();
        this.textArea.setPlaceholder(placeholder);
//        this.textArea.setBorder(null);
        this.textArea.setOpaque(false);
        this.textArea.setSelectedTextColor(Color.WHITE);
        this.textArea.setSelectionColor(ColorScheme.BRAND_ORANGE_TRANSPARENT);
        this.textArea.setLineWrap(true);
        this.textArea.setWrapStyleWord(true);
        this.textArea.setMargin(new Insets(4, 6, 5, 6));

        this.add(textArea, BorderLayout.CENTER);

        textArea.addMouseListener(new MouseAdapter() {
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

        this.setUpKeymaps(preventNewline);
    }

    protected void setUpKeymaps(boolean preventNewline) {
        this.setUpKeymaps(preventNewline, this.textArea);
    }

    protected void setUpKeymaps(boolean preventNewline, PlaceholderTextArea textArea) {
        KeyStroke enterKey = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0);
        KeyStroke tabKey = KeyStroke.getKeyStroke(KeyEvent.VK_TAB, 0);
        InputMap inputMap = textArea.getInputMap(JComponent.WHEN_FOCUSED);
        ActionMap actionMap = textArea.getActionMap();
        if (preventNewline) {
            inputMap.put(enterKey, enterKey.toString());
            actionMap.put(enterKey.toString(), new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    JTextArea textArea = (JTextArea) e.getSource();
                    textArea.getParent().getParent().requestFocusInWindow();
                }
            });
        }
        inputMap.put(tabKey, tabKey.toString());
        actionMap.put(tabKey.toString(), new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                KeyboardFocusManager.getCurrentKeyboardFocusManager().focusNextComponent();
            }
        });
    }

    public String getText()
    {
        return this.textArea.getText();
    }

    public void setText(String text)
    {
        this.textArea.setText(text);
    }

    @Override
    public void addKeyListener(KeyListener keyListener)
    {
        this.textArea.addKeyListener(keyListener);
    }

    @Override
    public void removeKeyListener(KeyListener keyListener)
    {
        this.textArea.removeKeyListener(keyListener);
    }

    @Override
    public void setBackground(Color color)
    {
        this.setBackground(color, true);
    }

    @Override
    public boolean requestFocusInWindow()
    {
        return this.textArea.requestFocusInWindow();
    }

    public void setBackground(Color color, boolean saveColor)
    {
        if (color == null)
        {
            return;
        }

        super.setBackground(color);

        if (saveColor)
        {
            this.backgroundColor = color;
        }
    }

    public void setHoverBackgroundColor(Color color)
    {
        if (color == null)
        {
            return;
        }

        this.hoverBackgroundColor = color;
    }

    public void setEditable(boolean editable)
    {
        this.blocked = !editable;
        this.textArea.setEditable(editable);
        this.textArea.setFocusable(editable);
        if (!editable)
        {
            super.setBackground(this.backgroundColor);
        }
    }

    public Document getDocument()
    {
        return this.textArea.getDocument();
    }

}
