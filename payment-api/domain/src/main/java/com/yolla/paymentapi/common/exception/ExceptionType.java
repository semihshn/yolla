package com.yolla.paymentapi.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ExceptionType {

    GENERIC_EXCEPTION(1, "Bilinmeyen bir sorun oluştu."),
    CONVERT_OBJECT_TO_JSON(2001,"Object convert to json exception."),
    REDIS_LOCK_EXCEPTION(2002,"Could not lock for aggregate id");

    private final Integer code;
    private final String message;

}
