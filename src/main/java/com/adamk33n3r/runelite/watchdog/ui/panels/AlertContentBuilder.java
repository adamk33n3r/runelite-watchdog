package com.adamk33n3r.runelite.watchdog.ui.panels;

import com.adamk33n3r.runelite.watchdog.alerts.RegexMatcher;
import com.adamk33n3r.runelite.watchdog.ui.FlatTextArea;
import com.adamk33n3r.runelite.watchdog.ui.PlaceholderTextField;

import net.runelite.client.plugins.info.JRichTextPane;
import net.runelite.client.ui.ColorScheme;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Fluent builder that mirrors {@link AlertPanel}'s control-population API but operates on any
 * {@link JPanel} with caller-supplied {@code onChange} and {@code rebuild} runnables.
 * Used by {@link AlertPanelContentFactory} to populate node-panel contents without duplicating UI code.
 */
public class AlertContentBuilder {
    private final JPanel container;
    private final Runnable onChange;
    private final Runnable rebuild;

    public AlertContentBuilder(JPanel container, Runnable onChange, Runnable rebuild) {
        this.container = container;
        this.onChange = onChange;
        this.rebuild = rebuild;
    }

    public AlertContentBuilder addLabel(String label) {
        this.container.add(new JLabel(label));
        return this;
    }

    public AlertContentBuilder addRichTextPane(String text) {
        JRichTextPane richTextPane = new JRichTextPane();
        richTextPane.setContentType("text/html");
        richTextPane.setText(text);
        richTextPane.setForeground(Color.WHITE);
        this.container.add(richTextPane);
        return this;
    }

    public AlertContentBuilder addTextField(String placeholder, String tooltip, String initialValue, Consumer<String> saveAction) {
        PlaceholderTextField textField = new PlaceholderTextField(initialValue);
        textField.setSelectedTextColor(Color.WHITE);
        textField.setSelectionColor(ColorScheme.BRAND_ORANGE_TRANSPARENT);
        textField.setPlaceholder(placeholder);
        textField.setToolTipText(tooltip);
        textField.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                textField.selectAll();
            }

            @Override
            public void focusLost(FocusEvent e) {
                saveAction.accept(textField.getText());
                onChange.run();
            }
        });
        this.container.add(textField);
        return this;
    }

    public AlertContentBuilder addTextArea(String placeholder, String tooltip, String initialValue, Consumer<String> saveAction) {
        FlatTextArea textArea = PanelUtils.createTextArea(placeholder, tooltip, initialValue, val -> {
            saveAction.accept(val);
            this.onChange.run();
        });
        this.container.add(textArea);
        return this;
    }

    public AlertContentBuilder addSpinner(String name, String tooltip, int initialValue, Consumer<Integer> saveAction) {
        return this.addSpinner(name, tooltip, initialValue, saveAction, 0, Integer.MAX_VALUE, 1);
    }

    public AlertContentBuilder addSpinner(String name, String tooltip, int initialValue, Consumer<Integer> saveAction, int min, int max, int step) {
        JSpinner spinner = PanelUtils.createSpinner(initialValue, min, max, step, val -> {
            saveAction.accept(val);
            this.onChange.run();
        });
        this.container.add(PanelUtils.createLabeledComponent(name, tooltip, spinner));
        return this;
    }

    public <E extends Enum<E>> AlertContentBuilder addSelect(String name, String tooltip, Class<E> enumType, E initialValue, Consumer<E> saveAction) {
        JComboBox<E> select = PanelUtils.createSelect(enumType.getEnumConstants(), initialValue, val -> {
            saveAction.accept(val);
            this.onChange.run();
        });
        this.container.add(PanelUtils.createLabeledComponent(name, tooltip, select));
        return this;
    }

    public AlertContentBuilder addCheckbox(String name, String tooltip, boolean initialValue, Consumer<Boolean> saveAction) {
        JCheckBox checkbox = PanelUtils.createCheckbox(name, tooltip, initialValue, val -> {
            saveAction.accept(val);
            this.onChange.run();
        });
        this.container.add(checkbox);
        return this;
    }

    public AlertContentBuilder addInputGroupWithSuffix(JComponent mainComponent, JComponent suffix) {
        return this.addInputGroup(mainComponent, null, Collections.singletonList(suffix));
    }

    public AlertContentBuilder addInputGroup(JComponent mainComponent, List<JComponent> prefixes, List<JComponent> suffixes) {
        this.container.add(PanelUtils.createInputGroup(mainComponent, prefixes, suffixes));
        return this;
    }

    public AlertContentBuilder addIf(Consumer<AlertContentBuilder> content, Supplier<Boolean> condition) {
        if (condition.get()) {
            content.accept(this);
        }
        return this;
    }

    public AlertContentBuilder addRegexMatcher(RegexMatcher regexMatcher, String placeholder, String tooltip) {
        return this.addRegexMatcher(regexMatcher, placeholder, tooltip, null);
    }

    public AlertContentBuilder addRegexMatcher(RegexMatcher regexMatcher, String placeholder, String tooltip, JComponent suffixAppend) {
        this.container.add(PanelUtils.createRegexMatcher(regexMatcher, placeholder, tooltip, suffixAppend));
        return this;
    }

    public AlertContentBuilder addRegexMatcher(
        Supplier<String> pattern,
        Consumer<String> savePattern,
        Supplier<Boolean> regexEnabled,
        Consumer<Boolean> saveRegexEnabled,
        String placeholder,
        String tooltip,
        JComponent suffixAppend
    ) {
        this.container.add(PanelUtils.createRegexMatcher(pattern, savePattern, regexEnabled, saveRegexEnabled, placeholder, tooltip, suffixAppend));
        return this;
    }

    public AlertContentBuilder addSubPanelControl(JPanel sub) {
        this.container.add(sub);
        return this;
    }

    /** For parity with {@link AlertPanel#addSubPanel}: in the builder context there is no separate center container, so it also goes to the main container. */
    public AlertContentBuilder addSubPanel(JPanel sub) {
        this.container.add(sub);
        return this;
    }

    public AlertContentBuilder addButton(String text, String tooltip, PanelUtils.ButtonClickListener clickListener) {
        JButton button = new JButton(text);
        button.setToolTipText(tooltip);
        button.addActionListener(ev -> clickListener.clickPerformed(button, ev.getModifiers()));
        this.container.add(button);
        return this;
    }

    /** Triggers a full rebuild of the containing panel. Call this from select/button listeners that change the set of visible controls. */
    public void rebuild() {
        this.rebuild.run();
    }
}
