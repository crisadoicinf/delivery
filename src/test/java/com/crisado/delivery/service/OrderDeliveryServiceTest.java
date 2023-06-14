package com.crisado.delivery.service;

import com.crisado.delivery.dto.DeliveryOrderSummariesDtoRequest;
import com.crisado.delivery.dto.OrderDeliveredDtoRequest;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.crisado.delivery.dto.OrderSummaryDto;
import com.crisado.delivery.dto.RiderDto;
import com.crisado.delivery.exception.OrderDeliveryException;
import com.crisado.delivery.exception.OrderNotFoundException;
import com.crisado.delivery.model.Order;
import com.crisado.delivery.model.OrderDelivery;
import com.crisado.delivery.model.Rider;
import com.crisado.delivery.repository.OrderDeliveryRepository;
import com.crisado.delivery.repository.OrderRepository;
import com.crisado.delivery.repository.RiderRepository;

@ExtendWith(MockitoExtension.class)
class OrderDeliveryServiceTest {

    @Mock
    private OrderRepository orderRepository;
    @Mock
    private OrderDeliveryRepository deliveryRepository;
    @Mock
    private RiderRepository riderRepository;
    @Mock
    private Services services;
    @InjectMocks
    private OrderDeliveryService service;

    @Test
    void getOrderSummaries() {
        var date = ZonedDateTime.now();
        var riderId = 1;
        var orders = List.of(new Order(), new Order());
        var orderDto1 = new OrderSummaryDto();
        var orderDto2 = new OrderSummaryDto();

        when(orderRepository.findAllByDeliveryDateBetweenAndOptionalRiderId(date, date.plusDays(1), riderId))
                .thenReturn(orders);
        when(services.map(any(Order.class), eq(OrderSummaryDto.class)))
                .thenReturn(orderDto1, orderDto2);

        assertThat(service.getOrderSummaries(new DeliveryOrderSummariesDtoRequest(date, riderId)))
                .containsExactlyInAnyOrder(orderDto1, orderDto2);
    }

    @Test
    void setOrderDeliveredThrowsExceptionIfOrderNotFound() {
        var orderId = 1L;
        var orderDeliveredDtoRequest = new OrderDeliveredDtoRequest(orderId, true);

        when(orderRepository.findById(orderId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.setOrderDelivered(orderDeliveredDtoRequest))
                .isInstanceOf(OrderDeliveryException.class)
                .hasMessage("Order not found")
                .hasCauseInstanceOf(OrderNotFoundException.class);
    }

    @Test
    void setOrderDeliveredAsTrue() {
        var orderId = 1L;
        var order = Order.builder()
                .delivery(OrderDelivery.builder()
                        .delivered(false)
                        .build())
                .build();

        when(orderRepository.findById(orderId))
                .thenReturn(Optional.of(order));

        service.setOrderDelivered(new OrderDeliveredDtoRequest(orderId, true));

        verify(deliveryRepository, times(1))
                .save(order.getDelivery());
        assertThat(order.getDelivery().isDelivered())
                .isTrue();
    }

    @Test
    void getRiders() {
        var rider = List.of(new Rider(), new Rider());
        var riderDto1 = new RiderDto();
        var riderDto2 = new RiderDto();

        when(riderRepository.findAll())
                .thenReturn(rider);
        when(services.map(any(Rider.class), eq(RiderDto.class)))
                .thenReturn(riderDto1, riderDto2);

        assertThat(service.getRiders())
                .containsExactlyInAnyOrder(riderDto1, riderDto2);
    }

}
