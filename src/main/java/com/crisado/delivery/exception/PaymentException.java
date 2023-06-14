package com.crisado.delivery.exception;

public class PaymentException extends RuntimeException {

    public PaymentException(String message) {
        super(message);
    }

    public PaymentException(Throwable e) {
        super(e.getMessage(), e);
    }

    public PaymentException(String message, Throwable e) {
        super(message, e);
    }

}
