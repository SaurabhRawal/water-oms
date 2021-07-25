package com.rubicon.water.Enums;

public enum OrderStatus {

    REQUESTED("Order has been placed but not yet delivered."),
    IN_PROGRESS("Order is being delivered right now."),
    DELIVERED("Order has been delivered."),
    CANCELLED("Order was cancelled before delivery");

    private String value;

    OrderStatus(final String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return this.getValue();
    }
}
