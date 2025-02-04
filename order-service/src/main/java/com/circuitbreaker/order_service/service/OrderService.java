package com.circuitbreaker.order_service.service;

import com.circuitbreaker.order_service.model.Type;

public interface OrderService {
    Type getOrderByPostCode(String orderNumber);
}
