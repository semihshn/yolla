package com.yolla.orderingapi.common.rest;

import com.yolla.orderingapi.order.exception.DomainRuleViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Objects;
import java.util.stream.Collectors;

@RestControllerAdvice
public class RestExceptionHandler {

    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    @ExceptionHandler(DomainRuleViolationException.class)
    public Response handleDomainRuleViolation(DomainRuleViolationException exception) {
        return Response.error("ORDER_RULE_VIOLATION", exception.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Response handleMethodArgumentNotValid(MethodArgumentNotValidException exception) {
        String errorDescription = exception.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> fieldError.getField() + " " + Objects.requireNonNullElse(fieldError.getDefaultMessage(), "is invalid"))
                .collect(Collectors.joining(", "));
        return Response.error("VALIDATION_ERROR", errorDescription);
    }
}
