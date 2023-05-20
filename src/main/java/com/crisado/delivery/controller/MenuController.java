package com.crisado.delivery.controller;

import com.crisado.delivery.model.Product;
import com.crisado.delivery.repository.ProductRepository;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import org.springframework.http.ResponseEntity;

@AllArgsConstructor
@RestController
@RequestMapping("/api/menus")
public class MenuController {

    private final ProductRepository productRepository;

    @GetMapping("/items")
    public ResponseEntity<List<Product>> getProducts() {
        var products = productRepository.findAll();
        return ResponseEntity.ok(products);
    }

}
