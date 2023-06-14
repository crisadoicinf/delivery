package com.crisado.delivery.dto;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
public class DeleteOrderDtoRequest extends GetOrderDtoRequest {

    public DeleteOrderDtoRequest(long orderId) {
        super(orderId);
    }

}
