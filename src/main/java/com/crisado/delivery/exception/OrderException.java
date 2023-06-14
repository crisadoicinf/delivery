package com.crisado.delivery.exception;

public class OrderException extends RuntimeException {

    public OrderException(String message) {
        super(message);
    }

    public OrderException(Throwable cause) {
        super(cause.getMessage(), cause);
    }

}
