package com.adamk33n3r.runelite.watchdog.ui;

import net.runelite.client.ui.ColorScheme;

import lombok.Getter;

import javax.swing.*;
import javax.swing.text.Document;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Insets;
import java.awt.event.*;

/**
 * This component is a JTextArea with a flat design look.
 */
public class FlatTextArea extends JPanel
{
    @Getter
    private final PlaceholderTextArea textArea;

    //the default background color, this needs to be stored for hover effects
    @Getter
    private Color backgroundColor = ColorScheme.DARKER_GRAY_COLOR;

    //the default hover background color, this needs to be stored for hover effects
    @Getter
    private Color hoverBackgroundColor;

    // the input can be blocked (no clicking, no editing, no hover effects)
    @Getter
    private boolean blocked;

    public FlatTextArea(String placeholder)
    {
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

        add(textArea, BorderLayout.CENTER);

        textArea.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseEntered(MouseEvent mouseEvent)
            {
                if (blocked)
                {
                    return;
                }

                if (hoverBackgroundColor != null)
                {
                    setBackground(hoverBackgroundColor, false);
                }
            }

            @Override
            public void mouseExited(MouseEvent mouseEvent)
            {
                setBackground(backgroundColor);
            }
        });
    }

    public FlatTextArea(String placeholder, boolean preventNewline) {
        this(placeholder);
        if (!preventNewline) {
            return;
        }
        KeyStroke enterKey = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0);
        InputMap inputMap = this.textArea.getInputMap(JComponent.WHEN_FOCUSED);
        ActionMap actionMap = this.textArea.getActionMap();
        inputMap.put(enterKey, enterKey.toString());
        actionMap.put(enterKey.toString(), new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JTextArea textArea = (JTextArea)e.getSource();
                textArea.getParent().getParent().requestFocusInWindow();
            }
        });
    }

    public String getText()
    {
        return textArea.getText();
    }

    public void setText(String text)
    {
        textArea.setText(text);
    }

    @Override
    public void addKeyListener(KeyListener keyListener)
    {
        textArea.addKeyListener(keyListener);
    }

    @Override
    public void removeKeyListener(KeyListener keyListener)
    {
        textArea.removeKeyListener(keyListener);
    }

    @Override
    public void setBackground(Color color)
    {
        setBackground(color, true);
    }

    @Override
    public boolean requestFocusInWindow()
    {
        return textArea.requestFocusInWindow();
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
        textArea.setEditable(editable);
        textArea.setFocusable(editable);
        if (!editable)
        {
            super.setBackground(backgroundColor);
        }
    }

    public Document getDocument()
    {
        return textArea.getDocument();
    }

}
