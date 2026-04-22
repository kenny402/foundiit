package com.student.foundiit.model;

import lombok.Getter;

@Getter
public enum ItemStatus {
    LOST("Lost", "bg-danger"),
    FOUND("Found", "bg-success"),
    CLAIMED("Claimed", "bg-info"),
    CLOSED("Closed", "bg-secondary");

    private final String displayName;
    private final String badgeClass;

    ItemStatus(String displayName, String badgeClass) {
        this.displayName = displayName;
        this.badgeClass = badgeClass;
    }

    public String getDisplayName() {
        return displayName;
    }
}
