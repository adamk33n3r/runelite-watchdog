# Changelog

## v4.0.0-beta.3

> **This is a beta release.** Advanced Alerts are experimental and may have rough edges. Please report any issues you encounter!

---

### New Node Types

Several new node types are available in the graph editor:

#### Flow

| Node | Description |
|------|-------------|
| **Delay** | Delays an exec signal by a configurable number of milliseconds before passing it downstream |
| **Timer** | Fires on a repeating interval; accepts an **Exec** input to start and a **Reset** input to cancel; a **Pulse** toggle switches between one-shot and repeating mode |
| **Counter** | Counts incoming exec signals and outputs the running total; a separate **Reset** exec input resets the count to zero |

#### Math

| Node | Description |
|------|-------------|
| **Round** | Rounds a number to the nearest integer |
| **Floor** | Floors a number down to the next lower integer |
| **Ceiling** | Ceils a number up to the next higher integer |

#### Utility

| Node | Description |
|------|-------------|
| **To String** | Converts any value to its string representation — useful for feeding numbers or booleans into message template nodes |
| **Display** | Shows the live value of any connected wire directly in the graph editor; acts as a debugging probe with no side effects |

---

### Graph Editor Improvements

- **Revamped Add Node popup** — completely rebuilt with a categorized, searchable layout; action (notification) nodes are now organized into subcategories matching the main notification type categories
- **Renamed "Notifications" → "Actions"** throughout the graph editor for consistency with the rest of the plugin
- **Node properties as wirable inputs** — several node types (`PluginState`, `InventoryCheck`, `LocationCompare`, `NoteNode`) now expose their configuration fields as connectable `VarInput` ports, so they can be driven by upstream nodes rather than only configured inline
- **Collapsible help sections in Advanced Alert panel** — the alert detail panel now shows collapsible info sections; opening one auto-collapses the others
- **Fixed auto-save for several node types** — `ActionNode`, `Add`, and `Branch` nodes were missing dirty-tracking calls, meaning edits to their inputs could be lost on reload; this is now fixed
- **Fixed integer/double input handling** — number inputs now correctly enforce integer vs. floating-point constraints depending on the field

---

### Bug Fixes

