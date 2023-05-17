package com.crisado.delivery.service;

import java.time.ZonedDateTime;
import java.util.List;
import org.springframework.stereotype.Service;

import com.crisado.delivery.model.CookingProduct;
import com.crisado.delivery.repository.OrderItemRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class CookingService {

    private final OrderItemRepository orderItemRepository;

    public List<CookingProduct> getProductsToCook(ZonedDateTime from, ZonedDateTime to) {
        return orderItemRepository.findAllByDeliveryDate(from, to);
    }
    
}
