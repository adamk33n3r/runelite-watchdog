package com.adamk33n3r.runelite.watchdog.ui.nodegraph.connections;

import javax.swing.*;
import java.util.function.Consumer;

public interface ConnectedVariable<T> {
    T getValue();
    void setValue(T value);
    JComponent getComponent();
    void onChange(Consumer<T> onChange);
    void setEnabled(boolean enabled);
}
