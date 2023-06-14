package com.crisado.delivery.exception;

public class PaymentTypeChangedException extends PaymentException {

    public PaymentTypeChangedException() {
        super("Payment Type can not be changed");
    }

}
