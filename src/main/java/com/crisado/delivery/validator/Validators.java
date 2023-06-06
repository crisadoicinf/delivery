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

    public <T extends Object> void validate(String message, T object, Class<?>... groups) {
        throwException(message, validator.validate(new ValidatorWrap(object)));
        throwException(message, validator.validate(object, groups));
    }

    private void throwException(String message, Set<ConstraintViolation<Object>> errors) {
        if (!errors.isEmpty()) {
            throw new ConstraintViolationException(message, errors);
        }
    }

    @AllArgsConstructor
    @Getter
    private class ValidatorWrap<T> {

        @NotNull
        private final T value;

    }
}
