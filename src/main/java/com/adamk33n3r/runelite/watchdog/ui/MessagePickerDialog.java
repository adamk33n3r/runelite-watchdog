package com.adamk33n3r.runelite.watchdog.ui;

import net.runelite.api.Client;
import net.runelite.api.IterableHashTable;
import net.runelite.api.MessageNode;
import net.runelite.client.util.Text;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class MessagePickerDialog extends JDialog {
    public MessagePickerDialog(Component parent, Client client, Consumer<String> callback) {
        this.setTitle("Pick Message");
        this.setSize(500, 250);
        this.setLocationRelativeTo(parent);
        this.setModal(true);
        this.setUndecorated(true);
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBorder(new EmptyBorder(10, 10, 10, 10));
        this.add(wrapper);
        wrapper.add(new JLabel("Pick a message"), BorderLayout.NORTH);

        IterableHashTable<MessageNode> messages = client.getMessages();
        List<String> messageArray = new ArrayList<>();
        messages.iterator().forEachRemaining(messageNode -> messageArray.add(Text.removeFormattingTags(messageNode.getValue())));
        JList<String> messageList = new JList<>(messageArray.subList(Math.max(0, messageArray.size()-20), messageArray.size()).toArray(new String[0]));
        messageList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        if (messageList.getModel().getSize() > 0) {
            JScrollPane scroll = new JScrollPane(messageList);
            wrapper.add(scroll, BorderLayout.CENTER);
            SwingUtilities.invokeLater(() -> {
                scroll.getVerticalScrollBar().setValue(scroll.getVerticalScrollBar().getMaximum());
            });
        } else {
            wrapper.add(new JLabel("No messages in history"));
        }

        JPanel btnGroup = new JPanel(new GridLayout(1, 0, 25, 0));
        JButton selectBtn = new JButton("Select");
        selectBtn.addActionListener((al) -> {
            callback.accept(messageList.getSelectedValue());
            this.setVisible(false);
        });
        btnGroup.add(selectBtn);
        JButton closeBtn = new JButton("Cancel");
        closeBtn.addActionListener(ev -> {
            this.setVisible(false);
        });
        btnGroup.add(closeBtn);
        wrapper.add(btnGroup, BorderLayout.SOUTH);
    }
}
