package com.yolla.orderingapi.common.exception;

import lombok.Getter;

@Getter
public class OrderingApiJsonConvertException extends RuntimeException {

    private final ExceptionType exceptionType;
    private String detail;

    public OrderingApiJsonConvertException(ExceptionType exceptionType, String detail) {
        super(exceptionType.getMessage());
        this.exceptionType = exceptionType;
        this.detail = detail;
    }

    public OrderingApiJsonConvertException(ExceptionType exceptionType) {
        super(exceptionType.getMessage());
        this.exceptionType = exceptionType;
    }

}
