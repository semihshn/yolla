package com.yolla.paymentapi.common.exception;

public class ProcessedEventAlreadyExistsException extends RuntimeException {

    public ProcessedEventAlreadyExistsException(String aggregateId) {
        super("Event already processed: " + aggregateId);
    }
}
