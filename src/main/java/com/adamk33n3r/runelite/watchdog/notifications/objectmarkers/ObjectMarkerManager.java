package com.adamk33n3r.runelite.watchdog.notifications.objectmarkers;

import com.adamk33n3r.runelite.watchdog.WatchdogConfig;
import com.google.common.base.Strings;
import com.google.gson.Gson;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.*;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.ui.components.colorpicker.ColorPickerManager;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.*;
import java.util.List;
import java.util.function.Predicate;

@Slf4j
@Singleton
public class ObjectMarkerManager {
    private static final String MARK = "Mark object";

    @Inject
    private Client client;
    @Inject
    private ClientThread clientThread;
    @Inject
    private ColorPickerManager colorPickerManager;
    @Inject
    private WatchdogConfig config;
    @Inject
    private Gson gson;
    @Inject
    private ConfigManager configManager;

    @Getter
    private boolean isInObjectMarkerMode = false;
    @Getter
    private ObjectMarker editingObjectMarker;
    @Getter
    private final List<ObjectMarkerData> objects = new ArrayList<>();
    private final List<TileObject> tileObjectsLoaded = new ArrayList<>();

    public void turnOnObjectMarkerMode(ObjectMarker objectMarker) {
        this.isInObjectMarkerMode = true;
        this.editingObjectMarker = objectMarker;
        if (objectMarker.getObjectPoint() == null) {
            objectMarker.setObjectPoint(new ObjectPoint());
        }
    }

    public void turnOffObjectMarkerMode() {
        this.hideObjectMarker(this.editingObjectMarker);
        this.isInObjectMarkerMode = false;
        this.editingObjectMarker = null;
    }

    public void showObjectMarker(ObjectMarker objectMarker) {
        this.clientThread.invokeLater(() -> {
            log.debug("showObjectMarker: {}", objectMarker);
            ObjectPoint objectPoint = objectMarker.getObjectPoint();
            if (objectPoint == null) {
                log.debug("ObjectMarker {} has no ObjectPoint; ignoring show.", objectMarker);
                return;
            }
            TileObject tileObject = this.tileObjectsLoaded.stream().filter(obj -> {
                // Not sure why this was happening, might be fixed by the onWorldViewUnloaded event
                if (this.client.getWorldView(obj.getLocalLocation().getWorldView()) == null) {
                    return false;
                }
                WorldPoint worldPoint = WorldPoint.fromLocalInstance(this.client, obj.getLocalLocation(), obj.getPlane());
                return objectIdEquals(obj, objectPoint.getId()) &&
                    worldPoint.getRegionX() == objectPoint.getRegionX() &&
                    worldPoint.getRegionY() == objectPoint.getRegionY() &&
                    worldPoint.getPlane() == objectPoint.getPlane();
            }).findFirst().orElse(null);
            if (tileObject == null) {
                return;
            }
            objectMarker.setTileObject(tileObject);
            objectMarker.setComposition(this.client.getObjectDefinition(objectPoint.getId()));
            this.objects.add(new ObjectMarkerData(objectMarker));
        });
    }

    public void hideObjectMarker(ObjectMarker objectMarker) {
        this.clientThread.invokeLater(() -> this.objects.removeIf(objData -> objData.getMarker() == objectMarker));
    }

    public void hideObjectMarkerById(String id) {
        this.clientThread.invokeLater(() -> this.objects.removeIf(objData -> Objects.equals(objData.getMarker().getId(), id)));
    }

    public void removeAllMarkers() {
        this.clientThread.invokeLater(this.objects::clear);
    }
    
    @Subscribe
    private void onMenuEntryAdded(MenuEntryAdded event) {
        if (!this.isInObjectMarkerMode) {
            return;
        }
        if (event.getType() != MenuAction.EXAMINE_OBJECT.getId()) {
            return;
        }

        int worldId = event.getMenuEntry().getWorldViewId();
        WorldView wv = client.getWorldView(worldId);
        if (wv == null) {
            return;
        }

        final TileObject tileObject = findTileObject(wv, event.getActionParam0(), event.getActionParam1(), event.getIdentifier());
        if (tileObject == null)
        {
            return;
        }

        client.getMenu().createMenuEntry(-1)
            .setOption(MARK)
            .setTarget(event.getTarget())
            .setWorldViewId(worldId)
            .setParam0(event.getActionParam0())
            .setParam1(event.getActionParam1())
            .setIdentifier(event.getIdentifier())
            .setType(MenuAction.RUNELITE_HIGH_PRIORITY)
            .onClick(this::markObject);
    }

    @Subscribe
    private void onWorldViewUnloaded(WorldViewUnloaded event) {
        WorldView wv = event.getWorldView();
        this.tileObjectsLoaded.removeIf(o -> o.getWorldView() == wv);
    }

    @Subscribe
    private void onWallObjectSpawned(WallObjectSpawned event) {
        this.tileObjectsLoaded.add(event.getWallObject());
    }

    @Subscribe
    private void onWallObjectDespawned(WallObjectDespawned event) {
        this.tileObjectsLoaded.remove(event.getWallObject());
    }

    @Subscribe
    private void onGameObjectSpawned(GameObjectSpawned event) {
        this.tileObjectsLoaded.add(event.getGameObject());
    }

