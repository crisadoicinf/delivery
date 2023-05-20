package com.crisado.delivery.controller;

import com.crisado.delivery.model.CookingProduct;
import com.crisado.delivery.service.CookingService;

import lombok.AllArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;
import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/api/cooking")
public class CookingController {

    private final CookingService cookingService;

    @GetMapping
    public ResponseEntity<List<CookingProduct>> getProductsToCook(
            @RequestParam ZonedDateTime from,
            @RequestParam ZonedDateTime to) {
        var products = cookingService.getProductsToCook(from, to);
        return ResponseEntity.ok(products);
    }

}
