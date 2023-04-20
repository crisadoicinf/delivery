package com.crisado.delivery.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.AllArgsConstructor;

@Controller
@RequestMapping("/")
@AllArgsConstructor
public class AppController {

    @GetMapping({
            "/",
            "/receive/orders",
            "/receive/orders/{orderId}",
            "/cooking",
            "/delivery"
    })
    public String index() {
        return "/index.html";
    }
}
