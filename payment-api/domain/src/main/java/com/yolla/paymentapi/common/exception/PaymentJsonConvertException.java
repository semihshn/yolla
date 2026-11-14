package com.yolla.paymentapi.common.exception;

import lombok.Getter;

@Getter
public class PaymentJsonConvertException extends RuntimeException {

    private final ExceptionType exceptionType;
    private String detail;

    public PaymentJsonConvertException(ExceptionType exceptionType, String detail) {
        super(exceptionType.getMessage());
        this.exceptionType = exceptionType;
        this.detail = detail;
    }

    public PaymentJsonConvertException(ExceptionType exceptionType) {
        super(exceptionType.getMessage());
        this.exceptionType = exceptionType;
    }

}
