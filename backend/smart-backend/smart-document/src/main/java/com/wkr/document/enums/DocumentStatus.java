package com.wkr.document.enums;

import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

public enum DocumentStatus {
    DRAFT,
    PROCESSING,
    READY,
    FAILED,
    ARCHIVED;

    private static final Map<DocumentStatus, Set<DocumentStatus>> TRANSITIONS = Map.of(
            DRAFT, EnumSet.of(PROCESSING, ARCHIVED),
            PROCESSING, EnumSet.of(READY, FAILED),
            FAILED, EnumSet.of(PROCESSING, ARCHIVED),
            READY, EnumSet.of(ARCHIVED),
            ARCHIVED, EnumSet.noneOf(DocumentStatus.class)
    );

    public boolean canTransitionTo(DocumentStatus target) {
        return TRANSITIONS.getOrDefault(this, Set.of()).contains(target);
    }
}
