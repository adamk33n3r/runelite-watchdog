package com.adamk33n3r.runelite.watchdog.ui.panels;

import com.adamk33n3r.runelite.watchdog.AlertManager;
import com.adamk33n3r.runelite.watchdog.Util;
import com.adamk33n3r.runelite.watchdog.WatchdogPlugin;
import com.adamk33n3r.runelite.watchdog.alerts.Alert;
import com.adamk33n3r.runelite.watchdog.notifications.Notification;
import com.adamk33n3r.runelite.watchdog.ui.*;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.plugins.config.ConfigPlugin;
import net.runelite.client.plugins.info.JRichTextPane;
import net.runelite.client.ui.DynamicGridLayout;
import net.runelite.client.ui.MultiplexingPluginPanel;
import net.runelite.client.ui.PluginPanel;
import net.runelite.client.util.ImageUtil;
import org.apache.commons.text.WordUtils;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.image.BufferedImage;
import java.util.function.Consumer;

import static com.adamk33n3r.runelite.watchdog.ui.notifications.panels.NotificationPanel.TEST_ICON;
import static com.adamk33n3r.runelite.watchdog.ui.notifications.panels.NotificationPanel.TEST_ICON_HOVER;

@Slf4j
public class AlertPanel extends PluginPanel {
    private final ScrollablePanel container;
    private final MultiplexingPluginPanel muxer;
    private final Alert alert;
    private final JPanel wrapper;
    private final JScrollPane scroll;

    private final AlertManager alertManager;

    static final ImageIcon BACK_ICON;
    static final ImageIcon BACK_ICON_HOVER;
    static final ImageIcon EXPORT_ICON;
    static final ImageIcon EXPORT_ICON_HOVER;

    static {
        final BufferedImage backIcon = ImageUtil.loadImageResource(ConfigPlugin.class, "config_back_icon.png");
        BACK_ICON = new ImageIcon(backIcon);
        BACK_ICON_HOVER = new ImageIcon(ImageUtil.alphaOffset(backIcon, -100));

        final BufferedImage exportIcon = ImageUtil.loadImageResource(AlertPanel.class, "export_icon.png");
        EXPORT_ICON = new ImageIcon(exportIcon);
        EXPORT_ICON_HOVER = new ImageIcon(ImageUtil.alphaOffset(exportIcon, -100));
    }

    private AlertPanel(MultiplexingPluginPanel muxer, Alert alert) {
        super(false);

        this.muxer = muxer;
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

        JLabel nameLabel = new JLabel(Util.humanReadableClass(this.alert));
        nameLabel.setForeground(Color.WHITE);
        nameGroup.add(nameLabel, BorderLayout.CENTER);

        JPanel rightButtons = new JPanel(new GridLayout(1, 0));

        JButton exportAlertBtn = PanelUtils.createActionButton(
            EXPORT_ICON,
            EXPORT_ICON_HOVER,
            "Export this alert",
            btn -> {
                ImportExportDialog importExportDialog = new ImportExportDialog(
                    SwingUtilities.getWindowAncestor(this),
                    this.alertManager.getGson().toJson(new Alert[] { alert })
                );
                importExportDialog.setVisible(true);
            }
        );
        rightButtons.add(exportAlertBtn);

        JButton testAlert = PanelUtils.createActionButton(
            TEST_ICON,
            TEST_ICON_HOVER,
            "Test the whole alert",
            btn -> alert.getNotifications().forEach(notification -> notification.fireForced(new String[]{ "1", "2", "3", "4", "5" }))
        );
        rightButtons.add(testAlert);

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
            btn -> this.muxer.popState()
        );
        backButton.setPreferredSize(new Dimension(22, 16));
        backButton.setBorder(new EmptyBorder(0, 0, 0, 5));
        nameGroup.add(backButton, BorderLayout.WEST);

        this.wrapper.add(nameGroup, BorderLayout.NORTH);

        this.add(wrapper, BorderLayout.CENTER);
    }

    public static AlertPanel create(MultiplexingPluginPanel muxer, Alert alert) {
        return new AlertPanel(muxer, alert);
    }

    public AlertPanel addLabel(String label) {
        JLabel labelComp = new JLabel(label);
        this.container.add(labelComp);
        return this;
    }

    public AlertPanel addRichTextPane(String text) {
        JRichTextPane richTextPane = new JRichTextPane();
        richTextPane.setContentType("text/html");
        richTextPane.setText(text);
        richTextPane.setForeground(Color.WHITE);
        this.container.add(richTextPane);
        return this;
    }

    public AlertPanel addTextField(String placeholder, String tooltip, String initialValue, Consumer<String> saveAction) {
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

    public AlertPanel addTextArea(String placeholder, String tooltip, String initialValue, Consumer<String> saveAction) {
        PlaceholderTextArea textArea = new PlaceholderTextArea(initialValue);
        textArea.setPlaceholder(placeholder);
        textArea.setToolTipText(tooltip);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setMargin(new Insets(4, 6, 5, 6));
        textArea.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                textArea.selectAll();
            }

            @Override
            public void focusLost(FocusEvent e) {
                saveAction.accept(textArea.getText());
                alertManager.saveAlerts();
            }
        });
        this.container.add(textArea);
        return this;
    }

    public AlertPanel addSpinner(String name, String tooltip, int initialValue, Consumer<Integer> saveAction) {
        return this.addSpinner(name, tooltip, initialValue, saveAction, 1, 99, 1);
    }

    public AlertPanel addSpinner(String name, String tooltip, int initialValue, Consumer<Integer> saveAction, int min, int max, int step) {
        JSpinner spinner = new JSpinner(new SpinnerNumberModel(initialValue, min, max, step));
        spinner.addChangeListener(e -> {
            saveAction.accept((Integer) spinner.getValue());
            this.alertManager.saveAlerts();
        });
        this.container.add(PanelUtils.createLabeledComponent(name, tooltip, spinner));
        return this;
    }

    public <T extends Enum<T>> AlertPanel addSelect(String name, String tooltip, Class<T> enumType, T initialValue, Consumer<T> saveAction) {
        JComboBox<T> select = new JComboBox<>(enumType.getEnumConstants());
        select.setSelectedItem(initialValue);
        select.setRenderer((list, value, index, isSelected, cellHasFocus) -> {
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

    public PluginPanel build() {
        NotificationsPanel notificationPanel = new NotificationsPanel(this.alert.getNotifications());
        WatchdogPlugin.getInstance().getInjector().injectMembers(notificationPanel);
        notificationPanel.setBorder(new HorizontalRuleBorder(10));
        this.container.add(notificationPanel);
        // I don't know why but sometimes the scroll pane is starting scrolled down 1 element, and we have to wait a tick to reset it
//        SwingUtilities.invokeLater(() -> {
//            this.scroll.getVerticalScrollBar();//.setValue(0);
//        });

        return this;
    }

    public AlertPanel addAlertDefaults(Alert alert) {
        return this.addTextField("Enter the alert name...", "Name of Alert", alert.getName(), alert::setName)
            .addSpinner(
                "Debounce Time (ms)",
                "How long to wait before allowing this alert to trigger again in milliseconds",
                alert.getDebounceTime(),
                alert::setDebounceTime,
                0,
                60000,
                100
            );
    }
}
