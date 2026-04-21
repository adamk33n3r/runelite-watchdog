package com.adamk33n3r.nodegraph.nodes.flow;

import com.adamk33n3r.nodegraph.ExecSignal;
import com.adamk33n3r.nodegraph.Node;
import com.adamk33n3r.nodegraph.VarInput;
import com.adamk33n3r.nodegraph.VarOutput;
import lombok.Getter;
import lombok.Setter;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicBoolean;

@Getter
public class TimerNode extends Node {
    private final VarInput<ExecSignal> exec       = new VarInput<>(this, "Exec", ExecSignal.class, new ExecSignal(new String[0]));
    private final VarInput<ExecSignal> reset      = new VarInput<>(this, "Reset", ExecSignal.class, new ExecSignal(new String[0]));
    private final VarInput<Number>     durationMs = new VarInput<>(this, "Duration (ms)", Number.class, 1000);
    private final VarInput<Boolean>    pulse      = new VarInput<>(this, "Pulse", Boolean.class, false);
    private final VarOutput<ExecSignal> execOut   = new VarOutput<>(this, "Exec", ExecSignal.class, new ExecSignal(new String[0]));
    private final VarOutput<ExecSignal> pulseOut  = new VarOutput<>(this, "Pulse", ExecSignal.class, new ExecSignal(new String[0]));

    // Canceled flag allows the pulse-swap inside a running scheduled runnable to detect
    // that a Reset arrived between the first fire and the scheduleAtFixedRate call.
    @Setter
    private volatile ScheduledFuture<?> currentFuture;
    private final AtomicBoolean canceled = new AtomicBoolean(false);

    public TimerNode() {
        this.exec.setAllowMultipleConnections(true);
        this.reset.setAllowMultipleConnections(true);

        reg(this.exec);
        reg(this.reset);
        reg(this.durationMs);
        reg(this.pulse);
        reg(this.execOut);
        reg(this.pulseOut);
    }

    public void cancelCurrentFuture() {
        this.canceled.set(true);
        ScheduledFuture<?> f = this.currentFuture;
        if (f != null) {
            f.cancel(false);
            this.currentFuture = null;
        }
    }

    @Override
    public void process() {
    }
}
