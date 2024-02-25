package com.yolla.orderingapi.common.valueObject;

import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Money implements Serializable {

    BigDecimal amount;

    Currency currency;

    public Boolean isGreaterThan(Double val) {
        return amount.doubleValue() > val;
    }
}
