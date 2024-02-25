package com.yolla.orderingapi.common.exception;

import lombok.Getter;

@Getter
public class OrderingJsonConvertException extends RuntimeException {

    private final ExceptionType exceptionType;
    private String detail;

    public OrderingJsonConvertException(ExceptionType exceptionType, String detail) {
        super(exceptionType.getMessage());
        this.exceptionType = exceptionType;
        this.detail = detail;
    }

    public OrderingJsonConvertException(ExceptionType exceptionType) {
        super(exceptionType.getMessage());
        this.exceptionType = exceptionType;
    }

}
