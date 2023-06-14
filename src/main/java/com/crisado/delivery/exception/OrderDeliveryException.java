package com.crisado.delivery.exception;

public class OrderDeliveryException extends RuntimeException {

    public OrderDeliveryException(String message) {
        super(message);
    }

    public OrderDeliveryException(Throwable cause) {
        super(cause.getMessage(), cause);
    }

}
