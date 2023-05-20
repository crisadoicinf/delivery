package com.crisado.delivery.service;

import com.crisado.delivery.dto.ProductDto;
import com.crisado.delivery.exception.StateException;
import com.crisado.delivery.dto.OrderDtoRequest;
import com.crisado.delivery.dto.OrderDto;
import com.crisado.delivery.dto.OrderSummaryDto;
import com.crisado.delivery.model.*;
import com.crisado.delivery.repository.*;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@Service
@Transactional
@AllArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final MenuItemRepository menuItemRepository;
    private final ProductRepository productRepository;
    private final RiderRepository riderRepository;
    private final ModelMapper mapper;

    /**
     * Retrieves a list of orders that have a delivery date between the
     * specified start and end dates.
     *
     * @param from The start date of the date range to search for orders.
     *
     * @param to The end date of the date range to search for orders.
     *
     * @return A list of orders that have a delivery date between the specified
     * start and end dates.
     */
    public List<OrderSummaryDto> getOrdersBetweenDates(ZonedDateTime from, ZonedDateTime to) {
        return orderRepository.findAllByDeliveryDateBetween(from, to).stream()
                .map(order -> mapper.map(order, OrderSummaryDto.class)).collect(toList());
    }

    /**
     * Retrieves a list of all products.
     *
     * @return A list of all products.
     */
    public List<ProductDto> getProducts() {
        return productRepository.findAll().stream().map(order -> mapper.map(order, ProductDto.class)).collect(toList());
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
                .collect(Collectors.toMap(OrdersCountByDay::day, OrdersCountByDay::total));
    }

    /**
     * Retrieves the order with the specified ID.
     *
     * @param orderId The ID of the order to retrieve.
     *
     * @return The order with the specified ID.
     *
     * @throws IllegalArgumentException if an order with the specified ID is not
     * found.
     */
    public OrderDto getOrder(long orderId) {
        var order = retrieveOrder(orderId);
        return mapper.map(order, OrderDto.class);
    }

    private Order retrieveOrder(long orderId) {
        return orderRepository.findById(orderId).orElseThrow(() -> new StateException("Order not found"));
    }

    /**
     * Creates a new order with the specified items and saves it.
     *
     * @param orderRequest The request object containing the items for the new
     * order.
     *
     * @return The newly created order.
     */
    public OrderDto createOrder(OrderDtoRequest orderRequest) {
        var order = saveOrder(Order.builder().items(new HashSet<>()).build(), orderRequest);
        return mapper.map(order, OrderDto.class);
    }

    /**
     * Updates the order with the specified ID with the items from the provided
     * order request and saves it.
     *
     * @param orderId The ID of the order to update.
     * @param orderRequest The request object containing the updated items for
     * the order.
     *
     * @return The updated order.
     *
     * @throws IllegalArgumentException if an order with the specified ID is not
     * found.
     */
    public OrderDto updateOrder(long orderId, OrderDtoRequest orderRequest) {
        var order = saveOrder(retrieveOrder(orderId), orderRequest);
        return mapper.map(order, OrderDto.class);
    }

    private Order saveOrder(Order order, OrderDtoRequest orderRequest) {
        buildOrder(orderRequest, order);
        buildOrderItems(order, orderRequest.getItems());
        orderRepository.save(order);
        return order;
    }

    /**
     * Deletes the order with the specified ID.
     *
     * @param orderId The ID of the order to delete.
     *
     * @throws IllegalArgumentException if an order with the specified ID is not
     * found.
     */
    public void deleteOrder(Long orderId) {
        Order order = retrieveOrder(orderId);
        orderRepository.delete(order);
    }

    /**
     * Builds an order object based on the provided order request and sets its
     * properties.
     *
     * @param orderRequest The request object containing the information for the
     * new order.
     * @param order The order object to populate with the information from the
     * request.
     *
     * @throws IllegalArgumentException if the provided rider ID in the order
     * request does not match an existing rider.
     */
    private void buildOrder(OrderDtoRequest orderRequest, Order order) {
        order.setDate(ZonedDateTime.now());
        order.setCustomerName(orderRequest.getCustomerName());
        order.setCustomerPhone(orderRequest.getCustomerPhone());
        OrderDelivery delivery = Optional.ofNullable(order.getDelivery()).orElse(new OrderDelivery());
        delivery.setOrder(order);
        delivery.setAddress(orderRequest.getDeliveryAddress());
        delivery.setDate(orderRequest.getDeliveryDate());
        delivery.setDateRange(orderRequest.getDeliveryDateRange());
        delivery.setPrice(orderRequest.getDeliveryPrice());
        delivery.setRider(Optional.ofNullable(orderRequest.getDeliveryRiderId()).map(riderId -> riderRepository
                .findById(riderId).orElseThrow(() -> new IllegalArgumentException("Rider not found"))).orElse(null));
        order.setDelivery(delivery);
        order.setDiscount(orderRequest.getDiscount());
        order.setNote(orderRequest.getNote());
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
        Set<OrderItem> orderItems = new HashSet<>(order.getItems());
        order.getItems().clear();
        if (itemsRequest != null) {
            int position = 0;
            for (OrderDtoRequest.OrderItem orderItemRequest : itemsRequest) {
                OrderItem orderItem = orderItems.stream()
                        .filter(oi -> orderItemRequest.getId() != null && orderItemRequest.getId().equals(oi.getId()))
                        .findFirst().orElse(new OrderItem());
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
