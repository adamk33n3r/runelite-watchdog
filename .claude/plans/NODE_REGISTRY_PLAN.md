# Plan: NodeTypeRegistry — Automatic Node Serialization

## Context

`GraphSerializer` currently has hardcoded `instanceof` chains for serialization and `switch` statements for deserialization. Every new node type requires manually editing this file with per-field JSON logic. The goal is a registry-based approach where registering a new simple node is a single line — `registry.registerSubtype(BooleanGate.class, BooleanGate::new)` — and the registry auto-handles all `VarInput`/`VarOutput` state.

---

## Core Insight

All node state is already accessible through `node.getInputs()` and `node.getOutputs()` (`LinkedHashMap<String, VarInput<?>>` / `VarOutput<?>`), since every var is registered via `reg()` in node constructors. Each `Var<T>` carries its `Class<T>` type token, enabling type-safe Gson serialization without reflection.

---

## JSON Format

Each node serializes to:
```json
{
  "id": "...", "x": 100, "y": 200, "type": "BooleanGate",
  "vars": {
    "in":  { "A": false, "B": false },
    "out": { "Result": false }
  }
}
```
Complex nodes add an extra top-level key:
```json
{
  "type": "TriggerNode",
  "alert": { ... },
  "vars": { "in": { "Enabled": false, "Name": "", "Debounce": 0.0 }, "out": {} }
}
```
Using nested `in`/`out` sub-objects avoids the `TriggerNode` var name collision (`name` input and `nameOut` output both use var name `"Name"` but live in separate maps).

**Breaking change**: the old flat format (`"op"`, `"a"`, `"b"` at the top level) is replaced. Since this is an in-progress branch, no migration is needed.

---

## New File: `NodeTypeRegistry.java`

**Location**: `src/main/java/com/adamk33n3r/nodegraph/NodeTypeRegistry.java`

### Functional Interfaces (static inner)
```java
@FunctionalInterface interface NodeFactory<N extends Node> {
    N create(JsonObject json, Gson gson);
}
@FunctionalInterface interface ExtraSerializer<N extends Node> {
    void serialize(N node, JsonObject target, Gson gson);
}
```

### Fields
```java
private final Map<Class<? extends Node>, NodeEntry<?>> byClass = new LinkedHashMap<>();
private final Map<String, NodeEntry<?>> byLabel  = new LinkedHashMap<>();
```

### Registration API
```java
// Simple: all VarInput/VarOutput auto-serialized
public <N extends Node> NodeTypeRegistry registerSubtype(Class<N> clazz, Supplier<N> factory)

// Complex: factory reads domain object from JSON; extraSerializer writes it back
public <N extends Node> NodeTypeRegistry registerSubtype(
    Class<N> clazz,
    NodeFactory<N> factory,
    ExtraSerializer<N> extraSerializer
)
```
Both return `this` for fluent chaining. Label is always `clazz.getSimpleName()`.

### `serialize(Node node, JsonObject target, Gson gson) → boolean`
1. Look up `NodeEntry` by `node.getClass()` — return `false` (with `log.warn`) if unknown
2. Write `"type"` label
3. Call `entry.invokeSerializer(node, target, gson)` (extra hook — adds `"alert"`, `"notification"`, etc.)
4. Create `JsonObject in`, `JsonObject out`; iterate `node.getInputs()` and `node.getOutputs()`:
   - Skip if `var.getType() == ExecSignal.class`
   - `Number.class` → `addProperty(name, ((Number) value).doubleValue())`
   - `Boolean.class` → `addProperty(name, (Boolean) value)`
   - `String.class` → `addProperty(name, (String) value)`
   - `type.isEnum()` → `addProperty(name, ((Enum<?>) value).name())`
   - `String[].class` → build `JsonArray`
5. Add `"vars": { "in": in, "out": out }` to target

### `deserialize(JsonObject nodeObj, Gson gson) → Node`
1. Look up `NodeEntry` by `"type"` string — return `null` if unknown
2. Call `entry.factory.create(nodeObj, gson)` to construct node
3. Read `vars.in` and `vars.out` sub-objects; for each registered var, call `deserializeVar`:
   - Skip `ExecSignal.class`
   - `Number.class` → `var.setValue(elem.getAsDouble())` via `setValueUnchecked`
   - `Boolean.class` → `getAsBoolean()`
   - `String.class` → `getAsString()`
   - `type.isEnum()` → `Enum.valueOf((Class<Enum>) type, elem.getAsString())`
   - `String[].class` → build `String[]` from `JsonArray`
4. Return node

### `setValueUnchecked` (private)
```java
@SuppressWarnings({"unchecked", "rawtypes"})
private void setValueUnchecked(Var var, Object value) { var.setValue(value); }
```
Mirrors existing `connectUnchecked` pattern in `GraphSerializer`.

### `NodeEntry.invokeSerializer`
Unsafe casts contained inside `NodeEntry` — keeps public API clean.

---

## Files to Modify

