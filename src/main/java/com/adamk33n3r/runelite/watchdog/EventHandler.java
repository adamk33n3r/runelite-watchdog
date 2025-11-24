package com.adamk33n3r.runelite.watchdog;

import com.adamk33n3r.runelite.watchdog.alerts.*;
import com.adamk33n3r.runelite.watchdog.alerts.InventoryAlert.InventoryAlertType;
import com.adamk33n3r.runelite.watchdog.ui.panels.HistoryPanel;

import lombok.AllArgsConstructor;
import lombok.Builder;
import net.runelite.api.*;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.gameval.InventoryID;
import net.runelite.api.events.*;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.NotificationFired;
import net.runelite.client.events.PluginMessage;
import net.runelite.client.game.ItemManager;
import net.runelite.client.util.Text;

import lombok.extern.slf4j.Slf4j;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import javax.swing.SwingUtilities;
import java.awt.TrayIcon;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static com.adamk33n3r.runelite.watchdog.alerts.SpawnedAlert.SpawnedDespawned.DESPAWNED;
import static com.adamk33n3r.runelite.watchdog.alerts.SpawnedAlert.SpawnedDespawned.SPAWNED;
import static com.adamk33n3r.runelite.watchdog.alerts.SpawnedAlert.SpawnedType.*;

@Slf4j
@Singleton
public class EventHandler {
    @Inject
    private Client client;

    @Inject
    private ItemManager itemManager;

    @Inject
    private AlertManager alertManager;

    @Inject
    private EventBus eventBus;

    @Inject
    private Provider<HistoryPanel> historyPanelProvider;

    @Inject
    private WatchdogPlugin plugin;

    private final Map<Alert, Instant> lastTriggered = new HashMap<>();

    private final Map<Skill, Integer> previousSkillLevelTable = new EnumMap<>(Skill.class);
    private final Map<Skill, Integer> previousSkillXPTable = new EnumMap<>(Skill.class);
    private final Map<Integer, ItemComposition> itemCompositionCache = new ConcurrentHashMap<>();
    private Map<Integer, InventoryItemData> previousItemsTable = new ConcurrentHashMap<>();
    private WorldPoint previousLocation = null;

    private boolean firedByWatchdog = false;

    public synchronized void notify(String message) {
        this.firedByWatchdog = true;
        // The event bus is synchronous
        this.eventBus.post(new NotificationFired(null, message, TrayIcon.MessageType.NONE));
        this.firedByWatchdog = false;
    }

    //region Chat Message
    @Subscribe
    public void onChatMessage(ChatMessage chatMessage) {
        // Don't process messages sent by this plugin
        if (chatMessage.getName().equals(this.plugin.getName())) {
            return;
        }

        log.debug("{} | {}: {}", chatMessage.getType().name(), chatMessage.getName(), chatMessage.getMessage());
        String unformattedMessage = Text.removeFormattingTags(chatMessage.getMessage());

        // Send player messages to a different handler
        if (PlayerChatType.ANY.isOfType(chatMessage.getType())) {
            this.alertManager.getAllEnabledAlertsOfType(PlayerChatAlert.class)
                .filter(chatAlert -> chatAlert.getPlayerChatType() == PlayerChatType.ANY || chatAlert.getPlayerChatType().isOfType(chatMessage.getType()))
                .forEach(chatAlert -> {
                    var message = unformattedMessage;
                    if (chatAlert.isPrependSender()) {
                        var playerName = Text.sanitize(Text.removeFormattingTags(chatMessage.getName()));
                        message = String.format("%s: %s", playerName, message);
                    }
                    String[] groups = this.matchPattern(chatAlert, message);
                    if (groups == null) return;

                    this.fireAlert(chatAlert, groups);
                });
            return;
        }

        this.alertManager.getAllEnabledAlertsOfType(ChatAlert.class)
            .filter(chatAlert -> chatAlert.getGameMessageType() == GameMessageType.ANY || chatAlert.getGameMessageType().isOfType(chatMessage.getType()))
            .forEach(gameAlert -> {
                String[] groups = this.matchPattern(gameAlert, unformattedMessage);
                if (groups == null) return;

                this.fireAlert(gameAlert, groups);
            });
    }
    //endregion

    //region Notification
    @Subscribe
    public void onNotificationFired(NotificationFired notificationFired) {
        this.alertManager.getAllEnabledAlertsOfType(NotificationFiredAlert.class)
            .filter(notificationFiredAlert -> !this.firedByWatchdog || notificationFiredAlert.isAllowSelf())
            .forEach(notificationFiredAlert -> {
                String[] groups = this.matchPattern(notificationFiredAlert, notificationFired.getMessage());
                if (groups == null) return;

                this.fireAlert(notificationFiredAlert, groups);
            });
    }
    //endregion

