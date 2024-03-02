package com.yolla.orderingapi.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ExceptionType {

    GENERIC_EXCEPTION(1, "Bilinmeyen bir sorun oluştu."),
    CONVERT_OBJECT_TO_JSON(2001,"Object convert to json exception."),
    OUTBOX_EXCEPTION(2002,"Outbox payload cannot be serialized.");

    private final Integer code;
    private final String message;

}
