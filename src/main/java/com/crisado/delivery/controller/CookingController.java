package com.crisado.delivery.controller;

import com.crisado.delivery.model.CookingProduct;
import com.crisado.delivery.service.CookingService;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;
import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/api/cooking")
public class CookingController {

    private final CookingService cookingService;

    @GetMapping
    public List<CookingProduct> getProductsToCook(
            @RequestParam ZonedDateTime from,
            @RequestParam ZonedDateTime to) {
        return cookingService.getProductsToCook(from, to);
    }

}
