package com.crisado.delivery.dto;

import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
public class UpdateOrderDtoRequest extends CreateOrderDtoRequest {

    @Min(1)
    private final long id;

    public UpdateOrderDtoRequest(long id, OrderDtoRequest order) {
        super(order);
        this.id = id;
    }
}
