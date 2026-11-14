package com.yolla.paymentapi.payment.model;

public enum PaymentState {

    COMPLETED("Completed"),
    CANCELLED("Cancelled"),
    REFUNDED("Refunded"),
    EXPIRED("Expired"),
    REJECTED("Rejected");

    private final String status;

    PaymentState(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}
