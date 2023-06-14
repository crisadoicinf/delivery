package com.crisado.delivery.service;

import com.crisado.delivery.dto.CreateOrderDtoRequest;
import com.crisado.delivery.dto.DateRangeDto;
import com.crisado.delivery.dto.DeleteOrderDtoRequest;
import com.crisado.delivery.dto.GetOrderDtoRequest;
import com.crisado.delivery.dto.ProductDto;
import com.crisado.delivery.dto.OrderDtoRequest;
import com.crisado.delivery.dto.OrderDto;
import com.crisado.delivery.dto.OrderSummaryDto;
import com.crisado.delivery.dto.UpdateOrderDtoRequest;
import com.crisado.delivery.exception.OrderException;
import com.crisado.delivery.exception.OrderNotFoundException;
import com.crisado.delivery.exception.ProductNotFoundException;
import com.crisado.delivery.exception.RiderNotFoundException;
import com.crisado.delivery.model.*;
import com.crisado.delivery.repository.*;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.*;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

@Service
@Transactional
@AllArgsConstructor
public class OrderService {
    
    private final Services services;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final RiderRepository riderRepository;
    
    public List<ProductDto> getProducts() {
        return productRepository.findAll()
                .stream()
                .map(order -> services.map(order, ProductDto.class))
                .toList();
    }

    /**
     * Retrieves the total number of orders delivered each day of the specified
     * month and returns it as a map with the day as key and the number of
     * orders as the value. Only days with orders are present in the map
     *
     * @param month The month for which to retrieve the daily order count.
     *
     * @return A map of delivery days in the specified month to the total number
     * of orders delivered on that day.
     */
    public Map<Integer, Long> getDailyAmountOfOrdersByMonth(int month) {
        return orderRepository.countTotalByDeliveryMonthDay(month).stream()
                .collect(toMap(OrdersCountByDay::day, OrdersCountByDay::total));
    }
    
    public List<OrderSummaryDto> getOrderSummaries(DateRangeDto dateRangeRequest) {
        services.validate("Order Summary Request", dateRangeRequest);
        var from = dateRangeRequest.getFrom();
        var to = dateRangeRequest.getTo();
        
        return orderRepository.findAllByDeliveryDateBetween(from, to)
                .stream()
                .map(order -> services.map(order, OrderSummaryDto.class))
                .collect(toList());
    }

    /**
     * Retrieves the order with the specified ID.
     *
     * @param getOrderDtoRequest The ID of the order to retrieve.
     *
     * @return The order with the specified ID.
     *
     * @throws IllegalArgumentException if an order with the specified ID is not
     * found.
     */
    public OrderDto getOrder(GetOrderDtoRequest getOrderDtoRequest) {
        services.validate("Get Order Request", getOrderDtoRequest);
        var orderId = getOrderDtoRequest.getOrderId();
        
        var order = findOrderById(orderId);
        return services.map(order, OrderDto.class);
    }

    /**
     * Creates a new order with the specified items and saves it.
     *
     * @param createOrderRequest The request object containing the items for the
     * new order.
     *
     * @return The newly created order.
     */
    public OrderDto createOrder(CreateOrderDtoRequest createOrderRequest) {
        services.validate("Create Order Request", createOrderRequest);
        var newOrder = createOrderRequest.getOrder();
        
        var order = Order.builder().items(new HashSet<>()).build();
        saveOrder(order, newOrder);
        return services.map(order, OrderDto.class);
    }

    /**
     * Updates the order with the specified ID with the items from the provided
     * order request and saves it.
     *
     * @param updateOrderRequest The request object containing the updated items
     * for the order.
     *
     * @return The updated order.
     *
     * @throws IllegalArgumentException if an order with the specified ID is not
     * found.
     */
    public OrderDto updateOrder(UpdateOrderDtoRequest updateOrderRequest) {
        services.validate("Update Order Request", updateOrderRequest);
        var orderId = updateOrderRequest.getId();
        var newOrder = updateOrderRequest.getOrder();
        
        var order = findOrderById(orderId);
        saveOrder(order, newOrder);
        return services.map(order, OrderDto.class);
    }
    
    private void saveOrder(Order order, OrderDtoRequest orderRequest) {
        services.validate("Order Request", orderRequest);
        
        buildOrder(order, orderRequest);
        buildOrderItems(order, orderRequest.getItems());
        orderRepository.save(order);
    }

    /**
     * Deletes the order with the specified ID.
     *
     * @param deleteOrderDtoRequest The ID of the order to delete.
     *
     * @throws IllegalArgumentException if an order with the specified ID is not
     * found.
     */
    public void deleteOrder(DeleteOrderDtoRequest deleteOrderDtoRequest) {
        services.validate("Delete Order Request", deleteOrderDtoRequest);
        var orderId = deleteOrderDtoRequest.getOrderId();
        
        orderRepository.deleteById(orderId);
    }
    
    private Order findOrderById(long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(id));
    }

    /**
     * Builds an order object based on the provided order request and sets its
     * properties.
     *
     * @param order The order object to populate with the information from the
     * request.
     * @param orderRequest The request object containing the information for the
     * new order.
     *
     * @throws IllegalArgumentException if the provided rider ID in the order
     * request does not match an existing rider.
     */
    private void buildOrder(Order order, OrderDtoRequest orderRequest) {
        order.setDate(ZonedDateTime.now());
        order.setCustomerName(orderRequest.getCustomerName());
        order.setCustomerPhone(orderRequest.getCustomerPhone());
        order.setDiscount(orderRequest.getDiscount());
        order.setNote(orderRequest.getNote());
        
        var delivery = Optional.ofNullable(order.getDelivery())
                .orElse(new OrderDelivery());
        delivery.setOrder(order);
        delivery.setAddress(orderRequest.getDeliveryAddress());
        delivery.setDate(orderRequest.getDeliveryDate());
        delivery.setDateRange(orderRequest.getDeliveryDateRange());
        delivery.setPrice(orderRequest.getDeliveryPrice());
        delivery.setRider(Optional.ofNullable(orderRequest.getDeliveryRiderId())
                .map(riderId -> riderRepository.findById(riderId)
                .orElseThrow(() -> new OrderException(new RiderNotFoundException(orderRequest.getDeliveryRiderId()))))
                .orElse(null));
        order.setDelivery(delivery);
    }

    /**
     * Builds and updates the set of order items for an order based on the
     * provided list of order item requests.
     *
     * @param order The order for which to update the order items.
     * @param itemsRequest The list of order item requests.
     *
     * @throws IllegalArgumentException if a product for an order item cannot be
     * found.
     */
    private void buildOrderItems(Order order, List<OrderDtoRequest.OrderItem> itemsRequest) {
        var orderItems = new HashSet<>(order.getItems());
        order.getItems().clear();
        if (itemsRequest != null) {
            int position = 0;
            for (OrderDtoRequest.OrderItem orderItemRequest : itemsRequest) {
                var orderItem = orderItems.stream()
                        .filter(oi -> orderItemRequest.getId() != null && orderItemRequest.getId().equals(oi.getId()))
                        .findFirst().orElse(new OrderItem());
                var product = productRepository.findById(orderItemRequest.getProductId())
                        .orElseThrow(() -> new OrderException(new ProductNotFoundException(orderItemRequest.getProductId())));
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
