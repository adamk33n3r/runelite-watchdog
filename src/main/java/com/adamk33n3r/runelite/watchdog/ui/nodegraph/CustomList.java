package com.adamk33n3r.runelite.watchdog.ui.nodegraph;

import com.adamk33n3r.runelite.watchdog.NotificationType;
import com.adamk33n3r.runelite.watchdog.TriggerType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.pushingpixels.substance.api.renderer.SubstanceDefaultListCellRenderer;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CustomList extends JList<Object> {
    public CustomList(Items<?>[] items) {
        super();
        Stream<Object> objectStream = Arrays.stream(items).flatMap(i -> Stream.concat(Stream.of("c:"+i.category), Arrays.stream(i.items)));
        this.setListData(objectStream.toArray());
        this.setCellRenderer(new SubstanceDefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                String str = value instanceof String ? (String) value : (value instanceof TriggerType ? ((TriggerType) value).getName() : (value instanceof NotificationType ? ((NotificationType) value).getName() : "UNKNOWN"));
                if (str.startsWith("c:")) {
                    this.setForeground(Color.LIGHT_GRAY);
                    this.setText(str.substring(2));
                    this.setEnabled(false);
//                    this.setFocusable(false);
//                    this.setOpaque(false);
                } else {
                    this.setText(str);
                }

                return this;
            }
        });

        this.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        this.setSelectionModel(new DefaultListSelectionModel() {
            @Override
            public void setSelectionInterval(int index0, int index1) {
                if (!CustomList.this.getModel().getElementAt(index0).toString().startsWith("c:")) {
                    super.setSelectionInterval(index0, index1);
                }
            }
        });
    }

    @Override
    public void setListData(Object[] listData) {
        this.setModel(new AbstractListModel<Object>() {
            public int getSize() { return listData.length; }
            public Object getElementAt(int i) { return listData[i]; }
        });
    }

    @Override
    public void setSelectionMode(int selectionMode) {
        super.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    @Getter
    @AllArgsConstructor
    static class Items<T> {
        private final String category;
        private final T[] items;
        private Consumer<T> onSelected;
    }
}
