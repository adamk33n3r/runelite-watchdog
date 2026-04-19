# Safe `onChange` Hook on `VarOutput`

## Context

Today, `VarOutput<T>` deliberately has no `onChange` listener API — only `onConnectChange`. The invariant comes from a past `StackOverflowError` (CHANGELOG:40, defended by tests in `LogicNodeConnectionTest.java`). Panels that want to display a computed output value must subscribe to each upstream `VarInput` and re-read the output inside the callback:

```java
// AddNodePanel.java:28-29 — today
addDisposer(node.getA().onChange(a -> resultView.setValue(node.getResult().getValue())));
addDisposer(node.getB().onChange(b -> resultView.setValue(node.getResult().getValue())));
```

Every transformation node panel duplicates the graph's edge into UI code; a `Clamp` panel has three. The workaround is recorded in `feedback_no_varoutput_onchange.md`.

### Why naive `VarOutput.onChange` loops

If we fired listeners from `VarOutput.setValue`, the cycle runs through `ConnectionLine`:

1. `out.setValue(v)` fires the new `VarOutput.onChange`.
2. A listener calls `variable.setValue(v)` (e.g. `ViewInput`/`NumberInput`).
3. The widget fires its own internal change event.
4. `ConnectionLine.java:37-39` registered `variable.registerOnChange(nv -> out.setValue(nv))`.
5. Back to step 1 — unbounded recursion.

`ViewInput.setValue` (ViewInput.java:32-38) has no equality guard, so this fires even for idempotent updates.

### Goal

Expose a safe `VarOutput.onChange(Consumer<T>)` returning a disposer, symmetric to `VarInput.onChange`, so panels can write:

```java
addDisposer(node.getResult().onChange(resultView::setValue));
```

---

## Approach

Two coordinated changes:

1. **Re-entrance guard on `VarOutput.setValue`** — per-instance `firing` flag that suppresses nested `onChange` invocation on the same output. Downstream `send()` propagation unchanged (preserves `Connection.java:15` initial-push semantics).
2. **`ConnectionLine` double-write cleanup** — suppress the widget's `registerOnChange → out.setValue` round-trip while ConnectionLine is programmatically setting the widget in response to a push on `in`. Today each push through a pass-through row writes the output twice (once via line 25, once via the widget's internal change event firing line 37-39).

### Rejected alternatives

- **Equality short-circuit in setValue** — breaks `Connection.java:15`'s initial push and any downstream `process()` that relies on the always-fires contract.
- **Hidden probe `VarInput`** — would pollute `VarOutput.getConnections()`, which is consulted by `Graph.wouldCreateCycle` (Graph.java:67), `ConnectionAutoMatcher`, and UI `isConnected()`. Every cycle check and auto-match becomes wrong.

### Guard scope (documented caveat)

The `firing` guard protects against **self-cycles through a single output** (the ConnectionLine widget↔output pattern). It does NOT protect against mutual feedback between two different outputs (A.onChange writes B, B.onChange writes A). The cross-output termination test (below) documents this by asserting the final settled state after one hop each side.

---

## Changes

### 1. `src/main/java/com/adamk33n3r/nodegraph/VarOutput.java`

Add listener list, firing flag, and `onChange`. Replace `setValue`:

```java
private final List<Consumer<T>> onChange = new ArrayList<>();
private transient boolean firing = false;

@Override
public void setValue(T value) {
    this.value = value;
    this.send();                         // unchanged — downstream Connections fire as before
    if (this.firing) return;             // cycle-break: nested setValue on same output swallowed
    this.firing = true;
    try {
        for (Consumer<T> c : this.onChange) {
            c.accept(this.value);        // re-read this.value so observers see settled state
        }
    } finally {
        this.firing = false;
    }
}

public Runnable onChange(Consumer<T> onChange) {
    this.onChange.add(onChange);
    return () -> this.onChange.remove(onChange);
}
```

