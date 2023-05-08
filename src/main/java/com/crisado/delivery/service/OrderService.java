package com.crisado.delivery.service;

import com.crisado.delivery.dto.OrderRequest;
import com.crisado.delivery.model.*;
import com.crisado.delivery.repository.*;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
@AllArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final MenuItemRepository menuItemRepository;
    private final ProductRepository productRepository;
    private final RiderRepository riderRepository;

    public List<Order> getOrdersBetweenDates(ZonedDateTime from, ZonedDateTime to) {
        return orderRepository.findAllByDeliveryDateBetween(from, to);
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Map<Integer, Long> getDailyAmountOfOrdersByMonth(int month) {
        return orderRepository.countTotalByDeliveryMonthDay(month)
                .stream()
                .collect(Collectors.toMap(OrdersCountByDay::day, OrdersCountByDay::total));
    }

    public Order getOrder(long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));
    }

    public Order createOrder(OrderRequest orderRequest) {
        Order order = Order.builder()
            .items(new HashSet<>())
            .build();
        return saveOrder(order, orderRequest);
    }

    public Order updateOrder(long orderId, OrderRequest orderRequest) {
        Order order = getOrder(orderId);
        return saveOrder(order, orderRequest);
    }

    private Order saveOrder(Order order, OrderRequest orderRequest) {
        buildOrder(orderRequest, order);
        buildOrderItems(order, orderRequest.getItems());
        orderRepository.save(order);
        return order;
    }

    public void deleteOrder(Long orderId) {
        Order order = getOrder(orderId);
        orderRepository.delete(order);
    }

    private void buildOrder(OrderRequest orderRequest, Order order) {
        order.setDate(ZonedDateTime.now());
        order.setCustomerName(orderRequest.getCustomerName());
        order.setCustomerPhone(orderRequest.getCustomerPhone());
        OrderDelivery delivery = Optional.ofNullable(order.getDelivery())
                .orElse(new OrderDelivery());
        delivery.setOrder(order);
        delivery.setAddress(orderRequest.getDeliveryAddress());
        delivery.setDate(orderRequest.getDeliveryDate());
        delivery.setDateRange(orderRequest.getDeliveryDateRange());
        delivery.setPrice(orderRequest.getDeliveryPrice());
        delivery.setRider(Optional.ofNullable(orderRequest.getDeliveryRiderId())
                .map(riderId -> riderRepository.findById(riderId)
                        .orElseThrow(() -> new IllegalArgumentException("Rider not found")))
                .orElse(null));
        order.setDelivery(delivery);
        order.setDiscount(orderRequest.getDiscount());
        order.setNote(orderRequest.getNote());
    }

    private void buildOrderItems(Order order, List<OrderRequest.OrderItem> itemsRequest) {
        Set<OrderItem> orderItems = new HashSet<>(order.getItems());
        order.getItems().clear();
        if (itemsRequest != null) {
            int position = 0;
            for (OrderRequest.OrderItem orderItemRequest : itemsRequest) {
                OrderItem orderItem = orderItems
                        .stream()
                        .filter(oi -> orderItemRequest.getId() != null && orderItemRequest.getId().equals(oi.getId()))
                        .findFirst()
                        .orElse(new OrderItem());
                Product product = menuItemRepository.findById(orderItemRequest.getProductId())
                        .orElseThrow(() -> new IllegalArgumentException("Product not found"));
                orderItem.setOrder(order);
                orderItem.setProduct(product);
                orderItem.setPosition(position++);
                orderItem.setQuantity(orderItemRequest.getQuantity());
                orderItem.setUnitPrice(product.getPrice());
                orderItem.setTotalPrice(product.getPrice() * orderItemRequest.getQuantity());
                orderItem.setNote(orderItemRequest.getNote());
                order.getItems().add(orderItem);
            }
        }
    }

}
