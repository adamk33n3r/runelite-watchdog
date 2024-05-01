package com.adamk33n3r.runelite.watchdog.ui.panels;

import com.adamk33n3r.runelite.watchdog.*;
import com.adamk33n3r.runelite.watchdog.alerts.Alert;
import com.adamk33n3r.runelite.watchdog.alerts.AlertGroup;
import com.adamk33n3r.runelite.watchdog.alerts.RegexMatcher;
import com.adamk33n3r.runelite.watchdog.ui.*;

import net.runelite.client.plugins.info.JRichTextPane;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.MultiplexingPluginPanel;
import net.runelite.client.ui.PluginPanel;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.text.WordUtils;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Slf4j
public abstract class AlertPanel<T extends Alert> extends PluginPanel {
    private final JPanel controlContainer;
    private final JPanel centerContainer;
    protected final WatchdogPanel watchdogPanel;
    protected final MultiplexingPluginPanel muxer;
    protected final T alert;

    private final AlertManager alertManager;

    public AlertPanel(WatchdogPanel watchdogPanel, T alert) {
        super(false);

        this.watchdogPanel = watchdogPanel;
        this.muxer = watchdogPanel.getMuxer();
        this.alert = alert;
        this.alertManager = WatchdogPlugin.getInstance().getAlertManager();

        this.setLayout(new BorderLayout());

        JPanel northPanel = new JPanel(new StretchedStackedLayout(3));
        this.add(northPanel, BorderLayout.NORTH);

        JPanel nameGroup = new JPanel(new BorderLayout());
        nameGroup.setBorder(new EmptyBorder(10, 5, 10, 5));

        TriggerType triggerType = this.alert.getType();
        JLabel nameLabel = new JLabel(triggerType.getName());
        nameLabel.setToolTipText(triggerType.getTooltip());
        nameLabel.setForeground(Color.WHITE);
        nameGroup.add(nameLabel, BorderLayout.CENTER);

        JPanel rightButtons = new JPanel(new GridLayout(1, 0));

        if (alert instanceof AlertGroup) {
            JButton importAlertBtn = PanelUtils.createActionButton(
                Icons.IMPORT,
                Icons.IMPORT_HOVER,
                "Import alert into this group",
                (btn, modifiers) -> {
                    ImportExportDialog importExportDialog = new ImportExportDialog(
                        SwingUtilities.getWindowAncestor(this),
                        (json, append) -> {
                            boolean result = this.alertManager.importAlerts(json, ((AlertGroup) alert).getAlerts(), append, true, WatchdogPlugin.getInstance().getConfig().overrideImportsWithDefaults());
                            // Delay for layout. Without this, it would sometimes make the search/actions narrower.
                            SwingUtilities.invokeLater(this::rebuild);
                            return result;
                        }
                    );
                    importExportDialog.setVisible(true);
                }
            );
            rightButtons.add(importAlertBtn);
        } else {
            JButton testAlert = PanelUtils.createActionButton(
                Icons.TEST,
                Icons.TEST_HOVER,
                "Test the whole alert",
                (btn, modifiers) -> {
                    String[] triggerValues = {"1", "2", "3", "4", "5"};
                    this.watchdogPanel.getHistoryPanelProvider().get().addEntry(alert, triggerValues);
                    new AlertProcessor(alert, triggerValues, true).start();
//                    alert.getNotifications().forEach(notification -> notification.fireForced(triggerValues));
                }
            );
            rightButtons.add(testAlert);
        }

        JButton exportAlertBtn = PanelUtils.createActionButton(
            Icons.EXPORT,
            Icons.EXPORT_HOVER,
            "Export this alert",
            (btn, modifiers) -> {
                ImportExportDialog importExportDialog = new ImportExportDialog(
                    SwingUtilities.getWindowAncestor(this),
                    alert
                );
                importExportDialog.setVisible(true);
            }
        );
        rightButtons.add(exportAlertBtn);

        ToggleButton toggleButton = new ToggleButton();
        toggleButton.setSelected(alert.isEnabled());
        toggleButton.addItemListener(i -> {
            alert.setEnabled(toggleButton.isSelected());
            this.alertManager.saveAlerts();
        });
        rightButtons.add(toggleButton);

        nameGroup.add(rightButtons, BorderLayout.EAST);

        JButton backButton = PanelUtils.createActionButton(
            Icons.BACK,
            Icons.BACK_HOVER,
            "Back",
            (btn, modifiers) -> {
                WatchdogPlugin.getInstance().getScreenMarkerUtil().finishCreation(true);
                this.alertManager.saveAlerts();
                this.muxer.popState();

                // Workaround for the onActivate rebuild issue
                // TODO remove if it ever gets fixed https://github.com/runelite/runelite/issues/17712
                int componentCount = this.muxer.getComponentCount();
                Component component = this.muxer.getComponent(componentCount - 1);
                if (component instanceof AlertPanel) {
                    ((AlertPanel<?>) component).rebuild();
                }
            }
        );
        backButton.setPreferredSize(new Dimension(22, 16));
        backButton.setBorder(new EmptyBorder(0, 0, 0, 5));
        nameGroup.add(backButton, BorderLayout.WEST);

        northPanel.add(nameGroup);

        this.controlContainer = new JPanel(new StretchedStackedLayout(3));
        this.controlContainer.setBorder(new EmptyBorder(0, 5, 0, 5));
        northPanel.add(this.controlContainer);

        this.centerContainer = new JPanel(new BorderLayout());
        this.add(this.centerContainer, BorderLayout.CENTER);
    }