    @Subscribe
    private void onGameObjectDespawned(GameObjectDespawned event) {
        this.tileObjectsLoaded.remove(event.getGameObject());
    }

    @Subscribe
    private void onDecorativeObjectSpawned(DecorativeObjectSpawned event) {
        this.tileObjectsLoaded.add(event.getDecorativeObject());
    }

    @Subscribe
    private void onDecorativeObjectDespawned(DecorativeObjectDespawned event) {
        this.tileObjectsLoaded.remove(event.getDecorativeObject());
    }

    @Subscribe
    private void onGroundObjectSpawned(GroundObjectSpawned event) {
        this.tileObjectsLoaded.add(event.getGroundObject());
    }

    @Subscribe
    private void onGroundObjectDespawned(GroundObjectDespawned event) {
        this.tileObjectsLoaded.remove(event.getGroundObject());
    }

    private void markObject(MenuEntry entry)
    {
        WorldView wv = client.getWorldView(entry.getWorldViewId());
        if (wv == null) {
            return;
        }

        TileObject object = findTileObject(wv, entry.getParam0(), entry.getParam1(), entry.getIdentifier());
        if (object == null)
        {
            return;
        }
        log.debug("markObject: {} {} {} {} {}", object.getX(), object.getY(), object.getPlane(), object.getId(), object.getWorldView().getId());

        // object.getId() is always the base object id, getObjectComposition transforms it to
        // the correct object we see
        ObjectComposition objectDefinition = getObjectComposition(object.getId());
        String name = objectDefinition.getName();
        // Name is probably never "null" - however prevent adding it if it is, as it will
        // become ambiguous as objects with no name are assigned name "null"
        if (Strings.isNullOrEmpty(name) || name.equals("null"))
        {
            return;
        }

        markObject(objectDefinition, name, object);
    }

    private TileObject findTileObject(WorldView wv, int x, int y, int id)
    {
        int level = wv.getPlane();
        Scene scene = wv.getScene();
        Tile[][][] tiles = scene.getTiles();
        final Tile tile = tiles[level][x][y];
        if (tile == null) {
            return null;
        }

        final GameObject[] tileGameObjects = tile.getGameObjects();
        final DecorativeObject tileDecorativeObject = tile.getDecorativeObject();
        final WallObject tileWallObject = tile.getWallObject();
        final GroundObject groundObject = tile.getGroundObject();

        if (objectIdEquals(tileWallObject, id))
        {
            return tileWallObject;
        }

        if (objectIdEquals(tileDecorativeObject, id))
        {
            return tileDecorativeObject;
        }

        if (objectIdEquals(groundObject, id))
        {
            return groundObject;
        }

        for (GameObject object : tileGameObjects)
        {
            if (objectIdEquals(object, id))
            {
                return object;
            }
        }

        return null;
    }

    private boolean objectIdEquals(TileObject tileObject, int id)
    {
        if (tileObject == null)
        {
            return false;
        }

        if (tileObject.getId() == id)
        {
            return true;
        }

        // Menu action EXAMINE_OBJECT sends the transformed object id, not the base id, unlike
        // all of the GAME_OBJECT_OPTION actions, so check the id against the impostor ids
        final ObjectComposition comp = client.getObjectDefinition(tileObject.getId());

        if (comp.getImpostorIds() != null)
        {
            for (int impostorId : comp.getImpostorIds())
            {
                if (impostorId == id)
                {
                    return true;
                }
            }
        }

        return false;
    }

    /** mark or unmark an object
     *
     * @param objectComposition transformed composition of object based on vars
     * @param name name of objectComposition
     * @param tileObject tile object, for multilocs object.getId() is the base id
     */
    private void markObject(ObjectComposition objectComposition, String name, final TileObject tileObject)
    {
        final WorldPoint worldPoint = WorldPoint.fromLocalInstance(client, tileObject.getLocalLocation());
        final int regionId = worldPoint.getRegionID();
        this.editingObjectMarker.setObjectPoint(new ObjectPoint(
            tileObject.getId(),
            name,
            regionId,
            worldPoint.getRegionX(),
            worldPoint.getRegionY(),
            worldPoint.getPlane()));

        objects.removeIf(o -> o.getMarker().getTileObject() == tileObject);
        this.editingObjectMarker.setTileObject(tileObject);
        this.editingObjectMarker.setComposition(objectComposition);
    }

    private static Predicate<ObjectMarker> findObjectPredicate(ObjectComposition objectComposition, TileObject object, WorldPoint worldPoint)
    {
        // Find the ObjectPoint for the given composition, object, and world point. There are two cases:
        // 1) object is a multiloc, the name may have changed since marking - match from base id
        // 2) not a multiloc, but an object has spawned with an identical name and a different
        //    id as what was originally marked
        return om -> {
            var op = om.getObjectPoint();
            return ((op.getId() == -1 || op.getId() == object.getId()) || op.getName().equals(objectComposition.getName()))
                && op.getRegionX() == worldPoint.getRegionX()
                && op.getRegionY() == worldPoint.getRegionY()
                && op.getPlane() == worldPoint.getPlane();
        };
    }

    @Nullable
    private ObjectComposition getObjectComposition(int id)
    {
        ObjectComposition objectComposition = client.getObjectDefinition(id);
        return objectComposition.getImpostorIds() == null ? objectComposition : objectComposition.getImpostor();
    }
}
