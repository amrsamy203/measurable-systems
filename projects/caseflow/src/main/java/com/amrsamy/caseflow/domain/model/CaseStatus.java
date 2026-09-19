package com.amrsamy.caseflow.domain.model;

import java.util.EnumSet;
import java.util.Set;

public enum CaseStatus {
    NEW,
    QUEUED,
    ASSIGNED,
    IN_PROGRESS,
    RESOLVED,
    CLOSED;

    private static final Set<CaseStatus> TERMINAL = EnumSet.of(RESOLVED, CLOSED);

    public boolean canTransitionTo(CaseStatus target) {
        return switch (this) {
            case NEW -> target == QUEUED || target == ASSIGNED || target == CLOSED;
            case QUEUED -> target == ASSIGNED || target == CLOSED;
            case ASSIGNED -> target == IN_PROGRESS || target == QUEUED || target == CLOSED;
            case IN_PROGRESS -> target == RESOLVED || target == ASSIGNED || target == CLOSED;
            case RESOLVED -> target == CLOSED || target == IN_PROGRESS;
            case CLOSED -> false;
        };
    }

    public boolean isTerminal() {
        return TERMINAL.contains(this);
    }
}
