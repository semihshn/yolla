package com.yolla.orderingapi.unit;

import com.yolla.orderingapi.adapter.order.OrderController;
import com.yolla.orderingapi.common.rest.RestExceptionHandler;
import com.yolla.orderingapi.order.OrderFacade;
import com.yolla.orderingapi.order.model.Order;
import com.yolla.orderingapi.order.model.OrderItem;
import com.yolla.orderingapi.order.model.OrderStatus;
import com.yolla.orderingapi.order.model.PaymentMethod;
import com.yolla.orderingapi.order.model.PlaceOrderCommand;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentCaptor.forClass;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderController.class)
@Import(RestExceptionHandler.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderFacade orderFacade;

    @Test
    void should_map_request_to_domain_model_and_return_response_model() throws Exception {
        //given
        UUID orderId = UUID.randomUUID();
        Order order = Order.confirmed(
                orderId,
                "customer-1",
                List.of(OrderItem.builder()
                        .productId("product-1")
                        .quantity(2)
                        .unitPrice(new BigDecimal("14.99"))
                        .build()),
                new BigDecimal("29.98"),
                PaymentMethod.CARD,
                LocalDateTime.of(2025, 1, 1, 12, 0)
        );
        given(orderFacade.apply(any())).willReturn(order);

        //when / then
        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "customerId": "customer-1",
                                  "items": [
                                    {"productId": "product-1", "quantity": 2, "unitPrice": 14.99}
                                  ],
                                  "paymentMethod": "CARD"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.id").value(orderId.toString()))
                .andExpect(jsonPath("$.data.totalAmount").value(29.98))
                .andExpect(jsonPath("$.data.status").value(OrderStatus.CONFIRMED.name()));

        var commandCaptor = forClass(PlaceOrderCommand.class);
        then(orderFacade).should().apply(commandCaptor.capture());
        assertThat(commandCaptor.getValue().getCustomerId()).isEqualTo("customer-1");
        assertThat(commandCaptor.getValue().getItems()).hasSize(1);
        assertThat(commandCaptor.getValue().getItems().get(0).getUnitPrice()).isEqualByComparingTo("14.99");
        assertThat(commandCaptor.getValue().getPaymentMethod()).isEqualTo(PaymentMethod.CARD);
    }

    @Test
    void should_reject_request_model_when_constraint_validation_fails() throws Exception {
        //given
        String invalidRequest = """
                {
                  "customerId": "",
                  "items": [
                    {"productId": "", "quantity": 0, "unitPrice": 0}
                  ]
                }
                """;

        //when / then
        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidRequest))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.data").doesNotExist())
                .andExpect(jsonPath("$.errorCode").value("VALIDATION_ERROR"));

        then(orderFacade).shouldHaveNoInteractions();
    }
}
