package com.adamk33n3r.runelite.watchdog.ui.nodegraph.inputs;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

/** A no-op input widget that occupies zero visual space in a ConnectionLine. */
public class NullInput<T> extends AbstractInput<T> {
    private static final JPanel EMPTY = new JPanel();
    static { EMPTY.setPreferredSize(new Dimension(0, 0)); }

    @Override
    public T getValue() { return null; }

    @Override
    public void setValue(T value) { /* no-op */ }

    @Override
    public void registerOnChange(Consumer<T> onChange) { /* no-op */ }

    @Override
    protected JComponent getValueComponent() { return EMPTY; }
}
