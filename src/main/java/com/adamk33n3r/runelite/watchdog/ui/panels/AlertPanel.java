package com.adamk33n3r.runelite.watchdog.ui.panels;

import com.adamk33n3r.runelite.watchdog.AlertManager;
import com.adamk33n3r.runelite.watchdog.Displayable;
import com.adamk33n3r.runelite.watchdog.TriggerType;
import com.adamk33n3r.runelite.watchdog.WatchdogPanel;
import com.adamk33n3r.runelite.watchdog.WatchdogPlugin;
import com.adamk33n3r.runelite.watchdog.alerts.Alert;
import com.adamk33n3r.runelite.watchdog.alerts.AlertGroup;
import com.adamk33n3r.runelite.watchdog.alerts.RegexMatcher;
import com.adamk33n3r.runelite.watchdog.ui.HorizontalRuleBorder;
import com.adamk33n3r.runelite.watchdog.ui.ImportExportDialog;
import com.adamk33n3r.runelite.watchdog.ui.PlaceholderTextField;
import com.adamk33n3r.runelite.watchdog.ui.StretchedStackedLayout;
import com.adamk33n3r.runelite.watchdog.ui.ToggleButton;

import net.runelite.client.plugins.config.ConfigPlugin;
import net.runelite.client.plugins.info.JRichTextPane;
import net.runelite.client.ui.MultiplexingPluginPanel;
import net.runelite.client.ui.PluginPanel;
import net.runelite.client.util.ImageUtil;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.text.WordUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.image.BufferedImage;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static com.adamk33n3r.runelite.watchdog.WatchdogPanel.IMPORT_ICON;
import static com.adamk33n3r.runelite.watchdog.WatchdogPanel.EXPORT_ICON;
import static com.adamk33n3r.runelite.watchdog.ui.notifications.panels.NotificationPanel.TEST_ICON;
import static com.adamk33n3r.runelite.watchdog.ui.notifications.panels.NotificationPanel.TEST_ICON_HOVER;

@Slf4j
public abstract class AlertPanel<T extends Alert> extends PluginPanel {
    private final ScrollablePanel container;
    protected final WatchdogPanel watchdogPanel;
    protected final MultiplexingPluginPanel muxer;
    protected final T alert;
    private final JPanel wrapper;
    private final JScrollPane scroll;

    private final AlertManager alertManager;

    public static final ImageIcon BACK_ICON;
    public static final ImageIcon BACK_ICON_HOVER;
    public static final ImageIcon IMPORT_ICON_HOVER;
    public static final ImageIcon EXPORT_ICON_HOVER;
    public static final ImageIcon REGEX_ICON;
    public static final ImageIcon REGEX_ICON_HOVER;
    public static final ImageIcon REGEX_SELECTED_ICON;
    public static final ImageIcon REGEX_SELECTED_ICON_HOVER;

    static {
        final BufferedImage backIcon = ImageUtil.loadImageResource(ConfigPlugin.class, "config_back_icon.png");
        BACK_ICON = new ImageIcon(backIcon);
        BACK_ICON_HOVER = new ImageIcon(ImageUtil.alphaOffset(backIcon, -120));

        IMPORT_ICON_HOVER = new ImageIcon(ImageUtil.alphaOffset(IMPORT_ICON.getImage(), -120));
        EXPORT_ICON_HOVER = new ImageIcon(ImageUtil.alphaOffset(EXPORT_ICON.getImage(), -120));

        final BufferedImage regexIcon = ImageUtil.loadImageResource(AlertPanel.class, "regex_icon.png");
        final BufferedImage regexIconSelected = ImageUtil.loadImageResource(AlertPanel.class, "regex_icon_selected.png");
        REGEX_ICON = new ImageIcon(ImageUtil.luminanceOffset(regexIcon, -80));
        REGEX_ICON_HOVER = new ImageIcon(ImageUtil.luminanceOffset(regexIcon, -120));
        REGEX_SELECTED_ICON = new ImageIcon(regexIconSelected);
        REGEX_SELECTED_ICON_HOVER = new ImageIcon(ImageUtil.luminanceOffset(regexIconSelected, -80));
    }

