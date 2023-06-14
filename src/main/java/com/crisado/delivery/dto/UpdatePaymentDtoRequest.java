package com.crisado.delivery.dto;

import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
public class UpdatePaymentDtoRequest extends CreatePaymentDtoRequest {

    @Min(1)
    private final long paymentId;

    public UpdatePaymentDtoRequest(long orderId, long paymentId, PaymentDto newPayment) {
        super(orderId, newPayment);
        this.paymentId = paymentId;
    }

}
