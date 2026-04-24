# Node Graph Editor — Pre-Release TODO

## Critical Bugs

- [x] **BooleanGate: `op` var not registered**
  `src/main/java/com/adamk33n3r/nodegraph/nodes/logic/BooleanGate.java`
  Add `reg(this.op);` in the constructor so the AND/OR operator serializes correctly.

- [x] **Equality: `op` var not registered**
  `src/main/java/com/adamk33n3r/nodegraph/nodes/logic/Equality.java`
  Same fix — add `reg(this.op);` so the comparison operator (==, !=, <, etc.) round-trips through save/load.

- [x] **ActionNode: duplicate var name collision**
  `src/main/java/com/adamk33n3r/nodegraph/nodes/ActionNode.java`
  `fireWhenAfk` and `fireWhenAfkSeconds` both use the name `"Fire When AFK"`. The second silently overwrites the first in the LinkedHashMap. Rename to `"Fire When AFK Seconds"`.

- [x] **VarOutput stale connection references**
  `src/main/java/com/adamk33n3r/nodegraph/VarOutput.java:10`
  If an input is overwritten, VarOutput still holds a reference to the removed VarInput. Fix by sourcing connections from Graph rather than VarOutput's own list.

## Serialization

- [ ] **Implement `NodeTypeRegistry`** (see `NODE_REGISTRY_PLAN.md`)
  Replace the hardcoded `instanceof`/`switch` chains in `GraphSerializer` with a registry-driven approach. Requires:
  - New class `NodeTypeRegistry.java`
  - Update `GraphSerializer.java` to use it
  - Build registry in `AlertManager.java`

- [x] **Remove `Random` stub**
  `src/main/java/com/adamk33n3r/nodegraph/nodes/Random.java`
  The class is an empty shell. Delete the file before release.

- [ ] **`Dummy<T>` and `Logger<T>` are not serializable**
  Generic type parameters prevent these nodes from round-tripping through `GraphSerializer`. Either restrict to concrete types, add custom serialization, or exclude them from the node creation popup.

## ContinuousTriggerNode

- [x] **`ContinuousTriggerNode` is never fired from `EventHandler`**
  `src/main/java/com/adamk33n3r/runelite/watchdog/EventHandler.java`
  `ContinuousTriggerNode` can be deleted along with all references to it. It was a failed attempt at a node type that
  didn't end up making it in.

## Math & Logic Nodes

- [x] **Missing math operations**
  Only `Add` is implemented. Add at minimum: `Subtract`, `Multiply`, `Divide`. `Min`/`Max`/`Clamp` would also be useful.

- [x] **No conditional branch node**
  There is no if/else node that routes an ExecSignal down one of two paths based on a boolean. This is essential for non-trivial graphs.

## UI / UX

- [x] **No user-visible error when a node fails to deserialize**
  Unknown node types are silently skipped with a log warning. Show a placeholder node (e.g. "Unknown node — could not load") so users know their graph is incomplete after an upgrade.
  Implement this via a new `NoteNode`. This node doesn't have any inputs or outputs. It just has a serializable `note` field that can be edited by the user

- [x] **Debounce not working correctly for advanced alerts**
  In `fireAdvancedAlertTriggerNode` the code is looking at the advanced alert's debounce value, but this value is not exposed to the user
  This function should be fixed so that it looks at the `triggerNode.getAlert().getDebounce()` instead to determine if it should fire

- [x] **Remove hardcoded example graphs from `GraphPanel`**
  `setUpExample1()` / `setUpExample2()` are inlined in `GraphPanel.java`. Move them to the `NodeGraphLauncher` test harness or a separate examples file.

## Other New Nodes
- [x] Add a new `DisplayNode` that takes an `Object` as input (so that it can take in anything) and displays it in a human-readable
  way. This can take inspiration from the ViewInput class

## Testing

- [x] No unit tests for `LocationCompare` and `InventoryCheck` execution paths
- [x] Add serialization round-trip tests for `BooleanGate` and `Equality` (after the `reg()` fix)
- [x] Add tests for `ActionNode` after the duplicate var name fix

## Performance / Stability

- [x] **Graph execution silently swallows exceptions**
  Daemon threads in `Graph.executeExecChain()` have no try/catch. Add logging at minimum; surface errors in the History panel ideally.

- [x] **No graph validation before execution**
  Validate that required inputs are connected (or have defaults) before firing a trigger, and surface validation errors in the UI.
