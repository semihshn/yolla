package com.yolla.orderingapi.common.rest;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Response {

    private Object data;
    private String errorCode;
    private String errorDescription;

    private Response(Object data, String errorCode, String errorDescription) {
        this.data = data;
        this.errorCode = errorCode;
        this.errorDescription = errorDescription;
    }

    public static Response success(Object data) {
        return new Response(data, null, null);
    }

    public static Response error(String errorCode, String errorDescription) {
        return new Response(null, errorCode, errorDescription);
    }

    public Object getData() {
        return data;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getErrorDescription() {
        return errorDescription;
    }
}
