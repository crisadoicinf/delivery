package com.crisado.delivery.service;

import com.crisado.delivery.dto.DateRangeDto;

import java.time.ZonedDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.crisado.delivery.model.CookingProduct;
import com.crisado.delivery.repository.OrderItemRepository;
import com.crisado.delivery.validator.DateRange;
import com.crisado.delivery.validator.Validators;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.constraints.NotNull;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.doNothing;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;

@ExtendWith(MockitoExtension.class)
public class CookingServiceTest {

    @Mock
    private OrderItemRepository orderItemRepository;
    @Mock
    private Validators validator;
    @InjectMocks
    private CookingService cookingService;

    @Test
    public void getProductsToCook() {
        var product1 = new CookingProduct("product1", "note1", 1);
        var product2 = new CookingProduct("product2", "note1", 2);
        var from = ZonedDateTime.parse("2023-06-01T00:00:00Z");
        var to = ZonedDateTime.parse("2023-06-05T00:00:00Z");
        var dateRangeDto = new DateRangeDto(from, to);

        when(orderItemRepository.findAllByDeliveryDate(from, to))
                .thenReturn(List.of(product1, product2));

        assertThat(cookingService.getProductsToCook(dateRangeDto))
                .containsExactly(product1, product2);
    }

    @Test
    public void getProductsThrowsExceptionIfDateRangeIsNull() {
        var exception = new ConstraintViolationException(null);
        
        doThrow(exception)
                .when(validator)
                .validate(anyString(), isNull(), eq(NotNull.class));

        assertThatThrownBy(() -> cookingService.getProductsToCook(null))
                .isSameAs(exception);
    }

    @Test
    public void getProductsThrowsExceptionIfDateRangeIsInvalid() {
        var dateRangeDto = new DateRangeDto(null, null);
        var exception = new ConstraintViolationException(null);
        
        doNothing()
                .when(validator)
                .validate(anyString(), eq(dateRangeDto), eq(NotNull.class));
        doThrow(exception)
                .when(validator)
                .validate(anyString(), eq(dateRangeDto), eq(DateRange.class));

        assertThatThrownBy(() -> cookingService.getProductsToCook(dateRangeDto))
                .isSameAs(exception);
    }
}
