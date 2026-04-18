package com.adamk33n3r.runelite.watchdog.ui.alerts;

import com.adamk33n3r.runelite.watchdog.WatchdogPlugin;
import com.adamk33n3r.runelite.watchdog.alerts.AdvancedAlert;
import com.adamk33n3r.runelite.watchdog.ui.CollapsibleSection;
import com.adamk33n3r.runelite.watchdog.ui.nodegraph.GraphPanel;
import com.adamk33n3r.runelite.watchdog.ui.panels.AlertContentPanel;

import net.runelite.client.plugins.info.JRichTextPane;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class AdvancedAlertPanel extends AlertContentPanel<AdvancedAlert> {
    private JFrame graphEditorFrame;

    public AdvancedAlertPanel(AdvancedAlert alert, Runnable onChange) {
        super(alert, onChange);
        this.init();
    }

    @Override
    public void buildTypeContent() {
        this.addButton("Open Graph Editor", "Open the visual node graph editor for this alert", (btn, modifiers) -> {
            if (this.graphEditorFrame != null && this.graphEditorFrame.isDisplayable()) {
                this.graphEditorFrame.toFront();
                this.graphEditorFrame.requestFocus();
                return;
            }

            GraphPanel graphPanel = WatchdogPlugin.getInstance().getInjector().getInstance(GraphPanel.class);
            graphPanel.setOnChange(() -> WatchdogPlugin.getInstance().getAlertManager().saveAlerts());
            graphPanel.init(this.alert.getGraph());

            JScrollPane scrollPane = new JScrollPane(graphPanel);
            scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

            this.graphEditorFrame = new JFrame("Graph Editor - " + this.alert.getName());
            this.graphEditorFrame.setSize(new Dimension(1200, 800));
            this.graphEditorFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            this.graphEditorFrame.setLayout(new BorderLayout());
            this.graphEditorFrame.add(scrollPane, BorderLayout.CENTER);
            this.graphEditorFrame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    WatchdogPlugin.getInstance().getAlertManager().saveAlerts();
                }
            });
            this.graphEditorFrame.setLocationRelativeTo(null);
            this.graphEditorFrame.setVisible(true);
        });

        this.addRichTextPane("<html>The graph editor lets you build alert logic visually. "
            + "<b>Right-click</b> the canvas to add a node, then "
            + "<b>drag</b> from an output port to an input port to connect them. "
            + "Expand the sections below for more details.</html>");

        this.addSubPanel(new CollapsibleSection("Navigation & Input", helpPane(
            "<html>"
            + "<b>Left-click drag</b> on empty canvas &#8212; pan the view<br>"
            + "<b>Mouse wheel</b> &#8212; zoom in / out (zooming out enters an overview;"
            + " click any node to jump back to it)<br>"
            + "<b>Right-click</b> empty canvas &#8212; open the create-node menu (all categories)<br>"
            + "</html>"
        ), true));

        this.addSubPanel(new CollapsibleSection("Node Types", helpPane(
            "<html>"
            + "<b>Trigger</b> &#8212; fires the graph when a game event occurs (chat message, stat change,"
            + " XP drop, inventory, location, etc.). Every graph needs a Trigger.<br><br>"
            + "<b>Action</b> &#8212; the response to run (game message, screen flash, sound, TTS, overlay,"
            + " popup, plugin toggle, &#8230;)<br><br>"
            + "<b>Variable</b> &#8212; supplies a constant or live value (number, boolean, current location,"
            + " inventory, plugin state)<br><br>"
            + "<b>Logic</b> &#8212; comparisons and gates (AND/OR, ==, !=, &gt;, &lt;, location check,"
            + " inventory check)<br><br>"
            + "<b>Math</b> &#8212; arithmetic (add, subtract, multiply, divide, min, max, clamp)<br><br>"
            + "<b>Flow</b> &#8212; control flow (Delay by N ms, Branch on a boolean to run different paths)<br><br>"
            + "<b>Utility</b> &#8212; helpers (Notes, Display to inspect a value while debugging)"
            + "</html>"
        ), false));

        this.addSubPanel(new CollapsibleSection("Connecting Nodes", helpPane(
            "<html>"
            + "<b>Exec connections</b> (triangle ports, thick grey wire) control <i>when</i> something runs."
            + " Chain Trigger &#8594; Action &#8594; Action by wiring exec outputs to exec inputs.<br><br>"
            + "<b>Data connections</b> (round ports, colored wire) pass a <i>value</i> (number, boolean,"
            + " location, etc.). Drag from an output circle to an input circle of the same color.<br><br>"
            + "<b>Drop onto empty canvas</b> to open a filtered create-node menu showing only compatible"
            + " node types &#8212; the new node auto-connects to the best matching input.<br><br>"
            + "Dropping onto an occupied input <b>replaces</b> the existing connection."
            + " Dropping an existing connection onto itself <b>disconnects</b> it."
            + "</html>"
        ), false));

        this.addSubPanel(new CollapsibleSection("Data Types & Colors", helpPane(
            "<html>"
            + "<font color='#00B4C8'>&#9632;</font> <b>Cyan</b> &#8212; Number<br>"
            + "<font color='#3CB43C'>&#9632;</font> <b>Green</b> &#8212; Boolean (true / false)<br>"
            + "<font color='#C8AF00'>&#9632;</font> <b>Yellow</b> &#8212; Location<br>"
            + "<font color='#D2781E'>&#9632;</font> <b>Orange</b> &#8212; String (text)<br>"
            + "<font color='#0066CC'>&#9632;</font> <b>Blue</b> &#8212; Inventory data<br>"
            + "<font color='#969696'>&#9632;</font> <b>Grey</b> &#8212; Any type (accepts connections of every color)<br><br>"
            + "Exec ports appear as <b>triangles</b>; all data ports are <b>circles</b>.<br><br>"
            + "<b>Rule:</b> output and input colors must match &#8212; except grey inputs, which accept any color."
            + "</html>"
        ), false));

        this.addSubPanel(new CollapsibleSection("Tips", helpPane(
            "<html>"
            + "&#8226; Start with a <b>Trigger</b> node, then chain <b>Actions</b> via exec connections.<br>"
            + "&#8226; Add a <b>Display</b> (Utility) node to inspect a wire&#39;s value while debugging.<br>"
            + "&#8226; Use <b>Branch</b> (Flow) + a <b>Logic</b> node to run different actions conditionally.<br>"
            + "&#8226; Use <b>Delay</b> (Flow) to space out chained actions (e.g. flash, wait 500 ms, play sound).<br>"
            + "&#8226; <b>Note</b> nodes (Utility) are free-form labels &#8212; they don&#39;t affect execution."
            + "</html>"
        ), false));
    }

    private static JRichTextPane helpPane(String html) {
        JRichTextPane pane = new JRichTextPane();
        pane.setContentType("text/html");
        pane.setText(html);
        pane.setForeground(Color.WHITE);
        return pane;
    }

    @Override
    protected boolean includeNotifications() {
        return false;
    }

    @Override
    protected void onBack() {
        if (this.graphEditorFrame != null && this.graphEditorFrame.isDisplayable()) {
            this.graphEditorFrame.dispose();
        }
    }
}
