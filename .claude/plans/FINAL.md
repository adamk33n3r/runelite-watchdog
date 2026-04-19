# Node Graph Editor — Pre-Release Plan (Watchdog)

## Context

The `feat/node-graph-editor` branch adds an `AdvancedAlert` type that executes a visual node graph (`com.adamk33n3r.nodegraph.*` + `runelite/watchdog/ui/nodegraph/*`) when its `TriggerNode`s match. This plan is the final push before release — 17 tracked items across critical bugs, cleanup, new nodes, UI/UX fixes, stability, and tests.

Exploration verified that **none** of the TODO-ticked items are actually done yet (ticks represent intended scope). Every stage follows TDD: write/extend tests first for the section, implement, run `./gradlew test`, iterate until green, then move on.

**Out of scope** (unchecked in TODO): `NodeTypeRegistry` refactor; `Dummy<T>`/`Logger<T>` generic-type serialization; graph validation (floating/unconnected nodes are acceptable at user discretion).

---

## Stage 1 — Dead Code Removal (low risk, unblocks later stages)

### 1a. Delete `Random.java`
- `src/main/java/com/adamk33n3r/nodegraph/nodes/Random.java` is an empty `class Random extends Node {}`; zero references verified by grep.
- Delete file. No test changes.

### 1b. Delete `ContinuousTriggerNode` and references
References to remove:
- `src/main/java/com/adamk33n3r/nodegraph/nodes/ContinuousTriggerNode.java` (file delete)
- `src/main/java/com/adamk33n3r/runelite/watchdog/alerts/GraphSerializer.java` — serialize branch (~52–54) and deserialize case `"ContinuousTriggerNode"` (~159–162)
- `src/main/java/com/adamk33n3r/runelite/watchdog/ui/nodegraph/AlertNodePanel.java` — `instanceof ContinuousTriggerNode` branch (lines ~3, 6–7)
- `src/main/java/com/adamk33n3r/runelite/watchdog/ui/nodegraph/GraphPanel.java` — factory branch (~565–567), import (line 3), `setUpExample2()` usage at ~172 (that example is being moved out entirely in Stage 8 anyway)
- `src/main/java/com/adamk33n3r/runelite/watchdog/EventHandler.java` — import at line 4 (unused)
- `src/test/java/com/adamk33n3r/runelite/watchdog/nodegraph/NodeVarRegistrationTest.java:10` and any `ContinuousTriggerNode`-specific assertion
- `ContinuousAlert.java` — **keep** (still a valid classic-mode alert type)

Tests: Remove the ContinuousTriggerNode-specific assertion in `NodeVarRegistrationTest`; ensure existing tests still pass.

---

## Stage 2 — Critical Serialization Bugs (TDD)

### Tests first (new file `src/test/java/com/adamk33n3r/runelite/watchdog/nodegraph/NodeSerializationRoundTripTest.java` or extend `GraphSerializerTest.java`)
- `booleanGate_preservesOpAfterRoundTrip` — create gate, set `op = Op.OR`, roundtrip, assert `.getOp().getValue() == Op.OR` AND that `getInputs().containsKey("Op")` after load.
- `equality_preservesOpAfterRoundTrip` — same pattern for `Op.GREATER_EQUAL`.
- `actionNode_fireWhenAfkAndAfkSecondsBothPreserved` — build ActionNode, set `fireWhenAfk=true`, `fireWhenAfkSeconds=30`, register via `getInputs()` → assert both keys present and values survive roundtrip. (This test currently fails because only one key survives the `LinkedHashMap` collision.)

### Implementation
- `nodegraph/nodes/logic/BooleanGate.java:37` — insert `reg(this.op);` above `reg(this.a);`
- `nodegraph/nodes/logic/Equality.java` — same: add `reg(this.op);`
- `nodegraph/nodes/ActionNode.java:17` — rename label `"Fire When AFK"` → `"Fire When AFK Seconds"`. Confirm `ActionNodePanel.java` already displays `"AFK Seconds"` (per explore: line ~49); no UI change needed.
- `GraphSerializer.java` — BooleanGate / Equality currently hardcode `op` in serialize/deserialize (lines 71–80, 203–221). After `reg(this.op)`, leaving the explicit `op` handling in place is lower-risk since the broader `NodeTypeRegistry` refactor is explicitly out of scope.

