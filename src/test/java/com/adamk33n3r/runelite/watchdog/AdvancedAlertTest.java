package com.adamk33n3r.runelite.watchdog;

import com.adamk33n3r.nodegraph.nodes.ActionNode;
import com.adamk33n3r.nodegraph.nodes.TriggerNode;
import com.adamk33n3r.nodegraph.nodes.utility.NoteNode;
import com.adamk33n3r.runelite.watchdog.alerts.AdvancedAlert;
import com.adamk33n3r.runelite.watchdog.alerts.Alert;
import com.adamk33n3r.runelite.watchdog.alerts.ChatAlert;
import com.adamk33n3r.runelite.watchdog.notifications.ScreenFlash;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import javax.inject.Inject;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

@RunWith(MockitoJUnitRunner.class)
public class AdvancedAlertTest extends TestBase {

    @Inject
    AlertManager alertManager;

    @Test
    public void triggerType_hasAdvancedAlertEntry() {
        Assert.assertEquals("Advanced Alert", TriggerType.ADVANCED_ALERT.getName());
        Assert.assertEquals(AdvancedAlert.class, TriggerType.ADVANCED_ALERT.getImplClass());
    }

    @Test
    public void gson_roundTrip_preservesName() {
        AdvancedAlert alert = new AdvancedAlert("My Graph Alert");

        String json = alertManager.getGson().toJson(alert, Alert.class);
        Alert deserialized = alertManager.getGson().fromJson(json, Alert.class);

        Assert.assertTrue(deserialized instanceof AdvancedAlert);
        Assert.assertEquals("My Graph Alert", deserialized.getName());
    }

    @Test
    public void gson_roundTrip_preservesTriggerNode() {
        AdvancedAlert alert = new AdvancedAlert("Graph Alert");
        TriggerNode trigger = new TriggerNode(new ChatAlert("harvest"));
        alert.getGraph().add(trigger);

        String json = alertManager.getGson().toJson(alert, Alert.class);
        AdvancedAlert loaded = (AdvancedAlert) alertManager.getGson().fromJson(json, Alert.class);

        Assert.assertEquals(1, loaded.getGraph().getNodes().size());
        Assert.assertTrue(loaded.getGraph().getNodes().get(0) instanceof TriggerNode);
        TriggerNode loadedTrigger = (TriggerNode) loaded.getGraph().getNodes().get(0);
        Assert.assertTrue(loadedTrigger.getAlert() instanceof ChatAlert);
        Assert.assertEquals("harvest", loadedTrigger.getAlert().getName());
    }

    @Test
    public void gson_roundTrip_preservesConnection() {
        AdvancedAlert alert = new AdvancedAlert("Graph Alert");
        TriggerNode trigger = new TriggerNode(new ChatAlert("test"));
        ActionNode notifNode = new ActionNode(new ScreenFlash());
        alert.getGraph().add(trigger);
        alert.getGraph().add(notifNode);
        alert.getGraph().connect(trigger.getExec(), notifNode.getExec());

        String json = alertManager.getGson().toJson(alert, Alert.class);
        AdvancedAlert loaded = (AdvancedAlert) alertManager.getGson().fromJson(json, Alert.class);

        Assert.assertEquals(1, loaded.getGraph().getConnections().size());
    }

    @Test
    public void loadAlerts_handlesAdvancedAlertJson() {
        AdvancedAlert alert = new AdvancedAlert("Graph Test");
        alert.getGraph().add(new TriggerNode(new ChatAlert("harvest")));
        String json = "[" + alertManager.getGson().toJson(alert, Alert.class) + "]";

        Mockito.when(configManager.getConfiguration(WatchdogConfig.CONFIG_GROUP_NAME, WatchdogConfig.ALERTS))
            .thenReturn(json);
        Mockito.when(configManager.getConfiguration(WatchdogConfig.CONFIG_GROUP_NAME, WatchdogConfig.PLUGIN_VERSION))
            .thenReturn(pluginVersion);

        alertManager.loadAlerts();

        Assert.assertEquals(1, alertManager.getAlerts().size());
        Assert.assertTrue(alertManager.getAlerts().get(0) instanceof AdvancedAlert);
        AdvancedAlert loaded = (AdvancedAlert) alertManager.getAlerts().get(0);
        Assert.assertEquals("Graph Test", loaded.getName());
        Assert.assertEquals(1, loaded.getGraph().getNodes().size());
    }

    @Test
    public void loadAlerts_advancedAlertWithRegularAlerts() {
        AdvancedAlert adv = new AdvancedAlert("Advanced");
        ChatAlert chat = new ChatAlert("Regular");
        chat.setMessage("hello*");
        String advJson = alertManager.getGson().toJson(adv, Alert.class);
        String chatJson = alertManager.getGson().toJson(chat, Alert.class);
        String json = "[" + advJson + "," + chatJson + "]";

        Mockito.when(configManager.getConfiguration(WatchdogConfig.CONFIG_GROUP_NAME, WatchdogConfig.ALERTS))
            .thenReturn(json);
        Mockito.when(configManager.getConfiguration(WatchdogConfig.CONFIG_GROUP_NAME, WatchdogConfig.PLUGIN_VERSION))
            .thenReturn(pluginVersion);

        alertManager.loadAlerts();

        Assert.assertEquals(2, alertManager.getAlerts().size());
        Assert.assertTrue(alertManager.getAlerts().get(0) instanceof AdvancedAlert);
        Assert.assertTrue(alertManager.getAlerts().get(1) instanceof ChatAlert);
    }

    @Test
    public void newAdvancedAlert_hasExactlyOneWelcomeNote() {
        AdvancedAlert alert = this.alertManager.createAlert(AdvancedAlert.class);
        long noteCount = alert.getGraph().getNodesOfType(NoteNode.class).count();
        assertEquals(1, noteCount);
    }

    @Test
    public void welcomeNote_hasNonEmptyText() {
        AdvancedAlert alert = this.alertManager.createAlert(AdvancedAlert.class);
        NoteNode note = alert.getGraph().getNodesOfType(NoteNode.class).findFirst().orElseThrow();
        assertFalse(note.getNote().getValue().isEmpty());
    }

    @Test
    public void deserializedAdvancedAlert_doesNotDuplicateNote() {
        AdvancedAlert alert = this.alertManager.createAlert(AdvancedAlert.class);
        String json = this.alertManager.getGson().toJson(alert, AdvancedAlert.class);
        AdvancedAlert reloaded = this.alertManager.getGson().fromJson(json, AdvancedAlert.class);
        long noteCount = reloaded.getGraph().getNodesOfType(NoteNode.class).count();
        assertEquals(1, noteCount);
    }
}
