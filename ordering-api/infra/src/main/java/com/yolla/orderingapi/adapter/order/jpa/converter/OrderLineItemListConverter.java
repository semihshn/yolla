package com.yolla.orderingapi.adapter.order.jpa.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yolla.orderingapi.order.model.OrderLineItem;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.List;

@Converter
public class OrderLineItemListConverter implements AttributeConverter<List<OrderLineItem>, String> {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(List<OrderLineItem> attribute) {
        try {
            return OBJECT_MAPPER.writeValueAsString(attribute);
        } catch (JsonProcessingException exception) {
            throw new IllegalArgumentException("Order line items cannot be serialized", exception);
        }
    }

    @Override
    public List<OrderLineItem> convertToEntityAttribute(String databaseData) {
        try {
            return OBJECT_MAPPER.readValue(databaseData, new TypeReference<>() {
            });
        } catch (JsonProcessingException exception) {
            throw new IllegalArgumentException("Order line items cannot be deserialized", exception);
        }
    }
}