### Verify
`./gradlew test --tests "*NodeSerializationRoundTripTest*" --tests "*GraphSerializerTest*" --tests "*NodeVarRegistrationTest*"`

---

## Stage 3 — VarOutput Stale Connection References

### Design (decided: Node holds Graph reference)
Current state: `VarOutput.connections` is a locally-held `ArrayList<Connection<T>>` (`VarOutput.java:10-12`) duplicated with `Graph.connections` (`Graph.java:24`). Duplication risks desync when `Graph.connect()` auto-disconnects a prior connection.

Fix:
1. Add `Node.graph` reference, set inside `Graph.add()` / cleared in `Graph.remove()`.
2. `VarOutput.connections` becomes a derived view: `this.getNode().getGraph().getConnections().stream().filter(c -> c.getOutput() == this)...`.
3. `VarOutput.send()` iterates that derived view (and gracefully handles null graph — orphaned outputs do nothing, no throw).
4. `addConnection` / `removeConnection` on `VarOutput` become no-ops for state, only calling `fireConnectChange`.
5. Same treatment for `VarInput.connections`.

### Tests first
Extend `GraphTest` / `VarConnectChangeTest`:
- `varOutput_send_doesNotPushToStaleInput_afterOverwrite` — connect `outA → inX`; then `connect(outB, inX)` (graph auto-disconnects outA→inX). Set `outA.value = 42`. Assert `inX.value` is NOT 42.
- `varOutput_isConnected_reflectsGraphTruth` — create connection, `graph.disconnect(...)`, assert `out.isConnected() == false`.
- `varOutput_send_fromOrphanedOutput_doesNotCrash` — node removed from graph, then `setValue()` on its output → no throw.

### Verify
Run full graph/serializer tests; launch `NodeGraphLauncher` manually and create/overwrite connections to smoke-test.

---

## Stage 4 — New Math Nodes

New subclasses of `com.adamk33n3r.nodegraph.nodes.math.MathNode`:
- `Subtract.java` — `a - b`
- `Multiply.java` — `a * b`
- `Divide.java` — `a / b`. **Divide-by-zero returns `0.0` and logs a warning** (consistent with the default-returning safety-net style of existing math nodes; avoids NaN propagating through downstream logic).
- `Min.java` — `min(a, b)`
- `Max.java` — `max(a, b)`
- `Clamp.java` — 3 inputs: `Value`, `Min`, `Max`. Output = `Math.max(min, Math.min(max, value))`. If `min > max`, swap them.