Signature mirrors `VarInput.java:71-74`. Mark both new fields `transient` (defensive; `GraphSerializer` doesn't reflect over `VarOutput`, so no JSON impact — but good for future-proofing).

### 2. `src/main/java/com/adamk33n3r/runelite/watchdog/ui/nodegraph/connections/ConnectionLine.java`

Add a local flag to suppress the widget's change-event writeback while ConnectionLine is programmatically setting the widget from an upstream push. Contained to this file — no widget API changes.

```java
public ConnectionLine(@Nullable ConnectionPointIn<T> in, ConnectedVariable<T> variable, @Nullable ConnectionPointOut<T> out) {
    this.in = in;
    this.out = out;
    this.setLayout(new BorderLayout(5, 5));
    this.setOpaque(false);

    final boolean[] updatingFromIn = { false };

    if (this.in != null) {
        variable.setValue(this.in.getInputVar().getValue());
        this.add(this.in, BorderLayout.WEST);
        disposers.add(this.in.getInputVar().onChange((newValue) -> {
            updatingFromIn[0] = true;
            try {
                variable.setValue(newValue);
            } finally {
                updatingFromIn[0] = false;
            }
            if (this.out != null) {
                this.out.getOutputVar().setValue(newValue);
            }
        }));
        disposers.add(this.in.getInputVar().onConnectChange((_c) -> {
            boolean connected = this.in.getInputVar().isConnected();
            this.in.setConnected(connected);
            variable.setEnabled(!connected);
        }));
    }

    if (this.out != null) {
        this.add(this.out, BorderLayout.EAST);
        variable.registerOnChange((newValue) -> {
            if (updatingFromIn[0]) return;          // don't round-trip a programmatic set
            this.out.getOutputVar().setValue(newValue);
        });
        disposers.add(this.out.getOutputVar().onConnectChange((_c) -> {
            this.out.setConnected(this.out.getOutputVar().isConnected());
        }));
    }
    this.add(variable.getComponent(), BorderLayout.CENTER);
}
```

Behavior after change:
- **User edits widget** → `registerOnChange` fires → `out.setValue(v)` exactly once (flag is false).
- **Push through `in`** → explicit `out.setValue(newValue)` writes exactly once; the widget-triggered path is suppressed.
- **Display-only row** (`in != null, out == null`) → flag is inert, behavior unchanged.
- **Output-only row** (`in == null, out != null`) → `updatingFromIn` never flips; `registerOnChange` always writes.

### 3. Panel migrations

Replace the per-input listener pattern with a single `result.onChange` subscription:

- `AddNodePanel.java:28-29`
- `SubtractNodePanel.java:28-29`
- `MultiplyNodePanel.java:28-29`
- `DivideNodePanel.java:28-29`
- `MinNodePanel.java:28-29`
- `MaxNodePanel.java:28-29`
- `ClampNodePanel.java:31-33` (three listeners collapse to one)
- `logic/EqualityNodePanel.java:39-41`
- `logic/BooleanGateNodePanel.java:39-41`

Standard pattern:
```java
this.resultOut = new ConnectionPointOut<>(this, node.getResult());
ViewInput<T> resultView = new ViewInput<>("Result", node.getResult().getValue());
addDisposer(node.getResult().onChange(resultView::setValue));
this.items.add(new ConnectionLine<>(null, resultView, this.resultOut));
```

**Deferred to a follow-up PR** (need per-node verification — some have non-display listeners mixed in, and Counter's current listener is on an exec input with different semantics):
- `CounterNodePanel`
- `logic/InventoryCheckNodePanel`
- `logic/LocationCompareNodePanel`

### 4. Tests

**New file** `src/test/java/com/adamk33n3r/runelite/watchdog/nodegraph/VarOutputOnChangeTest.java`:

1. `test_onChange_fires_once_per_setValue` — two `setValue` calls fire listener twice.
2. `test_onChange_disposer_unregisters` — calling the returned `Runnable` stops the listener.
3. `test_onChange_does_not_re_enter_same_output` — listener that calls `setValue` on its own output does not recurse. Verifies the outer invocation iterates listeners exactly once; nested `setValue` updates `this.value` and runs `send()` but does not re-fire `onChange`.
4. `test_onChange_cross_output_chain_terminates` — A.onChange writes B, B.onChange writes A. Each output's per-instance guard breaks after one hop; assert final settled values and no overflow. Documents the guard's scope.
5. `test_onChange_multiple_listeners_all_fire_in_order` — register two listeners; verify both fire once per setValue, in registration order.

**Append to** `src/test/java/com/adamk33n3r/runelite/watchdog/nodegraph/LogicNodeConnectionTest.java`:

- `test_var_output_onChange_fires_on_push_through_connection` — integration: `Num → Connection → Add.a`, listener on `Add.result`, pushing `Num` fires the result listener (sits next to existing stack-overflow regression tests).

### 5. Memory update

`feedback_no_varoutput_onchange.md` is stale. Replace body with:

> `VarOutput.onChange` is supported. Prefer `node.getOutput().onChange(view::setValue)` over subscribing to each upstream `VarInput`.
> **Why:** previous `StackOverflowError` fix (see `LogicNodeConnectionTest`).
> **How to apply:** the per-instance `firing` guard protects against self-cycles (widget↔output round-trips in `ConnectionLine`). It does NOT protect mutual feedback between two different outputs — don't create observers that write back to a different output that writes back to this one.

---

## Files to modify

- `src/main/java/com/adamk33n3r/nodegraph/VarOutput.java` — add `onChange` list + `firing` guard + method.
- `src/main/java/com/adamk33n3r/runelite/watchdog/ui/nodegraph/connections/ConnectionLine.java` — `updatingFromIn` local flag.
- `src/main/java/com/adamk33n3r/runelite/watchdog/ui/nodegraph/AddNodePanel.java`
- `src/main/java/com/adamk33n3r/runelite/watchdog/ui/nodegraph/SubtractNodePanel.java`
- `src/main/java/com/adamk33n3r/runelite/watchdog/ui/nodegraph/MultiplyNodePanel.java`
- `src/main/java/com/adamk33n3r/runelite/watchdog/ui/nodegraph/DivideNodePanel.java`
- `src/main/java/com/adamk33n3r/runelite/watchdog/ui/nodegraph/MinNodePanel.java`
- `src/main/java/com/adamk33n3r/runelite/watchdog/ui/nodegraph/MaxNodePanel.java`
- `src/main/java/com/adamk33n3r/runelite/watchdog/ui/nodegraph/ClampNodePanel.java`
- `src/main/java/com/adamk33n3r/runelite/watchdog/ui/nodegraph/logic/EqualityNodePanel.java`
- `src/main/java/com/adamk33n3r/runelite/watchdog/ui/nodegraph/logic/BooleanGateNodePanel.java`
- `src/test/java/com/adamk33n3r/runelite/watchdog/nodegraph/VarOutputOnChangeTest.java` — new.
- `src/test/java/com/adamk33n3r/runelite/watchdog/nodegraph/LogicNodeConnectionTest.java` — one integration test appended.

## Reference files (read-only, informed the design)

- `VarInput.java:71-74` — `onChange` signature symmetry.
- `Connection.java:10-16` — initial-push semantics preserved.
- `GraphSerializer.java` — confirmed it does not reflect over `VarOutput` internals.
- `Graph.java:67`, `ConnectionAutoMatcher.java` — why the hidden-probe alternative was rejected.

## Verification

1. `./gradlew test --tests "com.adamk33n3r.runelite.watchdog.nodegraph.VarOutputOnChangeTest"` — new unit tests pass.
2. `./gradlew test --tests "com.adamk33n3r.runelite.watchdog.nodegraph.LogicNodeConnectionTest"` — all existing regression tests pass (especially the three `*_does_not_stack_overflow` tests).
3. `./gradlew test --tests "com.adamk33n3r.runelite.watchdog.nodegraph.MathNodeTest"` — math node behavior unchanged.
4. `./gradlew test` — full suite green.
5. Manual QA via `NodeGraphLauncher`:
   - Wire `Num → Add.a`, `Num → Add.b`. Change either Num; confirm `AddNodePanel` Result view updates live and exactly once per change (no double-fire).
   - Chain `Add → Clamp → BooleanGate`; change upstream; confirm all downstream Result views update.
   - Disconnect an input mid-run; confirm no stray updates or NPEs.
   - Edit a `NumberInput` widget directly in a node that has `in` + `out` on the same row (e.g. a `Num` pass-through); confirm the output is written exactly once (not twice).
