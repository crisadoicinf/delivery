package com.crisado.delivery.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.time.ZonedDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Builder
@Getter
@Valid
public class DateRangeDto {

    @NotNull
    private final ZonedDateTime from;
    @NotNull
    private final ZonedDateTime to;

}
