package com.crisado.delivery.controller;

import com.crisado.delivery.dto.DeliveryOrderSummariesDtoRequest;
import com.crisado.delivery.dto.OrderDeliveredDtoRequest;
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
import org.springframework.web.bind.annotation.ModelAttribute;

@AllArgsConstructor
@RestController
@RequestMapping("/api/delivery")
public class OrderDeliveryController {

    private final OrderDeliveryService deliveryService;

    @GetMapping("/orders")
    public ResponseEntity<List<OrderSummaryDto>> getOrderSummaries(@ModelAttribute DeliveryOrderSummariesDtoRequest request) {
        var orders = deliveryService.getOrderSummaries(request);
        return ResponseEntity.ok(orders);
    }

    @PutMapping("/orders/{orderId}")
    public ResponseEntity<Void> setOrderDelivered(@PathVariable long orderId, @RequestParam boolean delivered) {
        var request = OrderDeliveredDtoRequest.builder()
                .orderId(orderId)
                .delivered(delivered)
                .build();
        deliveryService.setOrderDelivered(request);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/riders")
    public ResponseEntity<List<RiderDto>> getRiders() {
        var riders = deliveryService.getRiders();
        return ResponseEntity.ok(riders);
    }
}