    public void onPluginMessage(PluginMessage pluginMessage) {
        if (pluginMessage.getNamespace().equals("watchdog")) {
        }
    }

    //region Stat Changed
    @Subscribe
    private void onGameStateChanged(GameStateChanged gameStateChanged) {
        if (gameStateChanged.getGameState() == GameState.LOGIN_SCREEN) {
            this.previousSkillLevelTable.clear();
            this.previousSkillXPTable.clear();
            this.previousItemsTable.clear();
        }
    }

    @Subscribe
    public void onStatChanged(StatChanged statChanged) {
//        log.debug(String.format("%s: %s/%s", statChanged.getSkill().getName(), statChanged.getBoostedLevel(), statChanged.getLevel()));
        this.handleStatChanged(statChanged);
        this.handleXPDrop(statChanged);
    }

    private void handleStatChanged(StatChanged statChanged) {
        Integer previousLevel = this.previousSkillLevelTable.put(statChanged.getSkill(), statChanged.getBoostedLevel());
        if (previousLevel == null) {
            return;
        }

        this.alertManager.getAllEnabledAlertsOfType(StatChangedAlert.class)
            .filter(alert -> {
                boolean isSkill = alert.getSkill() == statChanged.getSkill();
                if (!isSkill) {
                    return false;
                }

                int targetLevel = alert.isRelative() ? statChanged.getLevel() + alert.getChangedAmount() : alert.getChangedAmount();
                boolean currentIs = alert.getChangedComparator().compare(statChanged.getBoostedLevel(), targetLevel);
                boolean prevWasNot = alert.getChangedComparator().converse().compare(previousLevel, targetLevel);
                return currentIs && prevWasNot;
            })
            .forEach(alert -> this.fireAlert(alert, statChanged.getSkill().getName()));
    }

    private void handleXPDrop(StatChanged statChanged) {
        Integer previousXP = this.previousSkillXPTable.put(statChanged.getSkill(), statChanged.getXp());
        if (previousXP == null) {
            return;
        }

        this.alertManager.getAllEnabledAlertsOfType(XPDropAlert.class)
            .filter(alert -> {
                boolean isSkill = alert.getSkill() == statChanged.getSkill();
                int gainedXP = statChanged.getXp() - previousXP;
                return isSkill && alert.getGainedComparator().compare(gainedXP, alert.getGainedAmount());
            })
            .forEach(alert -> this.fireAlert(alert, statChanged.getSkill().getName()));
    }
    //endregion

    //region Sound Effects
    @Subscribe
    private void onSoundEffectPlayed(SoundEffectPlayed soundEffectPlayed) {
        this.handleSoundEffectPlayed(soundEffectPlayed.getSoundId());
    }

    @Subscribe
    private void onAreaSoundEffectPlayed(AreaSoundEffectPlayed areaSoundEffectPlayed) {
        this.handleSoundEffectPlayed(areaSoundEffectPlayed.getSoundId());
    }

    private void handleSoundEffectPlayed(int soundID) {
        this.alertManager.getAllEnabledAlertsOfType(SoundFiredAlert.class)
            .filter(soundFiredAlert -> soundFiredAlert.getSoundID() == soundID)
            .forEach(alert -> this.fireAlert(alert, "" + soundID));
    }
    //endregion

