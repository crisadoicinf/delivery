package com.crisado.delivery.exception;

public class PaymentUnkownTypeException extends PaymentException {

    public PaymentUnkownTypeException() {
        super("Unknown Payment Type");
    }

}
