package com.yolla.orderingapi.common.exception;

import lombok.Getter;

@Getter
public class OrderingApiBrokerException extends RuntimeException {

    private final ExceptionType exceptionType;
    private String detail;

    public OrderingApiBrokerException(ExceptionType exceptionType, String detail) {
        super(exceptionType.getMessage());
        this.exceptionType = exceptionType;
        this.detail = detail;
    }

    public OrderingApiBrokerException(ExceptionType exceptionType) {
        super(exceptionType.getMessage());
        this.exceptionType = exceptionType;
    }

}
