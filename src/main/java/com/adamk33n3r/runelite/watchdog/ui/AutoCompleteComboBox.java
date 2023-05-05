package com.adamk33n3r.runelite.watchdog.ui;

import javax.swing.*;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import javax.swing.text.PlainDocument;
import java.util.Arrays;

public class AutoCompleteComboBox<T> extends JComboBox<T> {
    JTextComponent editorComponent;
    T[] items;
    public AutoCompleteComboBox(T[] items) {
        super();
        this.items = items;
        this.setEditable(true);
        this.editorComponent = (JTextComponent) this.getEditor().getEditorComponent();
        this.editorComponent.setDocument(new AutoCompleteDocument());
    }

    private class AutoCompleteDocument extends PlainDocument {
        @Override
        public void remove(int offs, int len) throws BadLocationException {
            super.remove(offs, len);
//            this.filterItems(getText(0, getLength()));
        }

        @Override
        public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
            super.insertString(offs, str, a);

            String input = getText(0, getLength());

            Object selectedItem = getModel().getSelectedItem();
//            if (selectedItem == null || !startsWithIgnoreCase(selectedItem.toString(), input)) {
            this.filterItems(input);
//            }

            Object item = lookupItem(input);
            if (item != null) {
                getModel().setSelectedItem(item);
            } else {
                // keep old item selected if there is no match
                item = getSelectedItem();
                // imitate no insert (later on offs will be incremented by str.length(): selection won't move forward)
                offs = offs-str.length();
                // provide feedback to the user that his input has been received but can not be accepted
//                getToolkit().beep(); // when available use: UIManager.getLookAndFeel().provideErrorFeedback(comboBox);
                UIManager.getLookAndFeel().provideErrorFeedback(AutoCompleteComboBox.this);
            }
            super.remove(0, getLength());
            super.insertString(0, item.toString(), null);
            // select the completed part
            editorComponent.setCaretPosition(getLength());
            editorComponent.moveCaretPosition(offs + str.length());
        }
        private boolean startsWithIgnoreCase(String str1, String str2) {
            return str1.toUpperCase().startsWith(str2.toUpperCase());
        }
        private void filterItems(String pattern) {
            int prevSize = getModel().getSize();
            Arrays.stream(items)
                .filter(item -> startsWithIgnoreCase(item.toString(), pattern))
                .limit(5)
                .forEach(AutoCompleteComboBox.this::addItem);
            // Do it this way because if you add an item, and it's the only item (which happens if you remove all)
            // it sets the model to it, which calls insertString again
            int newSize = getModel().getSize() - prevSize;
            while (getModel().getSize() > newSize)
                removeItemAt(0);
        }
        private Object lookupItem(String pattern) {
            Object selectedItem = getModel().getSelectedItem();
            // only search for a different item if the currently selected does not match
            if (selectedItem != null && startsWithIgnoreCase(selectedItem.toString(), pattern)) {
                return selectedItem;
            } else {
                // iterate over all items
//                for (int i=0, n=getModel().getSize(); i < n; i++) {
//                    Object currentItem = getModel().getElementAt(i);
//                    // current item starts with the pattern?
//                    if (currentItem != null && startsWithIgnoreCase(currentItem.toString(), pattern)) {
//                        return currentItem;
//                    }
//                }
                if (getModel().getSize() > 0)
                    return getModel().getElementAt(0);
            }
            // no item starts with the pattern => return null
            return null;
        }
    }

}
