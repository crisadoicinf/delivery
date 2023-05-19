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

    /**
     * Retrieves a list of CookingProduct objects representing the products and
     * quantities to cook
     * within a specified delivery time range.
     *
     * @param deliveryFrom The start date and time of the delivery time range.
     * @param deliveryTo   The end date and time of the delivery time range.
     * @return A list of CookingProduct objects to be cooked within the specified
     *         delivery time range.
     */
    public List<CookingProduct> getProductsToCook(ZonedDateTime deliveryFrom, ZonedDateTime deliverTo) {
        return orderItemRepository.findAllByDeliveryDate(deliveryFrom, deliverTo);
    }

}
