package com.circuitbreaker.order_service;

import com.circuitbreaker.order_service.model.Order;
import com.circuitbreaker.order_service.service.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

//@SpringBootTest + MockMvc	For testing Spring MVC behavior, including HTTP status codes, request handling, and validation.
//@ExtendWith(SpringExtension.class) // JUnit 5 Integration
@SpringBootTest
@AutoConfigureMockMvc // Configures MockMvc, which allows HTTP request testing without starting a
                      // server.c
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean // Mocks the OrderService
    private OrderService orderService;

    @Test
    void testGetOrderByOrderNumber() throws Exception {

        Order mockOrder = new Order();
        mockOrder.setOrderNumber("0c70c0c2");
        mockOrder.setShippingCity("New York");
        mockOrder.setShippingState("NY");

        when(orderService.getOrderByPostCode("0c70c0c2")).thenReturn(mockOrder);
        mockMvc.perform(get("/orders?orderNumber=0c70c0c2")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()) // Expecting HTTP 200
                .andExpect(jsonPath("$.orderNumber").value("0c70c0c2"))
                .andExpect(jsonPath("$.shippingCity").value("New York"))
                .andExpect(jsonPath("$.shippingState").value("NY"));
    }
}
