package com.adamk33n3r.runelite.watchdog;

import com.adamk33n3r.nodegraph.Graph;
import com.adamk33n3r.nodegraph.nodes.ActionNode;
import com.adamk33n3r.nodegraph.nodes.TriggerNode;
import com.adamk33n3r.nodegraph.nodes.utility.ToStringNode;
import com.adamk33n3r.runelite.watchdog.alerts.AdvancedAlert;
import com.adamk33n3r.runelite.watchdog.alerts.ChatAlert;
import com.adamk33n3r.runelite.watchdog.notifications.GameMessage;

import net.runelite.client.chat.QueuedMessage;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.MockitoJUnitRunner;

import javax.inject.Inject;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class GameMessageGraphOverrideTest extends TestBase {
    @Inject
    GameMessage gameMessage;

    @Test
    public void connectedMessage_firesOnce() throws InterruptedException {
        doReturn(false).when(this.watchdogPlugin).isInBannedArea();
        this.gameMessage.setMessage("CONFIGURED");

        CountDownLatch queued = new CountDownLatch(1);
        doAnswer(invocation -> {
            queued.countDown();
            return null;
        }).when(this.chatMessageManager).queue(any(QueuedMessage.class));

        AdvancedAlert adv = new AdvancedAlert("test");
        Graph graph = adv.getGraph();
        TriggerNode trigger = new TriggerNode(new ChatAlert("test"));
        ActionNode action = new ActionNode(this.gameMessage);
        ToStringNode toString = new ToStringNode();
        graph.add(trigger);
        graph.add(action);
        graph.add(toString);
        graph.connect(trigger.getExec(), action.getExec());
        graph.connect(toString.getResult(), action.getMessage());
        toString.getValue().setValue("DYNAMIC");

        adv.fireTriggerNode(trigger, new String[0]);

        assertTrue("timed out waiting for the game message to fire", queued.await(10, TimeUnit.SECONDS));
        ArgumentCaptor<QueuedMessage> captor = ArgumentCaptor.forClass(QueuedMessage.class);
        verify(this.chatMessageManager, times(1)).queue(captor.capture());
        assertTrue(captor.getValue().getRuneLiteFormattedMessage().endsWith("DYNAMIC"));
    }
}
