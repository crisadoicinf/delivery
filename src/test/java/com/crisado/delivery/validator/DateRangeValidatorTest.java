package com.crisado.delivery.validator;

import com.crisado.delivery.dto.DateRangeDto;
import jakarta.validation.ConstraintValidatorContext;
import java.time.ZonedDateTime;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class DateRangeValidatorTest {

    private final DateRangeValidator validator = new DateRangeValidator();

    @Test
    public void isValidIfToIsAfterFrom() {
        DateRangeDto dateRangeDto = new DateRangeDto(
                ZonedDateTime.parse("2023-06-01T00:00:00Z"),
                ZonedDateTime.parse("2023-06-05T00:00:00Z")
        );
        ConstraintValidatorContext ctx = null;

        assertThat(validator.isValid(dateRangeDto, ctx)).isTrue();
    }

    @Test
    public void isValidIfDateRangeIsNull() {
        DateRangeDto dateRangeDto = null;
        ConstraintValidatorContext ctx = null;

        assertThat(validator.isValid(dateRangeDto, ctx)).isTrue();

    }

    @Test
    public void isValidIsFromIsNull() {
        DateRangeDto dateRangeDto = new DateRangeDto(
                null,
                ZonedDateTime.parse("2023-06-05T00:00:00Z")
        );
        ConstraintValidatorContext ctx = null;

        assertThat(validator.isValid(dateRangeDto, ctx)).isTrue();
    }

    @Test
    public void isValidIfToIsNull() {
        DateRangeDto dateRangeDto = new DateRangeDto(
                ZonedDateTime.parse("2023-06-01T00:00:00Z"),
                null
        );
        ConstraintValidatorContext ctx = null;

        assertThat(validator.isValid(dateRangeDto, ctx)).isTrue();

    }

    @Test
    public void isInValidToIsNotAfterFrom() {
        DateRangeDto dateRangeDto = new DateRangeDto(
                ZonedDateTime.parse("2023-06-05T00:00:00Z"),
                ZonedDateTime.parse("2023-06-01T00:00:00Z")
        );
        ConstraintValidatorContext ctx = null;

        assertThat(validator.isValid(dateRangeDto, ctx)).isFalse();
    }
}
