package com.shopwise.entity;

public enum AppointmentStatus {
    SCHEDULED("Planifié"),
    COMPLETED("Honoré"),
    CANCELLED("Annulé");

    private final String label;

    AppointmentStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
