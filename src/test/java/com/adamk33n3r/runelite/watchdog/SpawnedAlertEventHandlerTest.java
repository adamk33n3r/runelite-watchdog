package com.adamk33n3r.runelite.watchdog;

import com.adamk33n3r.nodegraph.nodes.TriggerNode;
import com.adamk33n3r.runelite.watchdog.alerts.AdvancedAlert;
import com.adamk33n3r.runelite.watchdog.alerts.SpawnedAlert;
import com.adamk33n3r.runelite.watchdog.alerts.SpawnedAlert.SpawnedDespawned;
import com.adamk33n3r.runelite.watchdog.alerts.SpawnedAlert.SpawnedType;

import net.runelite.api.GameState;
import net.runelite.api.Player;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameStateChanged;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.stream.Stream;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;

@RunWith(MockitoJUnitRunner.class)
public class SpawnedAlertEventHandlerTest extends AlertTestBase {

    @InjectMocks
    EventHandler eventHandler;

    private static final WorldPoint PLAYER_LOC = new WorldPoint(3200, 3200, 0);

    @Before
    public void setup() {
        Mockito.doNothing().when(this.watchdogPlugin).processAlert(any(), any(), anyBoolean());
        Player player = Mockito.mock(Player.class);
        Mockito.when(player.getWorldLocation()).thenReturn(PLAYER_LOC);
        Mockito.when(this.client.getLocalPlayer()).thenReturn(player);
        Mockito.when(this.watchdogConfig.batchSpawnedEvents()).thenReturn(true);
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
        // hasEnabledAlertsOfType(SpawnedAlert.class) returning true short-circuits the && so
        // the AdvancedAlert check is never reached — only stub what's actually consumed.
        Mockito.doReturn(true).when(this.alertManager).hasEnabledAlertsOfType(SpawnedAlert.class);
    }

    private void enqueueNpc(String name, int id) {
        this.eventHandler.onSpawned(name, id, PLAYER_LOC, SpawnedDespawned.SPAWNED, SpawnedType.NPC, a -> true);
    }

    private GameStateChanged mockStateChange(GameState state) {
        GameStateChanged event = Mockito.mock(GameStateChanged.class);
        Mockito.when(event.getGameState()).thenReturn(state);
        return event;
    }

    // endregion

    @Test
    public void singleSpawn_matchingAlert_firesAfterDrain() {
        SpawnedAlert alert = this.makeNpcAlert("Cow");
        this.mockAlertManager(alert);

        this.enqueueNpc("Cow", 1);
        this.eventHandler.drainSpawnQueue();

        Mockito.verify(this.watchdogPlugin).processAlert(eq(alert), any(), eq(false));
    }

    @Test
    public void singleSpawn_doesNotFireBeforeDrain() {
        SpawnedAlert alert = this.makeNpcAlert("Cow");
        this.mockAlertManager(alert);

        this.enqueueNpc("Cow", 1);

        Mockito.verify(this.watchdogPlugin, Mockito.never()).processAlert(any(), any(), anyBoolean());
    }

    @Test
    public void batchOfSpawns_zeroDebounce_firesOnce() {
        SpawnedAlert alert = this.makeNpcAlert("Cow");
        alert.setDebounceTime(0);
        this.mockAlertManager(alert);

        for (int i = 0; i < 30; i++) {
            this.enqueueNpc("Cow", i);
        }
        this.eventHandler.drainSpawnQueue();

        Mockito.verify(this.watchdogPlugin, Mockito.times(1)).processAlert(eq(alert), any(), eq(false));
    }

    @Test
    public void regexCapture_firstMatchWins() {
        SpawnedAlert alert = this.makeNpcAlert("Cow {*}");
        this.mockAlertManager(alert);

        this.enqueueNpc("Cow 1", 1);
        this.enqueueNpc("Cow 2", 2);
        this.eventHandler.drainSpawnQueue();

        ArgumentCaptor<String[]> captor = ArgumentCaptor.forClass(String[].class);
        Mockito.verify(this.watchdogPlugin, Mockito.times(1)).processAlert(eq(alert), captor.capture(), eq(false));
        assertArrayEquals(new String[]{"1"}, captor.getValue());
    }

    @Test
    public void differentTypes_filteredCorrectly() {
        SpawnedAlert alert = this.makeNpcAlert("Cow");
        this.mockAlertManager(alert);

        // ITEM type should not match NPC alert
        this.eventHandler.onSpawned("Cow", 1, PLAYER_LOC, SpawnedDespawned.SPAWNED, SpawnedType.ITEM, a -> true);
        // NPC type should match
        this.enqueueNpc("Cow", 1);
        this.eventHandler.drainSpawnQueue();

        Mockito.verify(this.watchdogPlugin, Mockito.times(1)).processAlert(eq(alert), any(), eq(false));
    }

