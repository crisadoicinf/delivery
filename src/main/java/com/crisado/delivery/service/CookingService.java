package com.crisado.delivery.service;

import com.crisado.delivery.dto.DateRangeDto;
import java.util.List;
import org.springframework.stereotype.Service;

import com.crisado.delivery.model.OrderItemQuantity;
import com.crisado.delivery.repository.OrderItemRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class CookingService {

    private final Services services;
    private final OrderItemRepository orderItemRepository;

    public List<OrderItemQuantity> getProductsToCookByDateRange(DateRangeDto dateRange) {
        services.validate("dateRange", dateRange);
        var from = dateRange.getFrom();
        var to = dateRange.getTo();

        return orderItemRepository.findAllSumQuantityByDateBetween(from, to);
    }

}