### 1. `BooleanGate.java` — add `reg(this.op)`
**File**: `src/main/java/com/adamk33n3r/nodegraph/nodes/logic/BooleanGate.java:37`
```java
// Before:
reg(this.a);
reg(this.b);
reg(this.result);

// After:
reg(this.op);   // ← add this
reg(this.a);
reg(this.b);
reg(this.result);
```

### 2. `Equality.java` — add `reg(this.op)`
**File**: `src/main/java/com/adamk33n3r/nodegraph/nodes/logic/Equality.java:42`
Same pattern as above.

### 3. `NotificationNode.java` — fix duplicate var name
**File**: `src/main/java/com/adamk33n3r/nodegraph/nodes/NotificationNode.java:16`
```java
// Before:
private final VarInput<Number> fireWhenAfkSeconds = new VarInput<>(this, "Fire When AFK", Number.class, 0);

// After:
private final VarInput<Number> fireWhenAfkSeconds = new VarInput<>(this, "Fire When AFK Seconds", Number.class, 0);
```
The current duplicate key causes `fireWhenAfk` to be silently lost from the `inputs` map — `fireWhenAfkSeconds` overwrites it in the `LinkedHashMap`.

### 4. `GraphSerializer.java`
**File**: `src/main/java/com/adamk33n3r/runelite/watchdog/alerts/GraphSerializer.java`

Constructor: add `NodeTypeRegistry nodeRegistry` parameter.

`serialize()`: replace the entire `if (node instanceof ...) else if ...` chain with:
```java
JsonObject nodeObj = new JsonObject();
nodeObj.addProperty("id", node.getId().toString());
nodeObj.addProperty("x", node.getX());
nodeObj.addProperty("y", node.getY());
if (!nodeRegistry.serialize(node, nodeObj, subGson)) continue;
nodesArr.add(nodeObj);
```

`deserialize()`: replace entire `switch (nodeType)` block with:
```java
Node node = nodeRegistry.deserialize(nodeObj, subGson);
if (node == null) continue;
```

### 5. `AlertManager.java`
**File**: `src/main/java/com/adamk33n3r/runelite/watchdog/AlertManager.java`

Build registry before constructing `GraphSerializer`:
```java
NodeTypeRegistry nodeRegistry = new NodeTypeRegistry()
    .registerSubtype(Add.class, Add::new)
    .registerSubtype(Bool.class, Bool::new)
    .registerSubtype(Num.class, Num::new)
    .registerSubtype(BooleanGate.class, BooleanGate::new)
    .registerSubtype(Equality.class, Equality::new)
    .registerSubtype(TriggerNode.class,
        (json, gson) -> new TriggerNode(gson.fromJson(json.get("alert"), Alert.class)),
        (node, obj, gson) -> obj.add("alert", gson.toJsonTree(node.getAlert(), Alert.class))
    )
    .registerSubtype(ContinuousTriggerNode.class,
        (json, gson) -> new ContinuousTriggerNode(
            (ContinuousAlert) gson.fromJson(json.get("alert"), Alert.class)),
        (node, obj, gson) -> obj.add("alert", gson.toJsonTree(node.getAlert(), Alert.class))
    )
    .registerSubtype(NotificationNode.class,
        (json, gson) -> new NotificationNode(gson.fromJson(json.get("notification"), Notification.class)),
        (node, obj, gson) -> obj.add("notification", gson.toJsonTree(node.getNotification(), Notification.class))
    );

GraphSerializer graphSerializer = new GraphSerializer(alertTypeFactory, notificationTypeFactory, this.clientGson, nodeRegistry);
```

---

## Key Edge Cases

| Issue | Solution |
|---|---|
| `BooleanGate`/`Equality` `op` not in `inputs` map | Add `reg(this.op)` to both constructors |
| `NotificationNode` duplicate `"Fire When AFK"` key | Rename `fireWhenAfkSeconds` var to `"Fire When AFK Seconds"` |
| TriggerNode input `"Name"` vs output `"Name"` collision | Use nested `in`/`out` sub-objects in `"vars"` |
| `Number` is abstract — Gson can't instantiate it | Deserialize as `elem.getAsDouble()` → stored as `Double` |
| `ExecSignal` is runtime-only | Skip any var where `type == ExecSignal.class` |
| `Num` has only VarOutputs (output IS the state) | Serialize both inputs and outputs (`"out"` sub-object) |
| `ContinuousTriggerNode` extends `TriggerNode` | `byClass.get(node.getClass())` is exact match — no ordering issue |

---

## Verification

1. Launch `NodeGraphLauncher` (test sources) — create a graph with BooleanGate, Equality, Bool, Num, TriggerNode, NotificationNode nodes and connections
2. Save the graph (triggers `GraphSerializer.serialize`)
3. Reload — verify all node positions, values, op settings, and connections are restored
4. Add a new node type (e.g., `Subtract`) — confirm only one `registerSubtype` call in `AlertManager` is needed, with zero `GraphSerializer` changes
