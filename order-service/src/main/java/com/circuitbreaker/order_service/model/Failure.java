package com.circuitbreaker.order_service.model;

import lombok.Data;

@Data
// public class Failure implements Type {
// private final String msg;
// }
public class Failure implements Type {
    private String message;

    public Failure(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}