    //region Inventory
    @Subscribe
    private void onItemContainerChanged(ItemContainerChanged itemContainerChanged) {
        // Ignore everything but inventory
        if (itemContainerChanged.getItemContainer().getId() != InventoryID.INV)
            return;
        Item[] items = itemContainerChanged.getItemContainer().getItems();
        Map<Integer, InventoryItemData> currentItems = new HashMap<>();
        Arrays.stream(items)
            .filter(item -> item.getId() > -1)
            .forEach(item -> {
                ItemComposition itemComposition = this.itemCompositionCache.computeIfAbsent(item.getId(), id -> this.itemManager.getItemComposition(id));
                InventoryItemData inventoryItemData = InventoryItemData.builder()
                    .itemComposition(itemComposition)
                    .quantity(item.getQuantity())
                    .build();
                currentItems.merge(item.getId(), inventoryItemData, (orig, other) -> InventoryItemData.builder()
                    .itemComposition(itemComposition)
                    .quantity(orig.quantity + other.quantity)
                    .build());
            });
        // Skip firing alerts if there are no previous items, since we just logged in. Even an empty inventory will have a map of -1 itemIds.
        if (!this.previousItemsTable.isEmpty()) {
            Map<Integer, InventoryItemData> itemMap = new HashMap<>(currentItems);
            this.previousItemsTable.forEach((itemID, data) -> itemMap.putIfAbsent(itemID, InventoryItemData.builder()
                .itemComposition(data.itemComposition)
                .build()));
            long itemCount = Arrays.stream(items).filter(item -> item.getId() > -1).count();
            this.alertManager.getAllEnabledAlertsOfType(InventoryAlert.class)
                .forEach(inventoryAlert -> {
                    InventoryAlertType alertType = inventoryAlert.getInventoryAlertType();
                    switch (alertType) {
                        case FULL:
                            if (itemCount == 28) this.fireAlert(inventoryAlert, alertType.getName());
                            break;
                        case EMPTY:
                            if (itemCount == 0) this.fireAlert(inventoryAlert, alertType.getName());
                            break;
                        case ITEM:
                        case ITEM_CHANGE:
                            Optional<MatchedItem> matchedItems = this.getMatchedItems(inventoryAlert, itemMap);
                            matchedItems.ifPresent((matched) -> {
                                int change = alertType == InventoryAlertType.ITEM ? 0 : matched.previousQuantity;
                                if (inventoryAlert.getQuantityComparator().compare(matched.currentQuantity - change, inventoryAlert.getItemQuantity())) {
                                    this.fireAlert(inventoryAlert, matchedItems.get().groups.toArray(new String[0]));
                                }
                            });
                            break;
                    }
                });
        }
        this.previousItemsTable = currentItems;
    }

    private Optional<MatchedItem> getMatchedItems(InventoryAlert inventoryAlert, Map<Integer, InventoryItemData> allItems) {
        return allItems.entrySet().stream()
            .filter(itemData -> inventoryAlert.getInventoryMatchType() == InventoryAlert.InventoryMatchType.BOTH
                || (inventoryAlert.getInventoryMatchType() == InventoryAlert.InventoryMatchType.NOTED && itemData.getValue().isNoted())
                || (inventoryAlert.getInventoryMatchType() == InventoryAlert.InventoryMatchType.UN_NOTED && !itemData.getValue().isNoted()))
            .map(itemData -> {
                String[] groups = this.matchPattern(inventoryAlert, itemData.getValue().itemComposition.getName());
                if (groups == null) return null;
                var prevItem = this.previousItemsTable.get(itemData.getKey());
                return new MatchedItem(
                    new ArrayList<>(List.of(groups)), // so that is mutable
                    prevItem == null ? 0 : prevItem.quantity,
                    itemData.getValue().quantity
                );
            })
            .filter(Objects::nonNull)
            .reduce((acc, b) -> {
                acc.groups = IntStream.range(0, Math.min(acc.groups.size(), b.groups.size()))
                    .mapToObj(i -> acc.groups.get(i) + ", " + b.groups.get(i))
                    .collect(Collectors.toList());
                acc.previousQuantity = acc.previousQuantity + b.previousQuantity;
                acc.currentQuantity = acc.currentQuantity + b.currentQuantity;
                return acc;
            });
    }
    //endregion

    //region Spawned
    @Subscribe
    private void onItemSpawned(ItemSpawned itemSpawned) {
        ItemComposition comp = this.itemManager.getItemComposition(itemSpawned.getItem().getId());
        this.onSpawned(comp.getName(), comp.getId(), itemSpawned.getTile().getWorldLocation(), SPAWNED, ITEM);
    }
    @Subscribe
    private void onItemDespawned(ItemDespawned itemDespawned) {
        ItemComposition comp = this.itemManager.getItemComposition(itemDespawned.getItem().getId());
        this.onSpawned(comp.getName(), comp.getId(), itemDespawned.getTile().getWorldLocation(), DESPAWNED, ITEM);
    }
    @Subscribe
    private void onNpcSpawned(NpcSpawned npcSpawned) {
        this.onActorSpawned(npcSpawned.getNpc(), npcSpawned.getNpc().getId(), NPC);
    }
    @Subscribe
    private void onNpcDespawned(NpcDespawned npcDespawned) {
        this.onActorDespawned(npcDespawned.getNpc(), npcDespawned.getNpc().getId(), NPC);
    }
    @Subscribe
    private void onPlayerSpawned(PlayerSpawned playerSpawned) {
        this.onActorSpawned(playerSpawned.getPlayer(), -1, PLAYER);
    }
    @Subscribe
    private void onPlayerDespawned(PlayerDespawned playerDespawned) {
        this.onActorDespawned(playerDespawned.getPlayer(), -1, PLAYER);
    }
    private void onActorSpawned(Actor actor, int id, SpawnedAlert.SpawnedType type) {
        this.onSpawned(actor.getName(), id, actor.getWorldLocation(), SPAWNED, type);
    }
    private void onActorDespawned(Actor actor, int id, SpawnedAlert.SpawnedType type) {
        this.onSpawned(actor.getName(), id, actor.getWorldLocation(), DESPAWNED, type);
    }

