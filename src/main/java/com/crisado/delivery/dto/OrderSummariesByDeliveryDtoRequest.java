package com.crisado.delivery.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Builder
@Getter
public class OrderSummariesByDeliveryDtoRequest {

    @NotNull
    @Valid
    private final DateRangeDto dateRange;
    @Min(1)
    private final Integer riderId;
}
