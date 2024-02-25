package com.yolla.paymentapi.adapter.payment.jpa.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yolla.paymentapi.common.event.order.OrderedMenuItem;
import com.yolla.paymentapi.common.exception.ExceptionType;
import com.yolla.paymentapi.common.exception.PaymentJsonConvertException;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Slf4j
@Converter(autoApply = true)
@Component
public class OrderedMenuItemListConverter implements AttributeConverter<List<OrderedMenuItem>, String> {

    private final ObjectMapper objectMapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    @Override
    public String convertToDatabaseColumn(List<OrderedMenuItem> data) {
        try {
            return objectMapper.writeValueAsString(data);
        } catch (JsonProcessingException e) {
            log.error("Unable to convert list to json", e);
            throw new PaymentJsonConvertException(ExceptionType.CONVERT_OBJECT_TO_JSON);
        }
    }

    @Override
    public List<OrderedMenuItem> convertToEntityAttribute(String jsonData) {
        try {
            if (Objects.isNull(jsonData)) {
                return null;
            }

            return objectMapper.readerForListOf(OrderedMenuItem.class).readValue(jsonData);
        } catch (JsonProcessingException e) {
            log.error("Unable to convert json to list", e);
            throw new PaymentJsonConvertException(ExceptionType.CONVERT_OBJECT_TO_JSON);
        }
    }
}

