package com.crisado.delivery.controller;

import com.crisado.delivery.dto.OrderListResponse;
import com.crisado.delivery.service.OrderService;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.ZonedDateTime;
import java.util.List;

import static java.util.stream.Collectors.toList;

@AllArgsConstructor
@RestController
@RequestMapping("/api/delivery")
public class DeliveryController {

    private final OrderService orderService;
    private final ModelMapper mapper;

    @GetMapping("/orders")
    public List<OrderListResponse> getOrders(@RequestParam ZonedDateTime date) {
        ZonedDateTime to = date.plusDays(1);
        return orderService.getOrders(date, to).stream()
                .map(order -> mapper.map(order, OrderListResponse.class))
                .collect(toList());
    }
}