- Fixed alerts with a blank message pattern matching every event — empty glob and regex patterns now produce no-match instead of matching everything
- Fixed the **Private Chat** alert label "Prepend Sender" renamed to **"Prepend Name"** to better reflect what it does (prepends the other player's name)

---

### Other Improvements

- **Private message direction filter** — the **Private Chat** alert type now has a **Chat Direction** dropdown (Both / Sent Only / Received Only) when "Private" is selected as the chat type; use this to alert only on messages you send, only ones you receive, or both

---

### Performance

- **Spawned-event batching (opt-in)** — A new setting **Plugin Settings → Batch Spawned Events (Experimental)** collapses spawn-event matching to once per game tick instead of once per event. During zone transitions, hundreds of spawn events used to trigger a separate matching loop (and a new background thread) for each event; with this setting on they are queued and drained together at end-of-tick.
  - **Default: off.** When off, behavior is unchanged from beta.2.
  - **When on**: a SpawnedAlert fires at most once per game tick, even with debounce set to 0. 300 cows spawning during a region load fires once instead of 300 times. If multiple matching events carry different capture-group values (e.g. `{*}`), the captures from the first matching event are used.
  - AdvancedAlert graph triggers are not collapsed — every queued event still passes through the trigger node so downstream graph state stays accurate.

---

## v4.0.0-beta.2

> **This is a beta release.** Advanced Alerts are experimental and may have rough edges. Please report any issues you encounter!

---

### New Node Types

Several new node types are available in the graph editor:

| Node | Category | Description |
|------|----------|-------------|
| **Inventory** | Variable | Outputs the player's current inventory as a value that can be passed to other nodes |
| **Inventory Check** | Logic | Checks whether the inventory contains a specific item (by name or ID), with optional quantity threshold |
| **Location** | Variable | Outputs the player's current world location (region, coordinates) |
| **Location Compare** | Logic | Compares the player's location against a saved location, with configurable tolerance |
| **Plugin Var** | Variable | Reads a live plugin variable (skill level, XP, etc.) and outputs its current value |
| **Boolean Gate** | Logic | Passes a boolean signal through only when a condition is met — a more flexible replacement for the old `AND` node |
| **Equality** | Logic | Compares two values and outputs `true` if they are equal (supports numbers and booleans) |

Plugin variables (skill levels, etc.) are now initialized and kept up-to-date automatically when the plugin starts, so `Plugin Var` nodes always have a current value.

---

### Graph Editor Improvements

- **Zoom & overview mode** — scroll to zoom the canvas in/out; zoom level is rounded to one decimal place for clean display. An overview/minimap mode lets you see the full graph at a glance
- **Per-type connection colors** — connection lines are now color-coded by data type (boolean, number, inventory, location, etc.), making it easy to trace data flow at a glance
- **Hover effects on ports** — connection points highlight when hovered, making it clearer where you can connect
- **Smart popup filtering** — when you drag from an output port and open the Add Node popup, only compatible node types are shown
- **Auto-connect on drop** — when you add a node via drag-from-port, the best matching input port is automatically connected to the port you dragged from
- **Improved node header names** — node panel titles are now consistent and correctly reflect the node type

---

### Bug Fixes

- Fixed a `StackOverflowError` that could occur in certain `VarInput` connection configurations
- Fixed visual glitches with connection lines not rendering correctly in some cases
- Fixed a Guice injector bug that could cause crashes when opening the graph editor
- Fixed an issue where creating certain node types without configuration could produce an invalid state
- Fixed `LocationCompare` node not updating when its inputs changed
- Added additional null-safety guards in `EventHandler`, `Alert`, and `Graph` to prevent unexpected crashes

---

## v4.0.0-beta.1

> **This is a beta release.** Advanced Alerts are experimental and may have rough edges. Please report any issues you encounter!

---

### Advanced Alerts (Beta) — Visual Node Graph Editor

The headline feature of v4.0.0 is **Advanced Alerts**: a brand-new way to build complex alert logic using a visual node graph editor. Instead of a single trigger firing a fixed list of notifications, you can now wire together nodes in a graph to create conditional, multi-step alert flows.

**How it works:**

- Create an **Advanced Alert** from the alert creation menu (requires "Enable Advanced Alerts (Beta)" in plugin settings — off by default)
- Click **Open Graph Editor** to open a dedicated graph editor window
- Drag nodes onto the canvas and connect them by drawing lines between their ports
- Right-click the canvas to add new nodes from a popup menu

**Available node types:**

| Node | Description |
|------|-------------|
| **Trigger Nodes** | One node per alert type (Chat, Stat Change, Spawned Object, etc.). These are the entry points — they fire when the in-game event occurs. Capture groups from pattern matching (`$1`, `$2`, etc.) are passed downstream. |
| **Notification Nodes** | Fire any notification type (Sound, Screen Flash, TTS, Overlay, etc.). Multiple triggers or logic nodes can feed into a single notification. |
| **Variable Nodes** | Hold a named constant value (boolean or number) that can be fed into other nodes. |
| **Logic Nodes** | `AND` — combines multiple boolean signals. More logic node types are planned. |
| **Math Nodes** | `ADD` — adds two numbers together. More math node types are planned. |
| **Continuous Trigger** | A special trigger that fires on a repeating interval, useful for polling-style alerts. |

**Graph features:**

- **Drag-and-drop connections** — click an output port and drag to an input port to connect nodes
- **Cycle detection** — the graph will refuse connections that would create an infinite loop
- **Inline editing** — notification and alert settings are editable directly inside each node panel in the graph
- **Live test button** — fire the graph immediately to test your setup without waiting for an in-game trigger
- **Auto-save** — the graph saves automatically as you make changes

> Advanced Alerts are hidden behind a config toggle to keep things clean for users who don't need them. Enable via: **Plugin Settings → Enable Advanced Alerts (Beta)**

---

### Automatic Alert Backups

Watchdog now automatically backs up your alerts daily so you never lose your work.

- Backups are saved to `.runelite/watchdog/backups/<profile-name>/` as compressed `.json.gz` files
- One backup per day, named by date (e.g. `alerts_2026-04-08.json.gz`)
- Old backups are automatically pruned — keeps the **7 most recent** by default
- Configurable in **Plugin Settings → Backups**: toggle on/off and set max number of backups to keep
- Backups are organized by RuneLite profile name, so switching profiles keeps backups separate

---

### Other Changes

- **Compressed alert storage** — alerts are now stored in a compressed format in the RuneLite config, reducing config size for users with many alerts
- **Improved UI panel architecture** — internal refactor to allow alert and notification panels to be reused both in the sidebar and inside graph editor nodes (no visible change for most users, but makes the node editor work seamlessly)
