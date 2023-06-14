package com.crisado.delivery.dto;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@AllArgsConstructor
@Getter
public class GetOrderDtoRequest {

    @Min(1)
    private final long orderId;

}
