package com.adamk33n3r.runelite.watchdog.ui.panels;

import com.adamk33n3r.runelite.watchdog.AlertManager;
import com.adamk33n3r.runelite.watchdog.Util;
import com.adamk33n3r.runelite.watchdog.WatchdogPlugin;
import com.adamk33n3r.runelite.watchdog.alerts.Alert;
import com.adamk33n3r.runelite.watchdog.notifications.Notification;
import com.adamk33n3r.runelite.watchdog.ui.HorizontalRuleBorder;
import com.adamk33n3r.runelite.watchdog.ui.ImportExportDialog;
import com.adamk33n3r.runelite.watchdog.ui.StretchedStackedLayout;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.plugins.info.JRichTextPane;
import net.runelite.client.ui.DynamicGridLayout;
import net.runelite.client.ui.MultiplexingPluginPanel;
import net.runelite.client.ui.PluginPanel;
import org.apache.commons.text.WordUtils;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@Slf4j
public class AlertPanel extends PluginPanel {
    private final ScrollablePanel container;
    private final MultiplexingPluginPanel muxer;
    private final Alert alert;
    private final JPanel wrapper;

    private List<Runnable> saveActions = new ArrayList<>();

    private AlertManager alertManager;

    private AlertPanel(MultiplexingPluginPanel muxer, Alert alert) {
        this.muxer = muxer;
        this.alert = alert;
        this.alertManager = WatchdogPlugin.getInstance().getAlertManager();

        this.setLayout(new BorderLayout());

        this.wrapper = new JPanel(new BorderLayout());
        this.container = new ScrollablePanel(new StretchedStackedLayout(3, 3));
        this.container.setScrollableWidth(ScrollablePanel.ScrollableSizeHint.FIT);
        this.container.setScrollableHeight(ScrollablePanel.ScrollableSizeHint.STRETCH);
        this.container.setScrollableBlockIncrement(ScrollablePanel.VERTICAL, ScrollablePanel.IncrementType.PERCENT, 10);
        CompoundBorder compoundBorder = BorderFactory.createCompoundBorder(new EtchedBorder(), new EmptyBorder(0, 5, 5, 5));
        this.container.setBorder(new TitledBorder(compoundBorder, Util.humanReadableClass(this.alert), TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, Color.WHITE));
        JScrollPane scroll = new JScrollPane(this.container, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        this.wrapper.add(scroll, BorderLayout.CENTER);

        // If developer mode
//        JButton rbldBtn = new JButton("Rebuild Panel");
//        rbldBtn.addActionListener(ev -> {
//            this.build()
//        });
//        this.add(rbldBtn, BorderLayout.NORTH);
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

    public AlertPanel addTextField(String name, String tooltip, String initialValue, Consumer<String> saveAction) {
        JTextField nameTextField = new JTextField(initialValue);
        this.saveActions.add(() -> saveAction.accept(nameTextField.getText()));
        this.container.add(PanelUtils.createLabeledComponent(name, tooltip, nameTextField));
        return this;
    }

    public AlertPanel addTextArea(String name, String tooltip, String initialValue, Consumer<String> saveAction) {
        JTextArea textArea = new JTextArea(initialValue);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setMargin(new Insets(4, 6, 5, 6));
        this.saveActions.add(() -> saveAction.accept(textArea.getText()));
        this.container.add(PanelUtils.createLabeledComponent(name, tooltip, textArea, true));
        return this;
    }

    public AlertPanel addSpinner(String name, String tooltip, int initialValue, Consumer<Integer> saveAction) {
        return this.addSpinner(name, tooltip, initialValue, saveAction, 1, 99, 1);
    }

    public AlertPanel addSpinner(String name, String tooltip, int initialValue, Consumer<Integer> saveAction, int min, int max, int step) {
        JSpinner spinner = new JSpinner(new SpinnerNumberModel(initialValue, min, max, step));
        this.saveActions.add(() -> saveAction.accept((Integer) spinner.getValue()));
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
        this.saveActions.add(() -> saveAction.accept(select.getItemAt(select.getSelectedIndex())));
        this.container.add(PanelUtils.createLabeledComponent(name, tooltip, select));
        return this;
    }

    public PluginPanel build() {
        NotificationsPanel notificationPanel = new NotificationsPanel(this.alert.getNotifications());
        WatchdogPlugin.getInstance().getInjector().injectMembers(notificationPanel);
//        notificationPanel.setBorder(new TitledBorder(new EtchedBorder(), "Notifications", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, Color.WHITE));
//        notificationPanel.setBorder(new EmptyBorder(50, 0, 0, 0));
        notificationPanel.setBorder(new HorizontalRuleBorder(10));
        this.container.add(notificationPanel);

        JPanel buttonPanel = new JPanel(new BorderLayout(3, 3));

        JButton exportAlertBtn = new JButton("Export Alert");
        exportAlertBtn.addActionListener(ev -> {
            ImportExportDialog importExportDialog = new ImportExportDialog(SwingUtilities.getWindowAncestor(this), this.alertManager.getGson().toJson(new Alert[] { alert }));
            importExportDialog.setVisible(true);
        });
        buttonPanel.add(exportAlertBtn, BorderLayout.NORTH);

        JPanel backSavePanel = new JPanel(new DynamicGridLayout(1, 2, 3, 3));
        buttonPanel.add(backSavePanel, BorderLayout.CENTER);

        JButton back = new JButton("Back");
        back.addActionListener(ev -> {
            // This and the code in onActivate are bandages because the notification panel components actually modify
            // the data directly so that it can test fire itself.
            this.alertManager.loadAlerts();
            this.muxer.popState();
        });
        backSavePanel.add(back);

        JButton save = new JButton("Save");
        save.addActionListener(ev -> {
            this.saveActions.forEach(Runnable::run);
            List<Notification> notificationList = notificationPanel.getNotifications();
            this.alert.getNotifications().clear();
            this.alert.getNotifications().addAll(notificationList);
            this.alertManager.saveAlerts();
            this.muxer.popState();
        });
        backSavePanel.add(save);

        this.wrapper.add(buttonPanel, BorderLayout.SOUTH);
        return this;
    }

    public AlertPanel addAlertDefaults(Alert alert) {
        return this.addTextField("Name", "Name of Alert", alert.getName(), alert::setName)
            .addSpinner(
                "Debounce Time (ms)",
                "How long to wait before allowing this alert to trigger again",
                alert.getDebounceTime(),
                alert::setDebounceTime,
                0,
                60000,
                100
            );
    }
}
