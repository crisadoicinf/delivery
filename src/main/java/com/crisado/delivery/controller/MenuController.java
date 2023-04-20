package com.crisado.delivery.controller;

import com.crisado.delivery.model.Product;
import com.crisado.delivery.repository.MenuItemRepository;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/api/menus")
public class MenuController {

    private final MenuItemRepository menuItemRepository;

    @GetMapping("/items")
    public List<Product> getMenuItems() {
        return menuItemRepository.findAll();
    }

}
