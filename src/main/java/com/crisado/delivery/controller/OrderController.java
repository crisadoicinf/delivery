package com.crisado.delivery.controller;

import com.crisado.delivery.dto.OrderSummaryDto;
import com.crisado.delivery.dto.OrderProductList;
import com.crisado.delivery.dto.OrderRequest;
import com.crisado.delivery.dto.OrderResponse;
import com.crisado.delivery.model.Order;
import com.crisado.delivery.service.OrderService;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;

@AllArgsConstructor
@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;
    private final ModelMapper mapper;

    @GetMapping("/products")
    public List<OrderProductList> getAllProducts() {
        return orderService.getAllProducts().stream()
                .map(order -> mapper.map(order, OrderProductList.class))
                .collect(toList());
    }

    @GetMapping("/count")
    public Map<Integer, Long> getDailyAmountOfOrdersByMonth(@RequestParam int month) {
        return orderService.getDailyAmountOfOrdersByMonth(month);
    }

    @GetMapping
    public List<OrderSummaryDto> getOrdersBetweenDates(
            @RequestParam ZonedDateTime from,
            @RequestParam ZonedDateTime to
    ) {
        return orderService.getOrdersBetweenDates(from, to).stream()
                .map(order -> mapper.map(order, OrderSummaryDto.class))
                .collect(toList());
    }

    @GetMapping("/{id}")
    public OrderResponse getOrder(@PathVariable long id) {
        Order order = orderService.getOrder(id);
        return mapper.map(order, OrderResponse.class);
    }

    @PostMapping
    public OrderResponse createOrder(@RequestBody OrderRequest orderRequest) {
        Order order = orderService.createOrder(orderRequest);
        return mapper.map(order, OrderResponse.class);
    }

    @PutMapping("/{id}")
    public OrderResponse updateOrder(@PathVariable long id, @RequestBody OrderRequest orderRequest) {
        Order order = orderService.updateOrder(id, orderRequest);
        return mapper.map(order, OrderResponse.class);
    }

    @DeleteMapping("/{id}")
    public void deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);
    }


}
