package com.yolla.orderingapi.order.model;

public enum OrderState {
    RECEIVED("Received"),
    PREPARING("Preparing"),
    REVISION_PENDING("Revision Pending"),
    REVISION_CONFIRMED("Revision Confirmed"),
    REVISION_REJECTED("Revision Rejected"),
    REJECTED("Rejected");

    private final String status;

    OrderState(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}
