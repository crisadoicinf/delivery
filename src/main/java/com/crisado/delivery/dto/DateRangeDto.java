package com.crisado.delivery.dto;

import com.crisado.delivery.validator.DateRange;
import jakarta.validation.constraints.NotNull;
import java.time.ZonedDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@DateRange
public class DateRangeDto {

    @NotNull
    private final ZonedDateTime from;
    @NotNull
    private final ZonedDateTime to;

}
