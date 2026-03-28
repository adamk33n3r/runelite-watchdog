# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Overview

Watchdog is a RuneLite plugin that lets players create custom alerts triggered by in-game events (chat messages, stat changes, XP drops, spawned objects, etc.) and respond with configurable actions (sounds, overlays, screen flashes, TTS, webhooks, etc.).

## Build Commands

```bash
./gradlew build          # Build and run tests
./gradlew test           # Run all tests
./gradlew jar            # Build standard JAR
./gradlew shadowJar      # Build all-in-one JAR → jars/watchdog-{version}-all.jar
```

To run a single test class:
```bash
./gradlew test --tests "com.adamk33n3r.runelite.watchdog.GlobTest"
```

**Version** is read from `src/main/resources/com/adamk33n3r/runelite/watchdog/version.properties` (Major.Minor.Patch-Phase format).

## Architecture

### Core Flow

Game events → `EventHandler` → `WatchdogPlugin.processAlert()` → `AlertProcessor` (background thread) → `Notification.fire()` → `fireImpl()`

### Key Classes

- **`WatchdogPlugin`** — Plugin lifecycle, Guice bindings, hotkey management, overlay registration, banned region tracking. Exposes a static `getInstance()` used by alerts/notifications.
- **`AlertManager`** — Loads/saves alerts from RuneLite config as JSON. Manages the `CopyOnWriteArrayList<Alert>` in memory. Handles Gson `RuntimeTypeAdapterFactory` setup for polymorphic serialization. Contains version upgrade logic for backward compatibility.
- **`EventHandler`** — Subscribes to all RuneLite game events (`@Subscribe`). Maintains previous game state (skill levels, inventory, location) to detect changes. Calls `processAlert()` when trigger conditions match.
- **`AlertProcessor`** — Spawns background threads to fire notifications with optional per-notification delays.
- **`WatchdogPanel`** — Main Swing UI panel with sub-panels: `AlertListPanel`, `HistoryPanel`, `ToolsPanel`, `AlertHubPanel`.

### Polymorphic Type System

Both alert and notification types use enums to map display names to implementation classes:

- **`TriggerType`** enum → maps to `Alert` subclasses (e.g., `ChatAlert`, `StatChangedAlert`, `SpawnedAlert`)
- **`NotificationType`** enum → maps to `Notification` subclasses (e.g., `GameMessage`, `ScreenFlash`, `Sound`)

When adding a new alert or notification type:
1. Create the implementation class in `alerts/` or `notifications/`
2. Add an entry to `TriggerType` or `NotificationType` enum
3. Register the subtype in `AlertManager.init()` via `RuntimeTypeAdapterFactory`
4. Add the UI panel in `ui/alerts/` or `ui/notifications/`
5. Wire up the UI in `PanelUtils` or the relevant factory

### Alert Base Class (`Alert.java`)

All alerts share: `enabled`, `name`, `alertMode`, `debounceTime`, `debounceResetTime`, `randomNotifications`, and a `List<Notification>`. The `parent` (`AlertGroup`) reference is `@transient` and lazily resolved. `getType()` looks up the `TriggerType` by matching the concrete class.

### Notification Base Class (`Notification.java`)

`shouldFire()` checks: plugin is enabled, not in banned area, AFK state matches config, and window focus state matches `fireWhenFocused`. The public `fire()` calls `shouldFire()` then `fireImpl()`. `fireForced()` skips the check. Notifications are Guice-injected with `@transient` fields (not serialized).

### Regex/Glob Matching

`RegexMatcher` alerts support both glob patterns (using `{}` for capture groups) and regex (using `()` for capture groups). Captured values are passed as `triggerValues[]` to notifications where `$1`, `$2`, etc. are substituted in message templates.

### Serialization

Alerts are serialized to JSON via Gson with `RuntimeTypeAdapterFactory` for polymorphism. Import/export uses GZIP compression + Base64 encoding. The `AlertManager` registers all known alert and notification subtypes at init time — unknown subtypes from old versions are ignored via `.ignoreSubtype()`.

### Node Graph Editor (in-progress, `feat/node-graph-editor` branch)

A visual node-based editor is being built in `com.adamk33n3r.nodegraph` and `ui/nodegraph/`. `NodeGraphLauncher` in test sources can launch the editor standalone for development.

## Testing

Tests use JUnit 4 + Mockito + Guice's `BoundFieldModule`. `TestBase` wires up all required RuneLite mocks and creates a Guice injector. `AlertTestBase` extends this with `EventBus` and `HistoryPanel` mocks.

Fields annotated `@Bind` in test classes are automatically bound in the Guice injector. `@Mock` / `@Spy` (Mockito) can be combined with `@Bind` (Guice).
