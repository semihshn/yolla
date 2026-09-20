package com.yolla.orderingapi.adapter.order.rest;

import com.yolla.orderingapi.order.OrderService;
import com.yolla.orderingapi.order.model.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderController.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderService orderService;

    @Test
    void createOrder_returnsCreatedOrderId() throws Exception {
        given(orderService.createOrder(any(Order.class)))
                .willReturn(Order.builder().orderId("order-123").build());

        mockMvc.perform(post("/ordering/v1/orders")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                  "restaurantId": 42,
                                  "orderLineItems": [
                                    {
                                      "menuItemId": 10,
                                      "name": "Burger",
                                      "quantity": 2,
                                      "unitPrice": 12.50
                                    }
                                  ]
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.orderId").value("order-123"));
    }
}
