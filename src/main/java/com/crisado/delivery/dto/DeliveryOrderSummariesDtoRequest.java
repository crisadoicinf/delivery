package com.crisado.delivery.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.ZonedDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Builder
@Getter
public class DeliveryOrderSummariesDtoRequest {

    @NotNull
    private final ZonedDateTime date;
    @NotNull
    @Min(1)
    private final int riderId;
}
