package com.crisado.delivery.controller;

import com.crisado.delivery.dto.DateRangeDto;
import com.crisado.delivery.model.CookingProduct;
import com.crisado.delivery.service.CookingService;

import lombok.AllArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/api/cooking")
public class CookingController {

    private final CookingService cookingService;

    @GetMapping
    public ResponseEntity<List<CookingProduct>> getProductsToCook(
            @ModelAttribute DateRangeDto range) {
        var products = cookingService.getProductsToCook(range);
        return ResponseEntity.ok(products);
    }

}
