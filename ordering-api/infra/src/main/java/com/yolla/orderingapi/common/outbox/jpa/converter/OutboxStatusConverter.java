package com.yolla.orderingapi.common.outbox.jpa.converter;

import com.yolla.orderingapi.common.outbox.model.OutboxStatus;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class OutboxStatusConverter implements AttributeConverter<OutboxStatus, Integer> {

    @Override
    public Integer convertToDatabaseColumn(OutboxStatus status) {
        return status.getValue();
    }

    @Override
    public OutboxStatus convertToEntityAttribute(Integer value) {
        return OutboxStatus.of(value);
    }
}