    @Subscribe
    private void onGroundObjectSpawned(GroundObjectSpawned groundObjectSpawned) {
        this.onTileObjectSpawned(groundObjectSpawned.getGroundObject(), SPAWNED, GROUND_OBJECT);
    }
    @Subscribe
    private void onGroundObjectDespawned(GroundObjectDespawned groundObjectDespawned) {
        this.onTileObjectSpawned(groundObjectDespawned.getGroundObject(), DESPAWNED, GROUND_OBJECT);
    }

    @Subscribe
    private void onDecorativeObjectSpawned(DecorativeObjectSpawned decorativeObjectSpawned) {
        this.onTileObjectSpawned(decorativeObjectSpawned.getDecorativeObject(), SPAWNED, DECORATIVE_OBJECT);
    }
    @Subscribe
    private void onDecorativeObjectDespawned(DecorativeObjectDespawned decorativeObjectDespawned) {
        this.onTileObjectSpawned(decorativeObjectDespawned.getDecorativeObject(), DESPAWNED, DECORATIVE_OBJECT);
    }

    @Subscribe
    private void onGameObjectSpawned(GameObjectSpawned gameObjectSpawned) {
        this.onTileObjectSpawned(gameObjectSpawned.getGameObject(), SPAWNED, GAME_OBJECT);
    }
    @Subscribe
    private void onGameObjectDespawned(GameObjectDespawned gameObjectDespawned) {
        this.onTileObjectSpawned(gameObjectDespawned.getGameObject(), DESPAWNED, GAME_OBJECT);
    }

    @Subscribe
    private void onWallObjectSpawned(WallObjectSpawned wallObjectSpawned) {
        this.onTileObjectSpawned(wallObjectSpawned.getWallObject(), SPAWNED, WALL_OBJECT);
    }
    @Subscribe
    private void onWallObjectDespawned(WallObjectDespawned wallObjectDespawned) {
        this.onTileObjectSpawned(wallObjectDespawned.getWallObject(), DESPAWNED, WALL_OBJECT);
    }

    private void onTileObjectSpawned(TileObject tileObject, SpawnedAlert.SpawnedDespawned mode, SpawnedAlert.SpawnedType type) {
        final ObjectComposition comp = this.client.getObjectDefinition(tileObject.getId());
        final ObjectComposition impostor = comp.getImpostorIds() != null ? comp.getImpostor() : comp;
        if (impostor == null) {
            return;
        }
        WorldPoint location = tileObject.getWorldLocation();
        if (tileObject instanceof GameObject) {
            WorldPoint playerLocation = this.client.getLocalPlayer().getWorldLocation();
            location = Util.getClosestTile(playerLocation, (GameObject) tileObject);
        }
        this.onSpawned(impostor.getName(), impostor.getId(), location, mode, type);
    }

    private void onSpawned(@Nullable String name, int id, WorldPoint location, SpawnedAlert.SpawnedDespawned mode, SpawnedAlert.SpawnedType type) {
        if (name == null) {
            return;
        }
        String unformattedName = Text.removeFormattingTags(name);
        int distanceToObject = location.distanceTo(this.client.getLocalPlayer().getWorldLocation());
        this.alertManager.getAllEnabledAlertsOfType(SpawnedAlert.class)
            .filter(spawnedAlert -> spawnedAlert.getSpawnedDespawned() == mode)
            .filter(spawnedAlert -> spawnedAlert.getSpawnedType() == type)
            .filter(spawnedAlert -> spawnedAlert.getDistance() == -1 || spawnedAlert.getDistanceComparator().compare(distanceToObject, spawnedAlert.getDistance()))
            .forEach(spawnedAlert -> {
                try {
                    int parsedID = Integer.parseInt(spawnedAlert.getPattern());
                    if (id == parsedID) {
                        this.fireAlert(spawnedAlert, new String[] { spawnedAlert.getPattern() });
                    }
                } catch (NumberFormatException ignored) {
                    String[] groups = this.matchPattern(spawnedAlert, unformattedName);
                    if (groups == null) return;

                    this.fireAlert(spawnedAlert, groups);
                }
            });
    }
    //endregion

