package com.yolla.orderingapi.adapter.order.rest.request;

import com.yolla.orderingapi.order.model.PaymentInformation;
import com.yolla.orderingapi.common.valueObject.Currency;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentInformationRequest {

    @NotBlank
    String cardHolderName;

    @NotBlank
    String cardNumber;

    @NotBlank
    String expireYear;

    @NotBlank
    String expireMonth;

    @NotBlank
    String cvc;

    @NotNull
    Integer installment;

    @NotEmpty
    Currency currency;

    public PaymentInformation toModel() {
        return PaymentInformation.builder()
                .cardHolderName(cardHolderName)
                .cardNumber(cardNumber)
                .expireYear(expireYear)
                .expireMonth(expireMonth)
                .cvc(cvc)
                .installment(installment)
                .currency(currency)
                .build();
    }
}
