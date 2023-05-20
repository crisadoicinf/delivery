package com.crisado.delivery.controller;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.crisado.delivery.dto.OrderDto;
import com.crisado.delivery.dto.OrderDtoRequest;
import com.crisado.delivery.dto.OrderSummaryDto;
import com.crisado.delivery.dto.ProductDto;
import com.crisado.delivery.service.OrderService;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;
    private final ModelMapper mapper;

    @GetMapping("/products")
    public ResponseEntity<List<ProductDto>> getProducts() {
        var orders = orderService.getProducts();
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/count")
    public Map<Integer, Long> getDailyAmountOfOrdersByMonth(@RequestParam int month) {
        return orderService.getDailyAmountOfOrdersByMonth(month);
    }

    @GetMapping
    public ResponseEntity<List<OrderSummaryDto>> getOrdersBetweenDates(
            @RequestParam ZonedDateTime from,
            @RequestParam ZonedDateTime to) {
        var orders = orderService.getOrdersBetweenDates(from, to);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderDto> getOrder(@PathVariable long id) {
        var order = orderService.getOrder(id);
        return ResponseEntity.ok(order);
    }

    @PostMapping
    public ResponseEntity<OrderDto> createOrder(@RequestBody OrderDtoRequest orderRequest) {
        var order = orderService.createOrder(orderRequest);
        return ResponseEntity.ok(order);
    }

    @PutMapping("/{id}")
    public ResponseEntity<OrderDto> updateOrder(@PathVariable long id, @RequestBody OrderDtoRequest orderRequest) {
        var order = orderService.updateOrder(id, orderRequest);
        return ResponseEntity.ok(order);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);
        return ResponseEntity.noContent().build();
    }

}
