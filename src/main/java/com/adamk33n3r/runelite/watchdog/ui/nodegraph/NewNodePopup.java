package com.adamk33n3r.runelite.watchdog.ui.nodegraph;

import com.adamk33n3r.runelite.watchdog.NotificationType;
import com.adamk33n3r.runelite.watchdog.TriggerType;
import net.runelite.client.util.Text;

import com.google.common.base.Splitter;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class NewNodePopup extends JPopupMenu {
    private final JList<Object> itemList;
    private final static Splitter SPLITTER = Splitter.on(' ').trimResults().omitEmptyStrings();
    private final JTextField search;
    private final CustomList.Items<?>[] items;

    public NewNodePopup(Consumer<Object> onSelect) {
        this.items = new CustomList.Items[]{
            new CustomList.Items<>("Alert", TriggerType.values(), System.out::println),
            new CustomList.Items<>("Notification", NotificationType.values(), System.out::println),
        };
        this.itemList = new CustomList(this.items);

        this.search = new JTextField();
        this.search.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                NewNodePopup.this.filter();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                NewNodePopup.this.filter();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                NewNodePopup.this.filter();
            }
        });
        this.add(this.search);
        this.itemList.addListSelectionListener((ls) -> {
            if (this.itemList.getSelectedValue() == null || this.itemList.getSelectedValue().toString().startsWith("c:")) {
                return;
            }
            onSelect.accept(this.itemList.getSelectedValue());
            this.setVisible(false);
            this.itemList.clearSelection();
            // Simulate mouse leave so that it isn't highlighting an entry when it's opened again
            this.itemList.dispatchEvent(new MouseEvent(this.itemList, MouseEvent.MOUSE_EXITED, System.currentTimeMillis(), 0, 0, 0, 0, false));
        });

        JScrollPane jScrollPane = new JScrollPane(this.itemList);
        jScrollPane.getVerticalScrollBar().setBlockIncrement(1);
        this.add(jScrollPane);
    }

    private void filter() {
        this.itemList.setListData(Arrays.stream(this.items).flatMap(i -> Stream.concat(Stream.of("c:"+i.getCategory()), Arrays.stream(i.getItems())))
            .filter(item -> {
                String option = item.toString();
                return option.startsWith("c:") || Text.matchesSearchTerms(SPLITTER.split(this.search.getText().toUpperCase()), Collections.singleton(option.toUpperCase()));
        }).toArray(Object[]::new));
    }
}
