package com.adamk33n3r.runelite.watchdog.nodegraph;

import com.adamk33n3r.nodegraph.Graph;
import com.adamk33n3r.nodegraph.nodes.ActionNode;
import com.adamk33n3r.nodegraph.nodes.TriggerNode;
import com.adamk33n3r.nodegraph.nodes.flow.Branch;
import com.adamk33n3r.runelite.watchdog.alerts.ChatAlert;
import com.adamk33n3r.runelite.watchdog.notifications.Notification;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class BranchNodeTest {

    @Test
    public void branch_routesToTrue_whenConditionTrue() {
        Graph graph = new Graph();
        TriggerNode trigger = new TriggerNode(new ChatAlert("test"));
        Branch branch = new Branch();
        branch.getCondition().setValue(true);

        Notification trueNotif = Mockito.mock(Notification.class);
        Notification falseNotif = Mockito.mock(Notification.class);
        ActionNode trueAction = new ActionNode(trueNotif);
        ActionNode falseAction = new ActionNode(falseNotif);

        graph.add(trigger);
        graph.add(branch);
        graph.add(trueAction);
        graph.add(falseAction);

        graph.connect(trigger.getExec(), branch.getExec());
        graph.connect(branch.getExecTrue(), trueAction.getExec());
        graph.connect(branch.getExecFalse(), falseAction.getExec());

        graph.executeExecChain(trigger, new String[]{});

        Mockito.verify(trueNotif).fire(Mockito.any());
        Mockito.verify(falseNotif, Mockito.never()).fire(Mockito.any());
    }

    @Test
    public void branch_routesToFalse_whenConditionFalse() {
        Graph graph = new Graph();
        TriggerNode trigger = new TriggerNode(new ChatAlert("test"));
        Branch branch = new Branch();
        branch.getCondition().setValue(false);

        Notification trueNotif = Mockito.mock(Notification.class);
        Notification falseNotif = Mockito.mock(Notification.class);
        ActionNode trueAction = new ActionNode(trueNotif);
        ActionNode falseAction = new ActionNode(falseNotif);

        graph.add(trigger);
        graph.add(branch);
        graph.add(trueAction);
        graph.add(falseAction);

        graph.connect(trigger.getExec(), branch.getExec());
        graph.connect(branch.getExecTrue(), trueAction.getExec());
        graph.connect(branch.getExecFalse(), falseAction.getExec());

        graph.executeExecChain(trigger, new String[]{});

        Mockito.verify(trueNotif, Mockito.never()).fire(Mockito.any());
        Mockito.verify(falseNotif).fire(Mockito.any());
    }

    @Test
    public void branch_withNoCondition_defaultsFalse() {
        Graph graph = new Graph();
        TriggerNode trigger = new TriggerNode(new ChatAlert("test"));
        Branch branch = new Branch();
        // Default condition = false (not set)

        Notification trueNotif = Mockito.mock(Notification.class);
        Notification falseNotif = Mockito.mock(Notification.class);
        ActionNode trueAction = new ActionNode(trueNotif);
        ActionNode falseAction = new ActionNode(falseNotif);

        graph.add(trigger);
        graph.add(branch);
        graph.add(trueAction);
        graph.add(falseAction);

        graph.connect(trigger.getExec(), branch.getExec());
        graph.connect(branch.getExecTrue(), trueAction.getExec());
        graph.connect(branch.getExecFalse(), falseAction.getExec());

        graph.executeExecChain(trigger, new String[]{});

        Mockito.verify(trueNotif, Mockito.never()).fire(Mockito.any());
        Mockito.verify(falseNotif).fire(Mockito.any());
    }

    @Test
    public void branch_varsRegisteredCorrectly() {
        Branch branch = new Branch();
        assertTrue(branch.getInputs().containsKey("Exec"));
        assertTrue(branch.getInputs().containsKey("Condition"));
        assertTrue(branch.getOutputs().containsKey("True"));
        assertTrue(branch.getOutputs().containsKey("False"));
    }
}
