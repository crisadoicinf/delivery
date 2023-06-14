package com.crisado.delivery.exception;

public class PaymentNotFoundException extends PaymentException {

    private final String key;

    public PaymentNotFoundException(Object key) {
        super("Payment not found");
        this.key = String.valueOf(key);
    }

    public String getKey() {
        return key;
    }

}
