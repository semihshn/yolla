package com.yolla.paymentapi.common.exception;

import lombok.Getter;

@Getter
public class PaymentApiBusinessException extends RuntimeException {

    private final ExceptionType exceptionType;
    private String detail;

    public PaymentApiBusinessException(ExceptionType exceptionType, String detail) {
        super(exceptionType.getMessage());
        this.exceptionType = exceptionType;
        this.detail = detail;
    }

    public PaymentApiBusinessException(ExceptionType exceptionType) {
        super(exceptionType.getMessage());
        this.exceptionType = exceptionType;
    }

}
