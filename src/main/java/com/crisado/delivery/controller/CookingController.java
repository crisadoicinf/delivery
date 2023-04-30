package com.crisado.delivery.controller;

import com.crisado.delivery.model.CookingProduct;
import com.crisado.delivery.repository.OrderItemRepository;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;
import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/api/cooking")
public class CookingController {

    private final OrderItemRepository orderItemRepository;

    @GetMapping
    public List<CookingProduct> getProductsToCook(
            @RequestParam ZonedDateTime from,
            @RequestParam ZonedDateTime to) {
        return orderItemRepository.findAllByDeliveryDate(from, to);
    }

}
