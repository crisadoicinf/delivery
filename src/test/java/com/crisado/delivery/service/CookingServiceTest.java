package com.crisado.delivery.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.time.ZonedDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.crisado.delivery.model.CookingProduct;
import com.crisado.delivery.repository.OrderItemRepository;

@ExtendWith(MockitoExtension.class)
public class CookingServiceTest {

    @Mock
    private OrderItemRepository orderItemRepository;
    @InjectMocks
    private CookingService cookingService;

    @Test
    void getProductsToCook() {
        var product1 = new CookingProduct("product1", "note1", 1);
        var product2 = new CookingProduct("product2", "note1", 2);
        var from = ZonedDateTime.now();
        var to = from.plusDays(1);

        when(orderItemRepository.findAllByDeliveryDate(from, to))
                .thenReturn(List.of(product1, product2));

        assertThat(cookingService.getProductsToCook(from, to))
                .containsExactly(product1, product2);
    }
}
