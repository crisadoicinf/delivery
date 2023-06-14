package com.crisado.delivery.validator;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import jakarta.validation.constraints.NotNull;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class Validators {

    private final Validator validator;

    public <T extends Object> void validate(String name, T object, Class<?>... groups) {
        throwException(name, validator.validate(new ValidatorWrap<T>(object)));
        throwException(name, validator.validate(object, groups));
    }

    private <T extends Object> void throwException(String name, Set<ConstraintViolation<T>> errors) {
        if (name == null) {
            throw new IllegalArgumentException("'name' must nut be null");
        }
        if (errors == null) {
            throw new IllegalArgumentException("'errors' must not be null");
        }
        if (!errors.isEmpty()) {
            throw new ConstraintViolationException(name + " has invalid values", errors);
        }
    }

    @AllArgsConstructor
    @Getter
    private class ValidatorWrap<T> {

        @NotNull
        private final T value;

    }
}
