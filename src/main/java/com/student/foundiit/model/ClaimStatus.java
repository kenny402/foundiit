package com.student.foundiit.model;

public enum ClaimStatus {
    PENDING, APPROVED, REJECTED, CLOSED;

    public String getDisplayName() {
        return name().charAt(0) + name().substring(1).toLowerCase();
    }
}