package com.crisado.delivery.controller;

import com.crisado.delivery.dto.CreateOrderDtoRequest;
import com.crisado.delivery.dto.DateRangeDto;
import com.crisado.delivery.dto.DeleteOrderDtoRequest;
import com.crisado.delivery.dto.GetOrderDtoRequest;
import java.util.List;
import java.util.Map;

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
import com.crisado.delivery.dto.UpdateOrderDtoRequest;
import com.crisado.delivery.service.OrderService;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.ModelAttribute;

@AllArgsConstructor
@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    @GetMapping("/products")
    public ResponseEntity<List<ProductDto>> getProducts() {
        var orders = orderService.getProducts();
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/count")
    public ResponseEntity<Map<Integer, Long>> getDailyAmountOfOrdersByMonth(@RequestParam int month) {
        var orders = orderService.getDailyAmountOfOrdersByMonth(month);
        return ResponseEntity.ok(orders);
    }

    @GetMapping
    public ResponseEntity<List<OrderSummaryDto>> getOrderSummaries(@ModelAttribute DateRangeDto request) {
        var orders = orderService.getOrderSummaries(request);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderDto> getOrder(@PathVariable long id) {
        var getOrderRequest = new GetOrderDtoRequest(id);
        var order = orderService.getOrder(getOrderRequest);
        return ResponseEntity.ok(order);
    }

    @PostMapping
    public ResponseEntity<OrderDto> createOrder(@RequestBody OrderDtoRequest orderRequest) {
        var reateRequest = new CreateOrderDtoRequest(orderRequest);
        var order = orderService.createOrder(reateRequest);
        return ResponseEntity.ok(order);
    }

    @PutMapping("/{id}")
    public ResponseEntity<OrderDto> updateOrder(@PathVariable long id, @RequestBody OrderDtoRequest orderRequest) {
        var updateRequest = new UpdateOrderDtoRequest(id, orderRequest);
        var order = orderService.updateOrder(updateRequest);
        return ResponseEntity.ok(order);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable long id) {
        var deleteOrderRequest = new DeleteOrderDtoRequest(id);
        orderService.deleteOrder(deleteOrderRequest);
        return ResponseEntity.noContent().build();
    }

}