    @Test
    public void distanceFilter_withinRange_fires() {
        SpawnedAlert alert = this.makeNpcAlert("*");
        alert.setDistance(5);
        this.mockAlertManager(alert);

        this.enqueueNpc("Goblin", 1);  // same tile as player → distance 0
        this.eventHandler.drainSpawnQueue();

        Mockito.verify(this.watchdogPlugin).processAlert(eq(alert), any(), eq(false));
    }

    @Test
    public void distanceFilter_outOfRange_doesNotFire() {
        SpawnedAlert alert = this.makeNpcAlert("*");
        alert.setDistance(5);
        this.mockAlertManager(alert);

        WorldPoint farLoc = new WorldPoint(3215, 3215, 0);  // ~21 tiles away
        this.eventHandler.onSpawned("Goblin", 1, farLoc, SpawnedDespawned.SPAWNED, SpawnedType.NPC, a -> true);
        this.eventHandler.drainSpawnQueue();

        Mockito.verify(this.watchdogPlugin, Mockito.never()).processAlert(any(), any(), anyBoolean());
    }

    @Test
    public void ownershipFilter_rejectedByPredicate_doesNotFire() {
        SpawnedAlert alert = this.makeNpcAlert("Gold ore");
        alert.setSpawnedType(SpawnedType.ITEM);
        this.mockAlertManager(alert);

        this.eventHandler.onSpawned("Gold ore", 444, PLAYER_LOC, SpawnedDespawned.SPAWNED, SpawnedType.ITEM, a -> false);
        this.eventHandler.drainSpawnQueue();

        Mockito.verify(this.watchdogPlugin, Mockito.never()).processAlert(any(), any(), anyBoolean());
    }

    @Test
    public void ownershipFilter_acceptedByPredicate_fires() {
        SpawnedAlert alert = this.makeNpcAlert("Gold ore");
        alert.setSpawnedType(SpawnedType.ITEM);
        this.mockAlertManager(alert);

        this.eventHandler.onSpawned("Gold ore", 444, PLAYER_LOC, SpawnedDespawned.SPAWNED, SpawnedType.ITEM, a -> true);
        this.eventHandler.drainSpawnQueue();

        Mockito.verify(this.watchdogPlugin).processAlert(eq(alert), any(), eq(false));
    }

    @Test
    public void noEnabledAlerts_fastPathSkipsEnqueue() {
        Mockito.doReturn(false).when(this.alertManager).hasEnabledAlertsOfType(any());

        // onSpawned fast-path fires → nothing enqueued → drain is a no-op
        this.eventHandler.onSpawned("Cow", 1, PLAYER_LOC, SpawnedDespawned.SPAWNED, SpawnedType.NPC, a -> true);
        this.eventHandler.drainSpawnQueue();

        Mockito.verify(this.watchdogPlugin, Mockito.never()).processAlert(any(), any(), anyBoolean());
    }

    @Test
    public void disabledAlert_notFiredEvenIfMatching() {
        SpawnedAlert alert = this.makeNpcAlert("Cow");
        alert.setEnabled(false);
        this.mockAlertManager(alert);

        this.enqueueNpc("Cow", 1);
        this.eventHandler.drainSpawnQueue();

        Mockito.verify(this.watchdogPlugin, Mockito.never()).processAlert(any(), any(), anyBoolean());
    }

    @Test
    public void advancedAlertTrigger_firesPerEvent() {
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

        this.enqueueNpc("Goblin", 1);
        this.enqueueNpc("Goblin", 2);
        this.enqueueNpc("Goblin", 3);
        this.eventHandler.drainSpawnQueue();

        Mockito.verify(advSpy, Mockito.times(3)).fireTriggerNode(eq(triggerNode), any());
    }

    @Test
    public void queueClearedOnLoginScreen() {
        SpawnedAlert alert = this.makeNpcAlert("Cow");
        this.mockAlertManager(alert);

        this.enqueueNpc("Cow", 1);
        this.eventHandler.onGameStateChanged(this.mockStateChange(GameState.LOGIN_SCREEN));
        this.eventHandler.drainSpawnQueue();

        Mockito.verify(this.watchdogPlugin, Mockito.never()).processAlert(any(), any(), anyBoolean());
    }

    @Test
    public void queueClearedOnHopping() {
        SpawnedAlert alert = this.makeNpcAlert("Cow");
        this.mockAlertManager(alert);

        this.enqueueNpc("Cow", 1);
        this.eventHandler.onGameStateChanged(this.mockStateChange(GameState.HOPPING));
        this.eventHandler.drainSpawnQueue();

        Mockito.verify(this.watchdogPlugin, Mockito.never()).processAlert(any(), any(), anyBoolean());
    }
}
