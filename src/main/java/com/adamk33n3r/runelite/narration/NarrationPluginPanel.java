package com.adamk33n3r.runelite.narration;

import com.adamk33n3r.runelite.narration.tts.TTSSegmentProcessor;
import com.adamk33n3r.runelite.narration.tts.TTSSynth;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.config.ConfigDescriptor;
import net.runelite.client.config.ConfigItemDescriptor;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.config.Range;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.DynamicGridLayout;
import net.runelite.client.ui.PluginPanel;
import net.runelite.client.ui.components.ComboBoxListRenderer;
import net.runelite.client.ui.components.PluginErrorPanel;
import org.apache.commons.lang3.StringUtils;

import javax.inject.Inject;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Optional;

@Slf4j
public class NarrationPluginPanel extends PluginPanel {
//    @Inject
//    private NarrationPlugin plugin;

    @Inject
    private TTSSynth synth;

    @Inject
    private TTSSegmentProcessor ttsSegmentProcessor;

    @Inject
    private NarrationConfig config;

    @Inject
    private ConfigManager configManager;

    @SuppressWarnings("unused")
    @Inject
    private void init() {
//        container.setLayout(new BorderLayout());

        final JPanel sliderContainer = new JPanel(new GridLayout(1, 3, 3, 3));
        sliderContainer.add(this.createSlider("volume", 1, 5));
        sliderContainer.add(this.createSlider("pitch", 25, 50));
        sliderContainer.add(this.createSlider("wpm", 50, 100));
        this.add(sliderContainer);

        final JButton button = new JButton("Test Voice");
        final JButton button2 = new JButton("Stop");
        final JButton button3 = new JButton(">>");
        button.addActionListener(ev -> {
            this.synth.stop();
            this.synth.sayAsync("The quick brown fox jumped over the lazy dog.");
        });
        button2.addActionListener(ev -> {
            this.synth.stop();
            this.ttsSegmentProcessor.clear();
        });
        final JPanel buttonContainer = new JPanel(new GridLayout(1, 0, 3, 3));
        buttonContainer.add(button);
        buttonContainer.add(button2);
        buttonContainer.add(button3);
        this.add(buttonContainer);

//        new PluginErrorPanel();

        DefaultListModel<String> listModel = new DefaultListModel<>();
        listModel.addElement("Test this thing");
        listModel.addElement("Test this other thing");
        listModel.addElement("Don't forget about this one");
        JList<String> a = new JList<>(listModel);
        a.setForeground(Color.WHITE);
        a.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        a.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // Double Click
                if (e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() == 2) {
                    Rectangle r = a.getCellBounds(0, a.getLastVisibleIndex());
                    if (r != null && r.contains(e.getPoint())) {
                        int idx = a.locationToIndex(e.getPoint());
                        synth.sayAsync(listModel.get(idx));
                    }
                }
            }
        });
        final JScrollPane scroll = new JScrollPane(a);
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
//        scroll.setPreferredSize(new Dimension(50, 50));

        this.add(scroll);
    }

    private JPanel createSlider(String configName, int minorTickSpacing, int majorTickSpacing) throws NoSuchElementException {
        ConfigDescriptor cd = this.configManager.getConfigDescriptor(this.config);
        ConfigItemDescriptor desc = cd.getItems().stream()
            .filter(cid -> cid.getItem().keyName().equals(configName))
            .findFirst()
            .orElseThrow();
        Range range = desc.getRange();
        int value = this.configManager.getConfiguration(cd.getGroup().value(), configName, int.class);
        JSlider slider = new JSlider(SwingConstants.VERTICAL, range.min(), range.max(), value);
        slider.setBackground(ColorScheme.LIGHT_GRAY_COLOR);
//        slider.setForeground(ColorScheme.GRAND_EXCHANGE_LIMIT);
        slider.setPaintTicks(true);
        slider.setMinorTickSpacing(minorTickSpacing);
        slider.setMajorTickSpacing(majorTickSpacing);
        slider.addChangeListener(ev -> this.configManager.setConfiguration(cd.getGroup().value(), configName, slider.getValue()));
        slider.setPaintLabels(true);
        slider.setSnapToTicks(true);
        JPanel sliderPanel = new JPanel(new BorderLayout());
        JLabel leftLabel = new JLabel(StringUtils.capitalize(configName));
        leftLabel.setForeground(Color.WHITE);
        leftLabel.setLabelFor(slider);
        sliderPanel.add(leftLabel, BorderLayout.NORTH);
        sliderPanel.add(slider, BorderLayout.CENTER);
        return sliderPanel;
    }
}
