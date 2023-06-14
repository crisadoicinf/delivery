package com.crisado.delivery.dto;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
public class GetOrderPaymentDtoRequest extends GetOrderDtoRequest {

    public GetOrderPaymentDtoRequest(long orderId) {
        super(orderId);
    }

}
