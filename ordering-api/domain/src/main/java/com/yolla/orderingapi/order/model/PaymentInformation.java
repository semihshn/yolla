package com.yolla.orderingapi.order.model;

import com.yolla.orderingapi.common.valueObject.Currency;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentInformation {

    String cardHolderName;
    String cardNumber;
    String expireYear;
    String expireMonth;
    String cvc;

    Integer installment;

    Currency currency;
}
