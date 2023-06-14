package com.crisado.delivery.exception;

public class OrderNotFoundException extends OrderException {

    private final String key;

    public OrderNotFoundException(Object key) {
        super("Order not found");
        this.key = String.valueOf(key);
    }

    public String getKey() {
        return key;
    }

}
