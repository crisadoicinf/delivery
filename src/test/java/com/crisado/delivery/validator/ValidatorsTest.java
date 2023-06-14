package com.crisado.delivery.validator;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.hibernate.validator.internal.engine.ConstraintViolationImpl;

import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
class ValidatorsTest {

    @Mock
    private Validator validator;
    @InjectMocks
    private Validators validatorUtil;

    @Test
    void validateDoNotThrowExceptionIfThereIsNoError() {
        Set<ConstraintViolation<Object>> errors = Set.of();

        when(validator.validate(any(), any()))
                .thenReturn(errors);

        validatorUtil.validate("", "");
    }

    @Test
    void validateThrowsExceptionIfObjectIsNull() {
        var errors = Set.of(newConstraint());

        when(validator.validate(any(), any()))
                .thenReturn(errors);

        assertThatThrownBy(() -> validatorUtil.validate("bean", null))
                .isInstanceOf(ConstraintViolationException.class)
                .hasMessage("bean has invalid values")
                .asInstanceOf(InstanceOfAssertFactories.type(ConstraintViolationException.class))
                .extracting(ConstraintViolationException::getConstraintViolations)
                .isEqualTo(errors);
    }

    @Test
    void validateThrowsExceptionIfThereIsAnError() {
        var errors = Set.of(newConstraint());

        when(validator.validate(any(), any()))
                .thenReturn(Set.of());
        when(validator.validate(any(), any()))
                .thenReturn(errors);

        assertThatThrownBy(() -> validatorUtil.validate("bean", ""))
                .isInstanceOf(ConstraintViolationException.class)
                .hasMessage("bean has invalid values")
                .asInstanceOf(InstanceOfAssertFactories.type(ConstraintViolationException.class))
                .extracting(ConstraintViolationException::getConstraintViolations)
                .isEqualTo(errors);
    }

    private <T extends Object> ConstraintViolation<T> newConstraint() {
        return ConstraintViolationImpl.forBeanValidation(
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null);
    }
}