    @Subscribe
    private void onOverheadTextChanged(OverheadTextChanged overheadTextChanged) {
        this.alertManager.getAllEnabledAlertsOfType(OverheadTextAlert.class)
            .filter(alert -> alert.getNpcName().isEmpty() || this.matchPattern(alert::getNpcName, alert::isNpcRegexEnabled, overheadTextChanged.getActor().getName()) != null)
            .forEach(alert -> {
                String[] groups = this.matchPattern(alert, overheadTextChanged.getOverheadText());
                if (groups == null) return;

                this.fireAlert(alert, groups);
            });
    }

    @Subscribe
    private void onGameTick(GameTick gameTick) {
        // Location alerts
        var world = this.client.getLocalPlayer().getWorldLocation();
        var worldView = this.client.getLocalPlayer().getWorldView();
        var localWorld = LocalPoint.fromWorld(worldView, world);
        // Should never be null
        if (localWorld == null) {
            return;
        }
        var worldLocation = WorldPoint.fromLocalInstance(this.client, localWorld);
//        log.debug("local: {} | world: {} | localWorld: {} | newWorld: {}", this.client.getLocalPlayer().getLocalLocation(), world, localWorld, worldLocation);
        this.alertManager.getAllEnabledAlertsOfType(LocationAlert.class)
            .filter(locationAlert -> locationAlert.shouldFire(worldLocation))
            .forEach(locationAlert -> {
                // If we're not repeating, don't fire if previous location is within the area
                if (!locationAlert.isRepeat() && locationAlert.shouldFire(this.previousLocation)) {
                    return;
                }
                this.fireAlert(locationAlert, new String[] { String.valueOf(worldLocation.getX()), String.valueOf(worldLocation.getY()) });
            });
        this.previousLocation = worldLocation;
    }

    private String[] matchPattern(
        Supplier<String> pattern,
        Supplier<Boolean> regexEnabled,
        String input
    ) {
        String regex = regexEnabled.get() ? pattern.get() : Util.createRegexFromGlob(pattern.get());
        Matcher matcher = Pattern.compile(regex, regexEnabled.get() ? 0 : Pattern.CASE_INSENSITIVE).matcher(input);
        if (!matcher.find()) return null;

        String[] groups = new String[matcher.groupCount()];
        for (int i = 0; i < matcher.groupCount(); i++) {
            groups[i] = matcher.group(i+1);
        }
        return groups;
    }

    private String[] matchPattern(RegexMatcher regexMatcher, String input) {
        return this.matchPattern(regexMatcher::getPattern, regexMatcher::isRegexEnabled, input);
    }

    private void fireAlert(Alert alert, String triggerValue) {
        this.fireAlert(alert, new String[] { triggerValue });
    }

    private void fireAlert(Alert alert, String[] triggerValues) {
        // Don't fire if it is disabled
        if (!alert.isEnabled()) return;

        List<AlertGroup> ancestors = alert.getAncestors();
        // Don't fire if any of the ancestors are disabled
        if (ancestors != null && !ancestors.stream().allMatch(Alert::isEnabled)) {
            return;
        }

        Alert alertToDebounceWith = ancestors == null ? alert : Stream.concat(ancestors.stream(), Stream.of(alert))
            .filter(ancestor -> ancestor.getDebounceTime() > 0)
            .max(Comparator.comparingInt(Alert::getDebounceTime))
            .orElse(alert);

        // If the alert hasn't been fired yet, or has been enough time, set the last trigger time to now and fire.
        if (!this.lastTriggered.containsKey(alertToDebounceWith) || Instant.now().compareTo(this.lastTriggered.get(alertToDebounceWith).plusMillis(alertToDebounceWith.getDebounceTime())) >= 0) {
            SwingUtilities.invokeLater(() -> {
                this.historyPanelProvider.get().addEntry(alert, triggerValues);
            });
            this.lastTriggered.put(alertToDebounceWith, Instant.now());
            this.plugin.processAlert(alert, triggerValues, false);
        }
    }

    @AllArgsConstructor
    private static class MatchedItem {
        private List<String> groups;
        private int previousQuantity;
        private int currentQuantity;
    }

    @Builder
    private static class InventoryItemData {
        private ItemComposition itemComposition;
        private int quantity;

        public boolean isNoted() {
            return itemComposition.getNote() != -1;
        }
    }
}
