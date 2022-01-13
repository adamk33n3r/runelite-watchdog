package com.adamk33n3r.runelite.watchdog;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

public class LengthLimitFilter extends DocumentFilter {
    private final int limit;

    public LengthLimitFilter(int limit) {
        this.limit = limit;
    }

    @Override
    public void replace(FilterBypass filterBypass, int offset, int length, String text, AttributeSet attributeSet) throws BadLocationException {
        int currentLength = filterBypass.getDocument().getLength();
        int overLimit = (currentLength + text.length()) - this.limit - length;
        if (overLimit > 0) {
            text = text.substring(0, text.length() - overLimit);
        }

        super.replace(filterBypass, offset, length, text, attributeSet);
    }
}
