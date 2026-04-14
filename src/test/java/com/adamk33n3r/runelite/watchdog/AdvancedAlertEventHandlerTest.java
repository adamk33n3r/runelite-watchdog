package com.adamk33n3r.runelite.watchdog;

import com.adamk33n3r.nodegraph.nodes.TriggerNode;
import com.adamk33n3r.nodegraph.nodes.constants.PluginVar;
import com.adamk33n3r.runelite.watchdog.alerts.AdvancedAlert;
import com.adamk33n3r.runelite.watchdog.alerts.ChatAlert;
import com.adamk33n3r.runelite.watchdog.ui.panels.HistoryPanel;

import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.events.ChatMessage;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginManager;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import javax.inject.Provider;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class AdvancedAlertEventHandlerTest {

    @Mock Client client;
    @Mock ItemManager itemManager;
    @Mock AlertManager alertManager;
    @Mock EventBus eventBus;
    @Mock Provider<HistoryPanel> historyPanelProvider;
    @Mock WatchdogPlugin plugin;
    @Mock HistoryPanel historyPanel;
    @Mock PluginManager pluginManager;

    @InjectMocks
    EventHandler eventHandler;

    private AdvancedAlert advSpy;
    private TriggerNode triggerNode;
    private ChatAlert chatAlert;

    @Before
    public void setup() {
        Mockito.when(plugin.getName()).thenReturn("Watchdog");
        Mockito.when(historyPanelProvider.get()).thenReturn(historyPanel);

        chatAlert = new ChatAlert("chat test");
        chatAlert.setMessage("hello *");
        triggerNode = new TriggerNode(chatAlert);

        advSpy = Mockito.spy(new AdvancedAlert("event test"));
        advSpy.getGraph().add(triggerNode);

        // Return the spy AdvancedAlert for AdvancedAlert queries, empty for everything else
        Mockito.when(alertManager.getAllEnabledAlertsOfType(Mockito.any())).thenAnswer(inv -> {
            Class<?> clazz = inv.getArgument(0);
            if (clazz == AdvancedAlert.class) return Stream.of(advSpy);
            return Stream.empty();
        });
    }

    private ChatMessage mockGameMessage(String text) {
        ChatMessage msg = Mockito.mock(ChatMessage.class);
        Mockito.when(msg.getName()).thenReturn("SomeOtherPlayer");
        Mockito.when(msg.getType()).thenReturn(ChatMessageType.GAMEMESSAGE);
        Mockito.when(msg.getMessage()).thenReturn(text);
        return msg;
    }

    @Test
    public void chatMessage_matchingPattern_firesTriggerNode() {
        eventHandler.onChatMessage(mockGameMessage("hello world"));

        Mockito.verify(advSpy).fireTriggerNode(Mockito.eq(triggerNode), Mockito.any());
    }

    @Test
    public void chatMessage_nonMatchingPattern_doesNotFire() {
        eventHandler.onChatMessage(mockGameMessage("goodbye world"));

        Mockito.verify(advSpy, Mockito.never()).fireTriggerNode(Mockito.any(), Mockito.any());
    }

    @Test
    public void disabledAdvancedAlert_doesNotFire() {
        advSpy.setEnabled(false);
        eventHandler.onChatMessage(mockGameMessage("hello world"));

        Mockito.verify(advSpy, Mockito.never()).fireTriggerNode(Mockito.any(), Mockito.any());
    }

    @Test
    public void disabledTriggerNode_doesNotFire() {
        chatAlert.setEnabled(false);
        eventHandler.onChatMessage(mockGameMessage("hello world"));

        Mockito.verify(advSpy, Mockito.never()).fireTriggerNode(Mockito.any(), Mockito.any());
    }

    @Test
    public void captureGroups_passedCorrectlyToTriggerNode() {
        // {*} → (.*) in regex, so "hello {*}" captures everything after "hello "
        chatAlert.setMessage("hello {*}");
        ArgumentCaptor<String[]> captor = ArgumentCaptor.forClass(String[].class);

        eventHandler.onChatMessage(mockGameMessage("hello Adam"));

        Mockito.verify(advSpy).fireTriggerNode(Mockito.eq(triggerNode), captor.capture());
        assertArrayEquals(new String[]{"Adam"}, captor.getValue());
    }

    @Test
    public void multipleTriggers_eachFireOnMatchingMessage() {
        ChatAlert chatAlert2 = new ChatAlert("second");
        chatAlert2.setMessage("bye *");
        TriggerNode triggerNode2 = new TriggerNode(chatAlert2);
        advSpy.getGraph().add(triggerNode2);

        eventHandler.onChatMessage(mockGameMessage("hello world"));
        eventHandler.onChatMessage(mockGameMessage("bye world"));

        Mockito.verify(advSpy, Mockito.times(1))
            .fireTriggerNode(Mockito.eq(triggerNode), Mockito.any());
        Mockito.verify(advSpy, Mockito.times(1))
            .fireTriggerNode(Mockito.eq(triggerNode2), Mockito.any());
    }

    @Test
    public void sameMessage_doesNotFireWrongTrigger() {
        ChatAlert chatAlert2 = new ChatAlert("second");
        chatAlert2.setMessage("bye *");
        TriggerNode triggerNode2 = new TriggerNode(chatAlert2);
        advSpy.getGraph().add(triggerNode2);

        eventHandler.onChatMessage(mockGameMessage("hello world"));

        Mockito.verify(advSpy, Mockito.times(1))
            .fireTriggerNode(Mockito.eq(triggerNode), Mockito.any());
        Mockito.verify(advSpy, Mockito.never())
            .fireTriggerNode(Mockito.eq(triggerNode2), Mockito.any());
    }

    @Test
    public void initializePluginVars_enabledPlugin_setsTrue() {
        Plugin mockPlugin = Mockito.mock(Plugin.class);
        Mockito.when(mockPlugin.getName()).thenReturn("Bank");
        Mockito.when(pluginManager.getPlugins()).thenReturn(List.of(mockPlugin));
        Mockito.when(pluginManager.isPluginEnabled(mockPlugin)).thenReturn(true);

        PluginVar pv = new PluginVar();
        pv.setPluginName("Bank");
        advSpy.getGraph().add(pv);

        eventHandler.initializePluginVars();

        assertTrue(pv.getValueOut().getValue());
    }

    @Test
    public void initializePluginVars_disabledPlugin_setsFalse() {
        Plugin mockPlugin = Mockito.mock(Plugin.class);
        Mockito.when(mockPlugin.getName()).thenReturn("Bank");
        Mockito.when(pluginManager.getPlugins()).thenReturn(List.of(mockPlugin));
        Mockito.when(pluginManager.isPluginEnabled(mockPlugin)).thenReturn(false);

        PluginVar pv = new PluginVar();
        pv.setPluginName("Bank");
        pv.setValue(true); // start true to confirm it gets corrected to false
        advSpy.getGraph().add(pv);

        eventHandler.initializePluginVars();

        assertFalse(pv.getValueOut().getValue());
    }

    @Test
    public void initializePluginVars_nullPluginName_skipped() {
        PluginVar pv = new PluginVar(); // pluginName is null by default
        advSpy.getGraph().add(pv);

        eventHandler.initializePluginVars();

        Mockito.verify(pluginManager, Mockito.never()).isPluginEnabled(Mockito.any());
        Mockito.verify(pluginManager, Mockito.never()).getPlugins();
    }

    @Test
    public void initializePluginVars_unknownPluginName_notUpdated() {
        Plugin mockPlugin = Mockito.mock(Plugin.class);
        Mockito.when(mockPlugin.getName()).thenReturn("OtherPlugin");
        Mockito.when(pluginManager.getPlugins()).thenReturn(List.of(mockPlugin));

        PluginVar pv = new PluginVar();
        pv.setPluginName("NonExistentPlugin");
        advSpy.getGraph().add(pv);

        eventHandler.initializePluginVars();

        Mockito.verify(pluginManager, Mockito.never()).isPluginEnabled(Mockito.any());
        assertFalse(pv.getValueOut().getValue());
    }
}
