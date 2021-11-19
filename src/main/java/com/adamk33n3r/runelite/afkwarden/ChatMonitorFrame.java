package com.adamk33n3r.runelite.afkwarden;

import net.runelite.client.ui.ClientUI;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class ChatMonitorFrame extends JFrame {
    public ChatMonitorFrame() {
        this.setIconImage(ClientUI.ICON);

        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                close();
            }
        });
        this.setTitle("AFK Warden Chat Inspector");

        this.setLayout(new BorderLayout());

        JTable chatTable = new JTable(0, 2);
//        chatTable.addColumn(new TableColumn());
        ((DefaultTableModel)chatTable.getModel()).addRow(new Object[]{"hello, friend", "GAMEMESSAGE"});
        chatTable.setFillsViewportHeight(true);
        JScrollPane chatScrollPane = new JScrollPane(chatTable);
        this.add(chatScrollPane);
        this.pack();
    }
    public void open() {
        setVisible(true);
        toFront();
        repaint();
    }

    public void close() {
        setVisible(false);
    }
}
