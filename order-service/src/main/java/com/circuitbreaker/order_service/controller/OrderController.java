package com.circuitbreaker.order_service.controller;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.circuitbreaker.order_service.model.Type;
import com.circuitbreaker.order_service.service.OrderService;

@RestController
@RequestMapping("orders")

public class OrderController {

    @Autowired
    private OrderService orderService;

    @GetMapping
    public Type getOrderByOrderNumber(@RequestParam("orderNumber") String orderNumber) {
        return orderService.getOrderByPostCode(orderNumber);
    }
}
