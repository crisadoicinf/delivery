package com.crisado.delivery.service;

import com.crisado.delivery.dto.DateRangeDto;

import java.time.ZonedDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.crisado.delivery.model.OrderItemQuantity;
import jakarta.validation.ConstraintViolationException;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doThrow;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isNull;
import com.crisado.delivery.repository.OrderItemRepository;

@ExtendWith(MockitoExtension.class)
class CookingServiceTest {

    @Mock
    private OrderItemRepository orderItemRepository;
    @Mock
    private Services services;
    @InjectMocks
    private CookingService cookingService;

    @Test
    void getProductsToCookByDateRange() {
        var product1 = new OrderItemQuantity("product1", "note1", 1);
        var product2 = new OrderItemQuantity("product2", "note1", 2);
        var from = ZonedDateTime.parse("2023-06-01T00:00:00Z");
        var to = ZonedDateTime.parse("2023-06-05T00:00:00Z");
        var dateRangeDto = new DateRangeDto(from, to);

        when(orderItemRepository.findAllSumQuantityByDateBetween(from, to))
                .thenReturn(List.of(product1, product2));

        assertThat(cookingService.getProductsToCookByDateRange(dateRangeDto))
                .containsExactly(product1, product2);
    }

    @Test
    void getProductsToCookByDateRangeThrowsExceptionIfDateRangeIsNull() {
        var exception = new ConstraintViolationException(null);
        
        doThrow(exception)
                .when(services)
                .validate(anyString(), isNull(), any());

        assertThatThrownBy(() -> cookingService.getProductsToCookByDateRange(null))
                .isSameAs(exception);
    }

    @Test
    void getProductsToCookByDateRangeThrowsExceptionIfDateRangeIsInvalid() {
        var dateRangeDto = new DateRangeDto(null, null);
        var exception = new ConstraintViolationException(null);
        
        doThrow(exception)
                .when(services)
                .validate(anyString(), eq(dateRangeDto), any());

        assertThatThrownBy(() -> cookingService.getProductsToCookByDateRange(dateRangeDto))
                .isSameAs(exception);
    }
}
