package com.crisado.delivery.controller;

import java.time.ZonedDateTime;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.crisado.delivery.dto.OrderSummaryDto;
import com.crisado.delivery.dto.RiderDto;
import com.crisado.delivery.service.OrderDeliveryService;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
@RequestMapping("/api/delivery")
public class DeliveryController {

    private final OrderDeliveryService deliveryService;

    @GetMapping("/orders")
    public ResponseEntity<List<OrderSummaryDto>> getOrders(@RequestParam ZonedDateTime date, @RequestParam int riderId) {
        var orders = deliveryService.getOrders(date, riderId);
        return ResponseEntity.ok(orders);
    }

    @PutMapping("/orders/{orderId}")
    public ResponseEntity<Void> setOrderDelivered(@PathVariable long orderId, @RequestParam boolean delivered) {
    	deliveryService.setOrderDelivered(orderId, delivered);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/riders")
    public ResponseEntity<List<RiderDto>> getRiders() {
        var riders = deliveryService.getRiders();
        return ResponseEntity.ok(riders);
    }
}
