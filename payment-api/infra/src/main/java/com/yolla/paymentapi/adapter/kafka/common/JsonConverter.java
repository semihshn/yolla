package com.yolla.paymentapi.adapter.kafka.common;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yolla.paymentapi.common.event.DomainEvent;
import com.yolla.paymentapi.common.event.DomainEventEnvelope;
import com.yolla.paymentapi.common.exception.ExceptionType;
import com.yolla.paymentapi.common.exception.PaymentJsonConvertException;

import java.util.List;

public class JsonConverter {

    public  static <T extends DomainEvent> List<DomainEventEnvelope<T>> convertTo(ObjectMapper objectMapper,
                                                                                  String stringEvent,
                                                                                  TypeReference<List<DomainEventEnvelope<T>>> listTypeReference) {
        List<DomainEventEnvelope<T>> objectEvent;
        try {
            objectEvent = objectMapper.readValue(stringEvent, listTypeReference);
        } catch (JsonProcessingException e) {
            throw new PaymentJsonConvertException(ExceptionType.CONVERT_OBJECT_TO_JSON);
        }
        return objectEvent;
    }

    public static <T extends DomainEvent> String convertTo(ObjectMapper objectMapper, List<DomainEventEnvelope<T>> domainEventEnvelope) {
        String payload;
        try {
            payload = objectMapper.writeValueAsString(domainEventEnvelope);
        } catch (JsonProcessingException e) {
            throw new PaymentJsonConvertException(ExceptionType.CONVERT_OBJECT_TO_JSON);
        }
        return payload;
    }
}
