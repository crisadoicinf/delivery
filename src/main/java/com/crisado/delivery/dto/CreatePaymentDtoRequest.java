package com.crisado.delivery.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@AllArgsConstructor
@Getter
public class CreatePaymentDtoRequest {

    @Min(1)
    private final long orderId;
    @NotNull
    private final PaymentDto newPayment;
}