Each node must also be registered:
1. `MathNodeType` enum — add enum entry per node
2. `NodeProbeFactory.java` — registered via `probes.put(MathNodeType.X, X::new)`
3. `GraphSerializer.java` — serialize case (mirroring `Add`'s block), deserialize case
4. `GraphPanel.createNodePanel()` — if any need a custom panel (most can reuse `AddNodePanel` via a small refactor; otherwise add a `<Name>NodePanel` per memory-rule "dedicated NodePanel per node type")
5. `NodeTypeCompatibilityChecker` — add a test case per new enum entry

### Tests first (extend `MathNodeTest.java`)
- Per node: `test_<op>_computes_expected_from_default_values`, `test_<op>_computes_after_setValue`, `test_<op>_recomputes_on_input_push` (parallel of `Add` tests)
- Divide: explicit divide-by-zero test (`b=0` → result=0, warning logged)
- Clamp: value-in-range, value-below-min, value-above-max, min>max swap case
- Serialization roundtrip test per node (extend `NodeSerializationRoundTripTest`)

### Verify
`./gradlew test --tests "*MathNodeTest*" --tests "*NodeSerializationRoundTripTest*" --tests "*NodeTypeCompatibilityCheckerTest*"`

---

## Stage 5 — Conditional Branch Node

Semantics (decided: Blueprint-style):
- Inputs: `Exec` (ExecSignal), `Condition` (Boolean)
- Outputs: `True` (ExecSignal), `False` (ExecSignal)
- On exec, sample `Condition`, route signal to exactly one of the two exec outputs.

New class: `com.adamk33n3r.nodegraph.nodes.flow.Branch.java` (introduces `nodes/flow/` subpackage if not already present; moving `DelayNode` to that package is optional and can stay where it is).

Registration:
1. `FlowNodeType` enum — add `BRANCH`
2. `NodeProbeFactory` — factory entry
3. `GraphSerializer` — serialize/deserialize cases (no extra fields beyond what `reg()` covers)
4. `Graph.executeExecChain()` — extend the `if (node instanceof ...)` cascade with a `Branch` case that selects which downstream set to enqueue based on `condition.getValue()`
5. New `BranchNodePanel.java` extending `NodePanel` — expose exec-in, condition-in, exec-out-true, exec-out-false
6. `GraphPanel.createNodePanel()` — factory entry

### Tests first (new file `BranchNodeTest.java`)
- `branch_routesToTrue_whenConditionTrue` — wire trigger → branch → two actions (one per output), set condition=true, exec → only "true" action fires
- `branch_routesToFalse_whenConditionFalse` — mirror
- `branch_withNoCondition_defaultsFalse` — no condition connected; assert default-false path fires
- `branch_seriallyChained` — branch → branch (nested)
- Serialization roundtrip

### Verify
`./gradlew test --tests "*BranchNodeTest*" --tests "*NodeTypeCompatibilityCheckerTest*"`

---

## Stage 6 — NoteNode (user notes + unknown-type placeholder)

Dual purpose:
1. User-authored sticky notes in the graph — no inputs/outputs, serializable `note: String` field.
2. Placeholder when `GraphSerializer.deserialize()` hits an unknown type. **Preserves type name AND raw JSON on a transient field** so save-after-load is lossless if the user later installs the missing node type.

New class: `com.adamk33n3r.nodegraph.nodes.utility.NoteNode.java`
- Fields: `note` (serialized), `originalType` + `originalJson` (populated only for unknown-node case; `originalJson` transient in-memory but re-emitted in serialize if present so the data survives resave).
- No `reg()` calls (no vars).

New UI: `NoteNodePanel.java` — single editable text area bound to `note`. For the unknown-node case, render readonly with type label.

Registration:
1. `GraphSerializer.serialize()` — `NoteNode` branch writing `note` (and original payload if present)
2. `GraphSerializer.deserialize()` —
   - New `"NoteNode"` case
   - Replace the current silent-skip-on-unknown-type with a **fallback** that constructs a NoteNode carrying the unknown-type name + original JSON payload
3. `NodeProbeFactory` — factory entry under a new `UtilityNodeType` enum (or extend `VariableNodeType`)
4. `GraphPanel.createNodePanel()` — factory entry

### Tests first
- `noteNode_preservesText_afterRoundTrip`
- `noteNode_withNoText_serializesCleanly`
- `graphSerializer_unknownNodeType_deserializesAsNoteNode` — feed crafted JSON with `"type": "NotAThingNode"`, assert result graph has a `NoteNode` with `originalType == "NotAThingNode"`
- `graphSerializer_unknownNodeType_survivesSaveAfterLoad` — load unknown, immediately save → original JSON payload is still present in output (lossless round-trip)

### Verify
`./gradlew test --tests "*NoteNode*" --tests "*GraphSerializerTest*"`

---

## Stage 7 — DisplayNode

Viewer node — takes an `Object` input, renders a human-readable string via `ViewInput`-style rendering (arrays, `InventoryItemDataMap`, Boolean → ✔/✖, otherwise `toString`).

New class: `com.adamk33n3r.nodegraph.nodes.utility.DisplayNode.java`
- `VarInput<Object> value` (type: `Object.class` so accepts any output). Confirm `NodeTypeCompatibilityChecker` allows this — may need a tweak for "Object-typed inputs accept any output type".
- No output (terminal).
- `process()` updates the UI binding; rendering logic should reuse `ViewInput.getStringRepresentation` — extract that method to a static util if not already reusable.

New UI: `DisplayNodePanel.java` reusing `ViewInput<Object>` bound to the input value.

Registration: same 4-point registry (NodeType enum, NodeProbeFactory, GraphSerializer, GraphPanel factory).

### Tests first
- `displayNode_rendersPrimitive_asToString`
- `displayNode_rendersArray_asBracketedList`
- `displayNode_rendersBoolean_asCheckmark` (assertion on the extracted utility's output — don't test Swing directly per CLAUDE.md)
- Serialization roundtrip (DisplayNode has no persistent value beyond position)

### Verify
`./gradlew test --tests "*DisplayNode*"`

---

## Stage 8 — UI/UX Fixes

### 8a. Debounce fix
- `EventHandler.java:662` — change `int debounce = alert.getDebounceTime();` → `int debounce = triggerNode.getAlert().getDebounceTime();`
- `EventHandler.java:666` — change `alert.isDebounceResetTime()` → `triggerNode.getAlert().isDebounceResetTime()` (both come from the same Alert, so keep them consistent).

### Tests first
Extend `AdvancedAlertEventHandlerTest`:
- `advancedAlert_debounce_usesTriggerNodeAlertValue` — `TriggerNode.alert.debounceTime=500ms`, `AdvancedAlert.debounceTime=0`; fire twice in quick succession; verify second is debounced (before fix it wouldn't be).
- `advancedAlert_debounceReset_usesTriggerNodeAlertValue` — `triggerNode.alert.debounceResetTime=true`; confirm reset behavior hits that flag.

### 8b. Move example graphs out of `GraphPanel`
- Delete `GraphPanel.setUpExample1()` (lines 107–167) and `setUpExample2()` (lines 169–195).
- Delete commented call at line ~199.
- Move both helpers into `NodeGraphLauncher.java` (test sources) as private statics; add a dropdown or two buttons to load each.

No unit tests for this (UI-only). Smoke-test via `NodeGraphLauncher`.

---

## Stage 9 — Stability: Exception Handling in Graph Execution

(Graph validation intentionally scoped out — floating/unconnected nodes are acceptable at user discretion.)

- `Graph.java:195–204` — wrap the delay-thread body in a top-level try/catch, logging + invoking a new `Graph.onError` callback that `AdvancedAlert` wires to `HistoryPanel`.
- `AdvancedAlert.java:33` — wrap `graph.executeExecChain(...)` inside the daemon thread in a top-level try/catch; on throw, call `historyPanel.addError(this, throwable)`.
- `HistoryPanel` — new `addError(Alert, Throwable)` method; `HistoryEntryPanel` tweak to show error state (distinct color / icon, consistent with existing row styling).

### Tests first
- `graph_executeExecChain_exceptionInActionNode_doesNotKillOtherActions` — one notification throws; assert sibling actions still fire; error reported via the `onError` callback.
- `advancedAlert_fireTriggerNode_exception_logsToHistoryPanel` — mock HistoryPanel, force a throw, verify `addError` called once with the right alert + throwable.
- `graph_executeExecChain_exceptionInDelayThread_loggedNotPropagated` — exception inside a `DelayNode`'s spawned thread reaches the error callback without killing the JVM or silently dying.

### Verify
`./gradlew test --tests "*Graph*" --tests "*AdvancedAlert*"` + manual smoke via NodeGraphLauncher: build a graph where one action throws, confirm other actions still fire and HistoryPanel shows an error row.

---

## Stage 10 — Execution-Path Tests for Existing Nodes

Close the gap on `LocationCompare` and `InventoryCheck` — the TODO notes missing coverage even though some Connection-level tests exist in `LogicNodeConnectionTest`.

### LocationCompare (extend `LogicNodeConnectionTest` or new `LocationCompareProcessTest.java`)
All branches of `process()`:
- `pointA == null` → result=false
- `pointB == null` → result=false
- `cardinalOnly=true`, diagonal (both X and Y differ) → false
- `cardinalOnly=true`, only-X differs → compares distance
- `cardinalOnly=true`, only-Y differs → compares distance
- `cardinalOnly=false`, within distance → true
- `cardinalOnly=false`, beyond distance → false

### InventoryCheck (new `InventoryCheckProcessTest.java`)
All branches of `process()` / `evaluateInventoryVar`:
- FULL: `itemCount == 28` / `!= 28`
- EMPTY: `itemCount == 0` / `!= 0`
- SLOTS: each `Comparator` value (LT / LTE / EQ / NEQ / GTE / GT) crossing the threshold
- ITEM: matchType NOTED / UN_NOTED / BOTH
- ITEM: itemName regex match vs. glob
- ITEM: quantity comparator crossing threshold
- ITEM_CHANGE: same breakdowns (behavior deferred unless divergent from ITEM)

### Verify
`./gradlew test --tests "*LocationCompare*" --tests "*InventoryCheck*"`

---

## Critical files (read before editing)

| File | Why |
|------|-----|
| `com/adamk33n3r/nodegraph/VarOutput.java` | Stale-ref fix (Stage 3) |
| `com/adamk33n3r/nodegraph/Graph.java` | Connection management + `executeExecChain` (Stages 3, 5, 9) |
| `com/adamk33n3r/nodegraph/Node.java` | Add `graph` field (Stage 3) |
| `com/adamk33n3r/nodegraph/nodes/ActionNode.java` | Duplicate var name (Stage 2) |
| `com/adamk33n3r/nodegraph/nodes/logic/BooleanGate.java`, `Equality.java` | `op` reg (Stage 2) |
| `com/adamk33n3r/runelite/watchdog/alerts/GraphSerializer.java` | Register each new subtype + NoteNode unknown-type fallback (Stages 4–7) |
| `com/adamk33n3r/runelite/watchdog/alerts/AdvancedAlert.java` | Exception handling (Stage 9) |
| `com/adamk33n3r/runelite/watchdog/EventHandler.java:658–675` | Debounce fix (Stage 8a) |
| `com/adamk33n3r/runelite/watchdog/ui/nodegraph/GraphPanel.java` | Panel factory + example-graph removal (all new-node stages + 8b) |
| `com/adamk33n3r/runelite/watchdog/ui/nodegraph/NodeProbeFactory.java` | Drag-popup registry (all new-node stages) |
| `com/adamk33n3r/runelite/watchdog/ui/nodegraph/*NodeType.java` enums | Enum entries for new nodes |
| `com/adamk33n3r/runelite/watchdog/ui/nodegraph/inputs/ViewInput.java` | Reuse `getStringRepresentation` (Stage 7) |
| `com/adamk33n3r/runelite/watchdog/ui/panels/HistoryPanel.java` | Add `addError` (Stage 9) |
| `src/test/java/com/adamk33n3r/runelite/watchdog/nodegraph/NodeGraphLauncher.java` | Home for moved example graphs (Stage 8b) |

## Reusable utilities (don't reinvent)
- `ViewInput.getStringRepresentation` — Stage 7 DisplayNode rendering
- `RegexMatcher` + `Util.matchPattern` — Stage 10 InventoryCheck tests
- `MathNode` base — Stage 4 math nodes
- `HistoryPanel.addEntry` pattern — Stage 9 `addError` mirrors this
- Test helpers in `MathNodeTest`, `DelayNodeTest` (CountDownLatch patterns) — Stages 4, 5, 9
- `TestBase` / `AlertTestBase` — Guice + Mockito setup for all stages

## Verification (end-to-end)
1. `./gradlew clean build` — full compile + all tests green
2. Launch `NodeGraphLauncher` and exercise:
   - Create / overwrite connections (Stage 3)
   - Build a graph using each new math node + branch + note + display (Stages 4–7)
   - Open/close graph; save/reload from disk (serialization cycle)
   - Load a deliberately-corrupted JSON with an unknown node type → see NoteNode placeholder (Stage 6)
3. Launch the full plugin via `WatchdogPluginLauncher`:
   - Create AdvancedAlert with a ChatAlert trigger with debounce=500ms; send matching message twice quickly → second suppressed (Stage 8a)
   - Trigger a notification that throws → HistoryPanel shows error row (Stage 9)

---

## Decision log (reference)
- **Stage 3** — `Node` holds a `Graph` reference; `VarOutput` derives connections from `node.getGraph().getConnections()`.
- **Stage 4 Divide** — divide-by-zero returns `0.0` + logs warning.
- **Stage 4 Clamp** — 3 inputs (`Value`, `Min`, `Max`); swap if `min > max`.
- **Stage 5 Branch** — Blueprint-style (Exec + Bool condition → True/False exec outputs, sampled at exec time).
- **Stage 6 NoteNode** — unknown-type placeholder preserves type name + raw JSON on a transient field so save-after-load is lossless.
- **Stage 8a** — `isDebounceResetTime` changes alongside `getDebounceTime` (both from the same Alert).
- **Dropped** — `NodeTypeRegistry` refactor, `Dummy<T>`/`Logger<T>` serialization, graph validation.
