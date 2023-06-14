package com.crisado.delivery.service;

import com.crisado.delivery.dto.DeliveryOrderSummariesDtoRequest;
import com.crisado.delivery.dto.OrderDeliveredDtoRequest;

import java.util.List;

import org.springframework.stereotype.Service;

import com.crisado.delivery.dto.OrderSummaryDto;
import com.crisado.delivery.dto.RiderDto;
import com.crisado.delivery.exception.OrderDeliveryException;
import com.crisado.delivery.exception.OrderNotFoundException;
import com.crisado.delivery.model.OrderDelivery;
import com.crisado.delivery.repository.OrderDeliveryRepository;
import com.crisado.delivery.repository.OrderRepository;
import com.crisado.delivery.repository.RiderRepository;

import jakarta.transaction.Transactional;
import static java.util.stream.Collectors.toList;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class OrderDeliveryService {

    private final Services services;
    private final OrderRepository orderRepository;
    private final OrderDeliveryRepository deliveryRepository;
    private final RiderRepository riderRepository;

    /**
     * Retrieves all orders for a specific delivery date and rider ID. Also
     * includes any orders that do not have a rider assigned.
     *
     * @param orderSumariesRequest the request object containing the necessary parameters
     *
     * @return a list of order summaries as objects.
     */
    public List<OrderSummaryDto> getOrderSummaries(DeliveryOrderSummariesDtoRequest orderSumariesRequest) {
        services.validate("Delivery Order Summary Request", orderSumariesRequest);
        var from = orderSumariesRequest.getDate();
        var to = from.plusDays(1);
        var riderId = orderSumariesRequest.getRiderId();

        return orderRepository.findAllByDeliveryDateBetweenAndOptionalRiderId(from, to, riderId)
                .stream()
                .map(order -> services.map(order, OrderSummaryDto.class))
                .toList();
    }

    /**
     * Updates the delivery status of an order by marking it as delivered or
     * undelivered.
     *
     * @param orderDeliveredRequest the request object containing the necessary parameters
     */
    @Transactional
    public void setOrderDelivered(OrderDeliveredDtoRequest orderDeliveredRequest) {
        services.validate("Order Delivered Request", orderDeliveredRequest);
        var orderId = orderDeliveredRequest.getOrderId();
        var delivered = orderDeliveredRequest.isDelivered();

        OrderDelivery delivery = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderDeliveryException(new OrderNotFoundException(orderId)))
                .getDelivery();
        delivery.setDelivered(delivered);
        deliveryRepository.save(delivery);
    }

    /**
     * Retrieves a list of riders for delivery.
     *
     * @return A list of riders.
     */
    public List<RiderDto> getRiders() {
        return riderRepository.findAll()
                .stream()
                .map(rider -> services.map(rider, RiderDto.class))
                .toList();
    }

}
