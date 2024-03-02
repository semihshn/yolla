package com.yolla.orderingapi.adapter.order.jpa.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yolla.orderingapi.order.model.OrderLineItem;
import com.yolla.orderingapi.common.exception.ExceptionType;
import com.yolla.orderingapi.common.exception.OrderingApiJsonConvertException;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Slf4j
@Converter(autoApply = true)
@Component
public class OrderLineItemListConverter implements AttributeConverter<List<OrderLineItem>, String> {

    private final ObjectMapper objectMapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    @Override
    public String convertToDatabaseColumn(List<OrderLineItem> data) {
        try {
            return objectMapper.writeValueAsString(data);
        } catch (JsonProcessingException e) {
            log.error("Unable to convert list to json", e);
            throw new OrderingApiJsonConvertException(ExceptionType.CONVERT_OBJECT_TO_JSON);
        }
    }

    @Override
    public List<OrderLineItem> convertToEntityAttribute(String jsonData) {
        try {
            if (Objects.isNull(jsonData)) {
                return null;
            }

            return objectMapper.readerForListOf(OrderLineItem.class).readValue(jsonData);
        } catch (JsonProcessingException e) {
            log.error("Unable to convert json to list", e);
            throw new OrderingApiJsonConvertException(ExceptionType.CONVERT_OBJECT_TO_JSON);
        }
    }
}

