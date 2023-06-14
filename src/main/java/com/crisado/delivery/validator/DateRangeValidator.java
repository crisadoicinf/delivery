package com.crisado.delivery.validator;

import com.crisado.delivery.dto.DateRangeDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Valid;
import java.time.ZonedDateTime;

public class DateRangeValidator implements ConstraintValidator<Valid, DateRangeDto> {

    /**
     * Validates a DateRangeDto object to ensure that the 'to' date is after the
     * 'from' date.
     *
     * @param dateRangeDto The DateRangeDto to be validated.
     * @param ctx The ConstraintValidatorContext for custom constraint
     * validation.
     * @return {@code true} if the DateRangeDto is valid, {@code false}
     * otherwise.
     */
    @Override
    public boolean isValid(DateRangeDto dateRangeDto, ConstraintValidatorContext ctx) {
        if (dateRangeDto != null) {
            ZonedDateTime from = dateRangeDto.getFrom();
            ZonedDateTime to = dateRangeDto.getTo();
            if (from != null && to != null) {
                return to.isAfter(from);
            }
        }
        return true;
    }

}