    public AlertPanel<T> addLabel(String label) {
        JLabel labelComp = new JLabel(label);
        this.controlContainer.add(labelComp);
        return this;
    }

    public AlertPanel<T> addRichTextPane(String text) {
        JRichTextPane richTextPane = new JRichTextPane();
        richTextPane.setContentType("text/html");
        richTextPane.setText(text);
        richTextPane.setForeground(Color.WHITE);
        this.controlContainer.add(richTextPane);
        return this;
    }

    public AlertPanel<T> addTextField(String placeholder, String tooltip, String initialValue, Consumer<String> saveAction) {
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
        this.controlContainer.add(textField);
        return this;
    }

    public AlertPanel<T> addTextArea(String placeholder, String tooltip, String initialValue, Consumer<String> saveAction) {
        JTextArea textArea = PanelUtils.createTextArea(placeholder, tooltip, initialValue, val -> {
            saveAction.accept(val);
            this.alertManager.saveAlerts();
        });
        this.controlContainer.add(textArea);
        return this;
    }

    public AlertPanel<T> addSpinner(String name, String tooltip, int initialValue, Consumer<Integer> saveAction) {
        return this.addSpinner(name, tooltip, initialValue, saveAction, 0, Integer.MAX_VALUE, 1);
    }

    public AlertPanel<T> addSpinner(String name, String tooltip, int initialValue, Consumer<Integer> saveAction, int min, int max, int step) {
        JSpinner spinner = PanelUtils.createSpinner(initialValue, min, max, step, val -> {
            saveAction.accept(val);
            this.alertManager.saveAlerts();
        });
        this.controlContainer.add(PanelUtils.createLabeledComponent(name, tooltip, spinner));
        return this;
    }

    public <E extends Enum<E>> AlertPanel<T> addSelect(String name, String tooltip, Class<E> enumType, E initialValue, Consumer<E> saveAction) {
        JComboBox<E> select = new JComboBox<>(enumType.getEnumConstants());
        select.setSelectedItem(initialValue);
        select.setRenderer((list, value, index, isSelected, cellHasFocus) -> {
            if (value instanceof Displayable) {
                list.setToolTipText(((Displayable) value).getTooltip());
                return new DefaultListCellRenderer().getListCellRendererComponent(list, ((Displayable) value).getName(), index, isSelected, cellHasFocus);
            }
            String titleized = WordUtils.capitalizeFully(value.name());
            list.setToolTipText(titleized);
            return new DefaultListCellRenderer().getListCellRendererComponent(list, titleized, index, isSelected, cellHasFocus);
        });
        select.addActionListener(e -> {
            saveAction.accept(select.getItemAt(select.getSelectedIndex()));
            this.alertManager.saveAlerts();
        });
        this.controlContainer.add(PanelUtils.createLabeledComponent(name, tooltip, select));
        return this;
    }

    public AlertPanel<T> addCheckbox(String name, String tooltip, boolean initialValue, Consumer<Boolean> saveAction) {
        JCheckBox checkbox = PanelUtils.createCheckbox(name, tooltip, initialValue, val -> {
            saveAction.accept(val);
            this.alertManager.saveAlerts();
        });
        this.controlContainer.add(checkbox);
        return this;
    }