    public AlertPanel(WatchdogPanel watchdogPanel, T alert) {
        super(false);

        this.watchdogPanel = watchdogPanel;
        this.muxer = watchdogPanel.getMuxer();
        this.alert = alert;
        this.alertManager = WatchdogPlugin.getInstance().getAlertManager();

        this.setLayout(new BorderLayout());

        this.wrapper = new JPanel(new BorderLayout());
        this.container = new ScrollablePanel(new StretchedStackedLayout(3, 3));
        this.container.setBorder(new EmptyBorder(0, 10, 0, 10));
        this.container.setScrollableWidth(ScrollablePanel.ScrollableSizeHint.FIT);
        this.container.setScrollableHeight(ScrollablePanel.ScrollableSizeHint.STRETCH);
        this.container.setScrollableBlockIncrement(ScrollablePanel.VERTICAL, ScrollablePanel.IncrementType.PERCENT, 10);
        this.scroll = new JScrollPane(this.container, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        this.wrapper.add(scroll, BorderLayout.CENTER);

        JPanel nameGroup = new JPanel(new BorderLayout());
        nameGroup.setBorder(new EmptyBorder(10, 10, 10, 10));

        TriggerType triggerType = this.alert.getType();
        JLabel nameLabel = new JLabel(triggerType.getName());
        nameLabel.setToolTipText(triggerType.getTooltip());
        nameLabel.setForeground(Color.WHITE);
        nameGroup.add(nameLabel, BorderLayout.CENTER);

        JPanel rightButtons = new JPanel(new GridLayout(1, 0));

        if (alert instanceof AlertGroup) {
            JButton importAlertBtn = PanelUtils.createActionButton(
                IMPORT_ICON,
                IMPORT_ICON_HOVER,
                "Import alert into this group",
                (btn, modifiers) -> {
                    ImportExportDialog importExportDialog = new ImportExportDialog(
                        SwingUtilities.getWindowAncestor(this),
                        (json, append) -> {
                            boolean result = WatchdogPlugin.getInstance().getAlertManager().importAlerts(json, ((AlertGroup) alert).getAlerts(), append, true);
                            this.rebuild();
                            return result;
                        }
                    );
                    importExportDialog.setVisible(true);
                }
            );
            rightButtons.add(importAlertBtn);
        } else {
            JButton testAlert = PanelUtils.createActionButton(
                TEST_ICON,
                TEST_ICON_HOVER,
                "Test the whole alert",
                (btn, modifiers) -> {
                    String[] triggerValues = {"1", "2", "3", "4", "5"};
                    WatchdogPlugin.getInstance().getPanel().getHistoryPanelProvider().get().addEntry(alert, triggerValues);
                    alert.getNotifications().forEach(notification -> notification.fireForced(triggerValues));
                }
            );
            rightButtons.add(testAlert);
        }

        JButton exportAlertBtn = PanelUtils.createActionButton(
            EXPORT_ICON,
            EXPORT_ICON_HOVER,
            "Export this alert",
            (btn, modifiers) -> {
                ImportExportDialog importExportDialog = new ImportExportDialog(
                    SwingUtilities.getWindowAncestor(this),
                    this.alertManager.getGson().toJson(new Alert[] { alert })
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
            BACK_ICON,
            BACK_ICON_HOVER,
            "Back",
            (btn, modifiers) -> {
                this.alertManager.saveAlerts();
                this.muxer.popState();
            }
        );
        backButton.setPreferredSize(new Dimension(22, 16));
        backButton.setBorder(new EmptyBorder(0, 0, 0, 5));
        nameGroup.add(backButton, BorderLayout.WEST);

        this.wrapper.add(nameGroup, BorderLayout.NORTH);

        this.add(wrapper, BorderLayout.CENTER);
    }

    public AlertPanel<T> addLabel(String label) {
        JLabel labelComp = new JLabel(label);
        this.container.add(labelComp);
        return this;
    }

    public AlertPanel<T> addRichTextPane(String text) {
        JRichTextPane richTextPane = new JRichTextPane();
        richTextPane.setContentType("text/html");
        richTextPane.setText(text);
        richTextPane.setForeground(Color.WHITE);
        this.container.add(richTextPane);
        return this;
    }

    public AlertPanel<T> addTextField(String placeholder, String tooltip, String initialValue, Consumer<String> saveAction) {
        PlaceholderTextField textField = new PlaceholderTextField(initialValue);
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
        this.container.add(textField);
        return this;
    }

    public AlertPanel<T> addTextArea(String placeholder, String tooltip, String initialValue, Consumer<String> saveAction) {
        JTextArea textArea = PanelUtils.createTextArea(placeholder, tooltip, initialValue, val -> {
            saveAction.accept(val);
            this.alertManager.saveAlerts();
        });
        this.container.add(textArea);
        return this;
    }

    public AlertPanel<T> addSpinner(String name, String tooltip, int initialValue, Consumer<Integer> saveAction) {
        return this.addSpinner(name, tooltip, initialValue, saveAction, -99, 99, 1);
    }

    public AlertPanel<T> addSpinner(String name, String tooltip, int initialValue, Consumer<Integer> saveAction, int min, int max, int step) {
        JSpinner spinner = PanelUtils.createSpinner(initialValue, min, max, step, val -> {
            saveAction.accept(val);
            this.alertManager.saveAlerts();
        });
        this.container.add(PanelUtils.createLabeledComponent(name, tooltip, spinner));
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
        this.container.add(PanelUtils.createLabeledComponent(name, tooltip, select));
        return this;
    }

    public AlertPanel<T> addCheckbox(String name, String tooltip, boolean initialValue, Consumer<Boolean> saveAction) {
        JCheckBox checkbox = PanelUtils.createCheckbox(name, tooltip, initialValue, val -> {
            saveAction.accept(val);
            this.alertManager.saveAlerts();
        });
        this.container.add(checkbox);
        return this;
    }

    public AlertPanel<T> addInputGroupWithSuffix(JComponent mainComponent, JComponent suffix) {
        return this.addInputGroup(mainComponent, null, Collections.singletonList(suffix));
    }

    public AlertPanel<T> addInputGroup(JComponent mainComponent, List<JComponent> prefixes, List<JComponent> suffixes) {
        InputGroup textFieldGroup = new InputGroup(mainComponent)
            .addPrefixes(prefixes)
            .addSuffixes(suffixes);
        this.container.add(textFieldGroup);
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
        return this.addInputGroupWithSuffix(
            PanelUtils.createTextArea(placeholder, tooltip, regexMatcher.getPattern(), msg -> {
                if (!PanelUtils.isPatternValid(this, msg, regexMatcher.isRegexEnabled()))
                    return;
                regexMatcher.setPattern(msg);
                this.alertManager.saveAlerts();
            }),
            PanelUtils.createToggleActionButton(
                REGEX_SELECTED_ICON,
                REGEX_SELECTED_ICON_HOVER,
                REGEX_ICON,
                REGEX_ICON_HOVER,
                "Disable regex",
                "Enable regex",
                regexMatcher.isRegexEnabled(),
                (btn, modifiers) -> {
                    regexMatcher.setRegexEnabled(btn.isSelected());
                    this.alertManager.saveAlerts();
                }
            )
        );
    }

    public AlertPanel<T> addNotifications() {
        NotificationsPanel notificationPanel = new NotificationsPanel(this.alert);
        WatchdogPlugin.getInstance().getInjector().injectMembers(notificationPanel);
        notificationPanel.setBorder(new HorizontalRuleBorder(10));
        this.container.add(notificationPanel);

        return this;
    }

    public AlertPanel<T> addSubPanel(JPanel sub) {
        this.container.add(sub);

        return this;
    }

    protected abstract void build();
    protected void rebuild() {
        this.container.removeAll();
        this.build();
        this.revalidate();
        this.repaint();
    }

    @Override
    public void onActivate() {
        this.rebuild();
    }
}
