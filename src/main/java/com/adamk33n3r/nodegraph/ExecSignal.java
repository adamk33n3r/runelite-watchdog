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
        if (captureGroups.length == 0) return "[]";
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < captureGroups.length; i++) {
            if (i > 0) sb.append(", ");
            sb.append('"').append(captureGroups[i]).append('"');
        }
        return sb.append(']').toString();
    }
}
