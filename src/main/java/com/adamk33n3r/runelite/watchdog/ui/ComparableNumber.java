package com.adamk33n3r.runelite.watchdog.ui;

import com.adamk33n3r.runelite.watchdog.Displayable;
import com.adamk33n3r.runelite.watchdog.ui.panels.PanelUtils;

import net.runelite.client.ui.DynamicGridLayout;

import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import java.awt.Dimension;
import java.util.function.Consumer;

public class ComparableNumber extends JPanel {
    private final JComboBox<Comparator> comparator;
    private final JSpinner number;
    public ComparableNumber(
        int val, Consumer<Integer> onNumChange, int min, int max, int step,
        Comparator comparatorVal, Consumer<Comparator> onComparatorChange
    ) {
        this.comparator = PanelUtils.createSelect(Comparator.values(), comparatorVal, onComparatorChange);
        this.number = PanelUtils.createSpinner(val, min, max, step, onNumChange);

        this.setLayout(new DynamicGridLayout(1, 2, 5, 5));
        this.number.setPreferredSize(new Dimension(70, 0));
        this.add(this.comparator);
        this.add(this.number);
    }

    @Getter
    @AllArgsConstructor
    public enum Comparator implements Displayable {
        EQUALS("==", "Equals"),
        NOT_EQUALS("!=", "Not Equals"),
        LESS_THAN("<", "Less Than"),
        GREATER_THAN(">", "Greater Than"),
        LESS_THAN_OR_EQUALS("<=", "Less Than or Equals"),
        GREATER_THAN_OR_EQUALS(">=", "Greater Than or Equals"),
        ;

        private final String name;
        private final String tooltip;

        public boolean compare(int a, int b) {
            switch (this) {
                case EQUALS:
                    return a == b;
                case NOT_EQUALS:
                    return a != b;
                case LESS_THAN:
                    return a < b;
                case GREATER_THAN:
                    return a > b;
                case LESS_THAN_OR_EQUALS:
                    return a <= b;
                case GREATER_THAN_OR_EQUALS:
                    return a >= b;
                default:
                    return false;
            }
        }

        public Comparator converse() {
            switch (this) {
                case EQUALS:
                    return NOT_EQUALS;
                case NOT_EQUALS:
                    return EQUALS;
                case LESS_THAN:
                    return GREATER_THAN_OR_EQUALS;
                case GREATER_THAN:
                    return LESS_THAN_OR_EQUALS;
                case LESS_THAN_OR_EQUALS:
                    return GREATER_THAN;
                case GREATER_THAN_OR_EQUALS:
                    return LESS_THAN;
                default:
                    throw new IllegalArgumentException();
            }
        }
    }
}
