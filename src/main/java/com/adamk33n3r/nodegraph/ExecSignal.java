package com.adamk33n3r.nodegraph;

import lombok.Getter;

@Getter
public final class ExecSignal {
    private final String[] captureGroups;

    public ExecSignal(String[] captureGroups) {
        this.captureGroups = captureGroups != null ? captureGroups : new String[0];
    }

    @Override
    public String toString() {
        return captureGroups.length > 0 ? "[" + captureGroups.length + " groups]" : "—";
    }
}
