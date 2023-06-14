package com.crisado.delivery.dto;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Builder
@Getter
public class OrderPaymentsDtoRequest {

    @Min(1)
    private final long orderId;
}
