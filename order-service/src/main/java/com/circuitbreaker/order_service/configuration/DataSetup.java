package com.circuitbreaker.order_service.configuration;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import com.circuitbreaker.order_service.model.Order;
import com.circuitbreaker.order_service.repository.OrderRepository;

import jakarta.annotation.PostConstruct;

@Configuration
public class DataSetup {
    @Autowired
    private OrderRepository orderRepository;

    @PostConstruct
    public void setupData() {
        orderRepository.saveAll(Arrays.asList(
                Order.builder().orderNumber("0c70c0c2").postalCode("1000001").build(),
                Order.builder().orderNumber("7f8f9f15").postalCode("1100000").build(),
                Order.builder().orderNumber("394627b2").postalCode("2100001").build()));
    }
}
