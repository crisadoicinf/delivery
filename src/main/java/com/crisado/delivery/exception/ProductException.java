package com.crisado.delivery.exception;

public class ProductException extends RuntimeException {

    public ProductException(String message) {
        super(message);
    }

    public ProductException(Throwable cause) {
        super(cause.getMessage(), cause);
    }

}
