package com.adamk33n3r.runelite.watchdog.ui.panels;

import com.adamk33n3r.runelite.watchdog.AlertManager;
import com.adamk33n3r.runelite.watchdog.WatchdogPlugin;
import com.adamk33n3r.runelite.watchdog.alerts.Alert;
import com.adamk33n3r.runelite.watchdog.alerts.AlertGroup;
import com.adamk33n3r.runelite.watchdog.alerts.AlertMode;
import com.adamk33n3r.runelite.watchdog.alerts.RegexMatcher;
import com.adamk33n3r.runelite.watchdog.ui.FlatTextArea;
import com.adamk33n3r.runelite.watchdog.ui.PlaceholderTextField;
import com.adamk33n3r.runelite.watchdog.ui.StretchedStackedLayout;

import net.runelite.client.plugins.info.JRichTextPane;
import net.runelite.client.ui.ColorScheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Abstract base class for alert type-specific content panels.
 * <p>
 * Provides all the {@code add*()} fluent methods used to build the alert's controls.
 * Subclasses implement {@link #buildTypeContent()} for their type-specific UI.
 * <p>
 * Subclasses must set all their own fields and then call {@link #init()} as the
 * final statement in their constructor (after calling {@code super}).
 */
public abstract class AlertContentPanel<T extends Alert> extends JPanel {
    protected T alert;
    protected Runnable onChange;
    protected AlertManager alertManager;
    private Runnable onRebuild;

    protected AlertContentPanel(T alert, Runnable onChange) {
        this.alert = alert;
        this.onChange = onChange;
        this.alertManager = WatchdogPlugin.getInstance().getAlertManager();
        this.setLayout(new StretchedStackedLayout(3));
        this.setBorder(new EmptyBorder(0, 5, 0, 5));
    }

    /**
     * Must be called at the end of each subclass constructor, after all fields are set.
     * Invokes {@link #build()}.
     */
    protected void init() {
        this.build();
    }

    /**
     * Builds the full content: alert defaults followed by type-specific content.
     * Called from {@link #init()} and {@link #rebuild()}.
     */
    public void build() {
        this.addAlertDefaults();
        this.buildTypeContent();
    }

    /**
     * Subclasses implement this to add their type-specific controls to {@code this}.
     * Called by {@link #build()}. Do NOT call {@link #addAlertDefaults()} here.
     */
    public abstract void buildTypeContent();

    /**
     * Clears and rebuilds the content in-place. Fires the optional {@link #setOnRebuild(Runnable)}
     * callback after rebuilding.
     */
    public void rebuild() {
        this.removeAll();
        this.build();
        this.revalidate();
        this.repaint();
        if (this.onRebuild != null) {
            this.onRebuild.run();
        }
    }

    /**
     * Sets a callback invoked after every {@link #rebuild()} call.
     * Node panels set this to call {@code pack()} after a content rebuild.
     */
    public void setOnRebuild(Runnable onRebuild) {
        this.onRebuild = onRebuild;
    }

    /**
     * Returns {@code true} if the sidebar should add a {@link NotificationsPanel} below this content.
     * Override to return {@code false} for alert types that don't use the notification list
     * (e.g. AdvancedAlert, which uses the node graph instead).
     */
    protected boolean includeNotifications() {
        return true;
    }

    /**
     * Called when the sidebar's back button is pressed. Override for cleanup
     * (e.g. AdvancedAlert disposes its graph editor frame here).
     */
    protected void onBack() {}

    /**
     * Returns {@code true} if this content panel represents an {@link AlertGroup}.
     * The sidebar wrapper uses this to show an import button instead of a test button.
     */
    protected boolean isAlertGroup() {
        return false;
    }

    // ── Fluent add* methods ────────────────────────────────────────────────────

    public AlertContentPanel<T> addLabel(String label) {
        this.add(new JLabel(label));
        return this;
    }

    public AlertContentPanel<T> addRichTextPane(String text) {
        JRichTextPane richTextPane = new JRichTextPane();
        richTextPane.setContentType("text/html");
        richTextPane.setText(text);
        richTextPane.setForeground(Color.WHITE);
        this.add(richTextPane);
        return this;
    }

    public AlertContentPanel<T> addTextField(String placeholder, String tooltip, String initialValue, Consumer<String> saveAction) {
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
                alertManager.saveAlerts();
            }
        });
        this.add(textField);
        return this;
    }

    public AlertContentPanel<T> addTextArea(String placeholder, String tooltip, String initialValue, Consumer<String> saveAction) {
        FlatTextArea textArea = PanelUtils.createTextArea(placeholder, tooltip, initialValue, val -> {
            saveAction.accept(val);
            this.alertManager.saveAlerts();
        });
        this.add(textArea);
        return this;
    }

    public AlertContentPanel<T> addSpinner(String name, String tooltip, int initialValue, Consumer<Integer> saveAction) {
        return this.addSpinner(name, tooltip, initialValue, saveAction, 0, Integer.MAX_VALUE, 1);
    }

    public AlertContentPanel<T> addSpinner(String name, String tooltip, int initialValue, Consumer<Integer> saveAction, int min, int max, int step) {
        JSpinner spinner = PanelUtils.createSpinner(initialValue, min, max, step, val -> {
            saveAction.accept(val);
            this.alertManager.saveAlerts();
        });
        this.add(PanelUtils.createLabeledComponent(name, tooltip, spinner));
        return this;
    }

    public <E extends Enum<E>> AlertContentPanel<T> addSelect(String name, String tooltip, Class<E> enumType, E initialValue, Consumer<E> saveAction) {
        JComboBox<E> select = PanelUtils.createSelect(enumType.getEnumConstants(), initialValue, val -> {
            saveAction.accept(val);
            this.alertManager.saveAlerts();
        });
        this.add(PanelUtils.createLabeledComponent(name, tooltip, select));
        return this;
    }

    public AlertContentPanel<T> addCheckbox(String name, String tooltip, boolean initialValue, Consumer<Boolean> saveAction) {
        JCheckBox checkbox = PanelUtils.createCheckbox(name, tooltip, initialValue, val -> {
            saveAction.accept(val);
            this.alertManager.saveAlerts();
        });
        this.add(checkbox);
        return this;
    }

    public AlertContentPanel<T> addInputGroupWithSuffix(JComponent mainComponent, JComponent suffix) {
        return this.addInputGroup(mainComponent, null, Collections.singletonList(suffix));
    }

    public AlertContentPanel<T> addInputGroup(JComponent mainComponent, List<JComponent> prefixes, List<JComponent> suffixes) {
        InputGroup textFieldGroup = new InputGroup(mainComponent)
            .addPrefixes(prefixes)
            .addSuffixes(suffixes);
        this.add(textFieldGroup);
        return this;
    }

    public AlertContentPanel<T> addRegexMatcher(RegexMatcher regexMatcher, String placeholder, String tooltip) {
        return this.addRegexMatcher(regexMatcher, placeholder, tooltip, null);
    }

    public AlertContentPanel<T> addRegexMatcher(RegexMatcher regexMatcher, String placeholder, String tooltip, JComponent suffixAppend) {
        this.add(PanelUtils.createRegexMatcher(regexMatcher, placeholder, tooltip, suffixAppend));
        return this;
    }

    public AlertContentPanel<T> addRegexMatcher(
        Supplier<String> pattern,
        Consumer<String> savePattern,
        Supplier<Boolean> regexEnabled,
        Consumer<Boolean> saveRegexEnabled,
        String placeholder,
        String tooltip,
        JComponent suffixAppend
    ) {
        this.add(PanelUtils.createRegexMatcher(pattern, savePattern, regexEnabled, saveRegexEnabled, placeholder, tooltip, suffixAppend));
        return this;
    }

    public AlertContentPanel<T> addSubPanel(JPanel sub) {
        this.add(sub);
        return this;
    }

    public AlertContentPanel<T> addSubPanelControl(JPanel sub) {
        this.add(sub);
        return this;
    }

    public AlertContentPanel<T> addButton(String text, String tooltip, PanelUtils.ButtonClickListener clickListener) {
        JButton button = new JButton(text);
        button.setToolTipText(tooltip);
        button.addActionListener(ev -> clickListener.clickPerformed(button, ev.getModifiers()));
        this.add(button);
        return this;
    }

    public AlertContentPanel<T> addIf(Consumer<AlertContentPanel<T>> content, Supplier<Boolean> condition) {
        if (condition.get()) {
            content.accept(this);
        }
        return this;
    }

    /**
     * Adds the standard alert defaults: name field, alert mode select (skipped for AlertGroup),
     * and debounce spinner with reset checkbox.
     */
    public AlertContentPanel<T> addAlertDefaults() {
        JSpinner spinner = PanelUtils.createSpinner(
            this.alert.getDebounceTime(),
            0,
            8640000,
            100,
            val -> {
                this.alert.setDebounceTime(val);
                this.alertManager.saveAlerts();
            }
        );
        JCheckBox checkbox = PanelUtils.createCheckbox("Reset", "Reset the debounce time every time this alert is triggered", this.alert.isDebounceResetTime(), val -> {
            this.alert.setDebounceResetTime(val);
            this.alertManager.saveAlerts();
        });
        JPanel sub = new JPanel(new BorderLayout(5, 5));
        sub.add(spinner);
        sub.add(checkbox, BorderLayout.EAST);
        return this
            .addTextField("Enter the alert name...", "Name of Alert", this.alert.getName(), this.alert::setName)
            .addIf(panel -> panel.addSelect("Alert Mode", "How to handle re-triggering when this alert is already running",
                    AlertMode.class, this.alert.getAlertMode(), this.alert::setAlertMode),
                () -> !(this.alert instanceof AlertGroup))
            .addSubPanelControl(PanelUtils.createLabeledComponent(
                "Debounce (ms)",
                "How long to wait before allowing this alert to trigger again in milliseconds",
                sub
            ));
    }
}
