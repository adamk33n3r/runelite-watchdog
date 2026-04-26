package com.adamk33n3r.runelite.watchdog;

import com.adamk33n3r.nodegraph.nodes.TriggerNode;
import com.adamk33n3r.runelite.watchdog.alerts.AdvancedAlert;
import com.adamk33n3r.runelite.watchdog.alerts.SpawnedAlert;
import com.adamk33n3r.runelite.watchdog.alerts.SpawnedAlert.SpawnedDespawned;
import com.adamk33n3r.runelite.watchdog.alerts.SpawnedAlert.SpawnedType;

import net.runelite.api.Player;
import net.runelite.api.coords.WorldPoint;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.*;

@RunWith(MockitoJUnitRunner.class)
public class SpawnedAlertLegacyPathTest extends AlertTestBase {

    @InjectMocks
    EventHandler eventHandler;

    private static final WorldPoint PLAYER_LOC = new WorldPoint(3200, 3200, 0);

    @Before
    public void setup() {
        Mockito.doNothing().when(this.watchdogPlugin).processAlert(any(), any(), anyBoolean());
        Player player = Mockito.mock(Player.class);
        Mockito.when(player.getWorldLocation()).thenReturn(PLAYER_LOC);
        Mockito.when(this.client.getLocalPlayer()).thenReturn(player);
        // batchSpawnedEvents() defaults to false on a Mockito mock — no stub needed
    }

    // region Helpers

    private SpawnedAlert makeNpcAlert(String pattern) {
        SpawnedAlert alert = new SpawnedAlert("test");
        alert.setPattern(pattern);
        alert.setSpawnedType(SpawnedType.NPC);
        alert.setSpawnedDespawned(SpawnedDespawned.SPAWNED);
        return alert;
    }

    private void mockAlertManager(SpawnedAlert alert) {
        Mockito.doAnswer(inv -> {
            Class<?> clazz = inv.getArgument(0);
            if (clazz == SpawnedAlert.class) return Stream.of(alert);
            return Stream.empty();
        }).when(this.alertManager).getAllEnabledAlertsOfType(any());
        Mockito.doReturn(true).when(this.alertManager).hasEnabledAlertsOfType(SpawnedAlert.class);
    }

    private void fireNpc(String name, int id) {
        this.eventHandler.onSpawned(name, id, PLAYER_LOC, SpawnedDespawned.SPAWNED, SpawnedType.NPC, EventHandler.ALWAYS);
    }

    // endregion

    @Test
    public void singleSpawn_firesImmediately() {
        SpawnedAlert alert = this.makeNpcAlert("Cow");
        this.mockAlertManager(alert);

        this.fireNpc("Cow", 1);

        Mockito.verify(this.watchdogPlugin).processAlert(eq(alert), any(), eq(false));
    }

    @Test
    public void batchOfSpawns_firesPerEvent() {
        SpawnedAlert alert = this.makeNpcAlert("Cow");
        alert.setDebounceTime(0);
        this.mockAlertManager(alert);

        for (int i = 0; i < 30; i++) {
            this.fireNpc("Cow", i);
        }

        Mockito.verify(this.watchdogPlugin, Mockito.times(30)).processAlert(eq(alert), any(), eq(false));
    }

    @Test
    public void batchOfSpawns_withDebounce_firesOnce() {
        SpawnedAlert alert = this.makeNpcAlert("Cow");
        alert.setDebounceTime(500);
        this.mockAlertManager(alert);

        for (int i = 0; i < 30; i++) {
            this.fireNpc("Cow", i);
        }

        Mockito.verify(this.watchdogPlugin, Mockito.times(1)).processAlert(eq(alert), any(), eq(false));
    }

    @Test
    public void noEnabledAlerts_fastPathSkips() {
        Mockito.doReturn(false).when(this.alertManager).hasEnabledAlertsOfType(any());

        this.fireNpc("Cow", 1);

        Mockito.verify(this.watchdogPlugin, Mockito.never()).processAlert(any(), any(), anyBoolean());
    }

    @Test
    public void disabledAlert_notFired() {
        SpawnedAlert alert = this.makeNpcAlert("Cow");
        alert.setEnabled(false);
        this.mockAlertManager(alert);

        this.fireNpc("Cow", 1);

        Mockito.verify(this.watchdogPlugin, Mockito.never()).processAlert(any(), any(), anyBoolean());
    }

    @Test
    public void advancedAlertTrigger_firesPerEvent_legacy() {
        SpawnedAlert innerAlert = this.makeNpcAlert("*");
        TriggerNode triggerNode = new TriggerNode(innerAlert);

        AdvancedAlert advSpy = Mockito.spy(new AdvancedAlert("adv test"));
        advSpy.getGraph().add(triggerNode);

        Mockito.doAnswer(inv -> {
            Class<?> clazz = inv.getArgument(0);
            if (clazz == AdvancedAlert.class) return Stream.of(advSpy);
            return Stream.empty();
        }).when(this.alertManager).getAllEnabledAlertsOfType(any());
        Mockito.doReturn(false).when(this.alertManager).hasEnabledAlertsOfType(SpawnedAlert.class);
        Mockito.doReturn(true).when(this.alertManager).hasEnabledAlertsOfType(AdvancedAlert.class);

        this.fireNpc("Goblin", 1);
        this.fireNpc("Goblin", 2);
        this.fireNpc("Goblin", 3);

        Mockito.verify(advSpy, Mockito.times(3)).fireTriggerNode(eq(triggerNode), any());
    }

    @Test
    public void distanceFilter_outOfRange_doesNotFire() {
        SpawnedAlert alert = this.makeNpcAlert("*");
        alert.setDistance(5);
        this.mockAlertManager(alert);

        WorldPoint farLoc = new WorldPoint(3215, 3215, 0);
        this.eventHandler.onSpawned("Goblin", 1, farLoc, SpawnedDespawned.SPAWNED, SpawnedType.NPC, EventHandler.ALWAYS);

        Mockito.verify(this.watchdogPlugin, Mockito.never()).processAlert(any(), any(), anyBoolean());
    }

    @Test
    public void distanceFilter_withinRange_fires() {
        SpawnedAlert alert = this.makeNpcAlert("*");
        alert.setDistance(5);
        this.mockAlertManager(alert);

        this.fireNpc("Goblin", 1);

        Mockito.verify(this.watchdogPlugin).processAlert(eq(alert), any(), eq(false));
    }
}
