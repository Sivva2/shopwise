package com.shopwise.entity;

public enum TransactionType {
    EARNED("Points gagnés"),
    REDEEMED("Points utilisés"),
    ADJUSTMENT("Ajustement");

    private final String label;

    TransactionType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