    public AlertPanel<T> addInputGroupWithSuffix(JComponent mainComponent, JComponent suffix) {
        return this.addInputGroup(mainComponent, null, Collections.singletonList(suffix));
    }

    public AlertPanel<T> addInputGroup(JComponent mainComponent, List<JComponent> prefixes, List<JComponent> suffixes) {
        InputGroup textFieldGroup = new InputGroup(mainComponent)
            .addPrefixes(prefixes)
            .addSuffixes(suffixes);
        this.controlContainer.add(textFieldGroup);
        return this;
    }

    public AlertPanel<T> addAlertDefaults() {
        return this.addTextField("Enter the alert name...", "Name of Alert", this.alert.getName(), this.alert::setName)
            .addSpinner(
                "Debounce Time (ms)",
                "How long to wait before allowing this alert to trigger again in milliseconds",
                this.alert.getDebounceTime(),
                this.alert::setDebounceTime,
                0,
                8640000, // 6 hours - max time a player can be logged in
                100
            );
    }

    public AlertPanel<T> addIf(Consumer<AlertPanel<T>> panel, Supplier<Boolean> ifFunc) {
        if (ifFunc.get()) {
            panel.accept(this);
        }
        return this;
    }

    public AlertPanel<T> addRegexMatcher(RegexMatcher regexMatcher, String placeholder, String tooltip) {
        return this.addRegexMatcher(regexMatcher, placeholder, tooltip, null);
    }

    public AlertPanel<T> addRegexMatcher(RegexMatcher regexMatcher, String placeholder, String tooltip, JComponent suffixAppend) {
        JPanel btnGroup = new JPanel(new GridLayout(1, 0, 5, 5));
        JButton regex = PanelUtils.createToggleActionButton(
            Icons.REGEX_SELECTED,
            Icons.REGEX_SELECTED_HOVER,
            Icons.REGEX,
            Icons.REGEX_HOVER,
            "Disable regex",
            "Enable regex",
            regexMatcher.isRegexEnabled(),
            (btn, modifiers) -> {
                regexMatcher.setRegexEnabled(btn.isSelected());
                this.alertManager.saveAlerts();
            }
        );
        btnGroup.add(regex);
        if (suffixAppend != null) {
            btnGroup.add(suffixAppend);
        }
        return this.addInputGroupWithSuffix(
            PanelUtils.createTextArea(placeholder, tooltip, regexMatcher.getPattern(), msg -> {
                if (!PanelUtils.isPatternValid(this, msg, regexMatcher.isRegexEnabled()))
                    return;
                regexMatcher.setPattern(msg);
                this.alertManager.saveAlerts();
            }),
            btnGroup
        );
    }

    public AlertPanel<T> addNotifications() {
        NotificationsPanel notificationPanel = new NotificationsPanel(this.alert);
        WatchdogPlugin.getInstance().getInjector().injectMembers(notificationPanel);
        notificationPanel.setBorder(new CompoundBorder(new EmptyBorder(0, 5, 0, 5), new HorizontalRuleBorder(10)));
        this.centerContainer.add(notificationPanel);

        return this;
    }

    public AlertPanel<T> addSubPanel(JPanel sub) {
        this.centerContainer.add(sub);
        return this;
    }

    public AlertPanel<T> addSubPanelControl(JPanel sub) {
        this.controlContainer.add(sub);
        return this;
    }

    public AlertPanel<T> addButton(String text, String tooltip, PanelUtils.ButtonClickListener clickListener) {
        JButton button = new JButton(text);
        button.setToolTipText(tooltip);
        button.addActionListener((ev) -> clickListener.clickPerformed(button, ev.getModifiers()));
        this.controlContainer.add(button);
        return this;
    }

    protected abstract void build();
    public void rebuild() {
        this.controlContainer.removeAll();
        this.centerContainer.removeAll();
        this.build();
    }

    @Override
    public void onActivate() {
        // Getting some weird resizing issues when this is called when switching tabs or collapsing the side panel
        // if there is lots of text in a text area? idk
        // Moved to the muxer.onAdd

        // this causes it to resize on collapse/restore
//        this.rebuild();
//        SwingUtilities.invokeLater(() -> {
//            // this causes it to resize on edit
//            this.rebuild();
//        });
    }
}
