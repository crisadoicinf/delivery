package com.crisado.delivery.controller;

import static java.util.stream.Collectors.toList;

import java.time.ZonedDateTime;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.crisado.delivery.dto.OrderListResponse;
import com.crisado.delivery.dto.RiderSelectResponse;
import com.crisado.delivery.repository.RiderRepository;
import com.crisado.delivery.service.OrderDeliveryService;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
@RequestMapping("/api/delivery")
public class DeliveryController {

    private final OrderDeliveryService deliveryService;
    private final RiderRepository riderRepository;
    private final ModelMapper mapper;

    @GetMapping("/orders")
    public List<OrderListResponse> getOrders(@RequestParam ZonedDateTime date, @RequestParam int riderId) {
        return deliveryService.getOrders(date, riderId)
        		.stream()
                .map(order -> mapper.map(order, OrderListResponse.class))
                .collect(toList());
    }

    @PutMapping("/orders/{orderId}")
    public void deliverOrder(@PathVariable long orderId, @RequestParam boolean delivered) {
    	deliveryService.markOrderDelivered(orderId, delivered);
    }

    @GetMapping("/riders")
    public List<RiderSelectResponse> getRiders() {
        return riderRepository.findAll().stream()
                .map(rider -> mapper.map(rider, RiderSelectResponse.class))
                .collect(toList());
    }
}
