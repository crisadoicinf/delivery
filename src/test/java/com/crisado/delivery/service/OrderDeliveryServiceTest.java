package com.crisado.delivery.service;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.crisado.delivery.model.Order;
import com.crisado.delivery.model.OrderDelivery;
import com.crisado.delivery.repository.OrderDeliveryRepository;
import com.crisado.delivery.repository.OrderRepository;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
public class OrderDeliveryServiceTest {

        @Mock
        private OrderService orderService;
        @Mock
        private OrderRepository orderRepository;
        @Mock
        private OrderDeliveryRepository deliveryRepository;
        @InjectMocks
        private OrderDeliveryService service;

        @Test
        void getOrders() {
                var date = ZonedDateTime.of(2023, 5, 6, 0, 0, 0, 0, ZoneId.of("UTC"));
                var riderId = 1;
                var order1 = new Order();
                var order2 = new Order();
                var orders = List.of(order1, order2);

                when(orderRepository.findAllByDeliveryDateAndRiderId(date, date.plusDays(1), riderId))
                                .thenReturn(orders);

                var actualOrders = service.getOrders(date, riderId);
                assertThat(actualOrders)
                                .isEqualTo(orders)
                                .containsExactly(order1, order2);
        }

        @Test
        void markOrderDeliveredThrowsExceptionIfOrderNotFound() {
                var orderId = 1;

                when(orderService.getOrder(orderId))
                                .thenThrow(new IllegalArgumentException("Order not found"));

                assertThatThrownBy(() -> service.markOrderDelivered(orderId, true))
                                .isInstanceOf(IllegalArgumentException.class)
                                .hasMessage("Order not found");
        }

        @Test
        void markOrderDeliveredAsTrue() {
                var orderId = 1;
                var order = Order.builder()
                                .delivery(OrderDelivery.builder()
                                                .delivered(false)
                                                .build())
                                .build();

                when(orderService.getOrder(orderId))
                                .thenReturn(order);

                service.markOrderDelivered(orderId, true);

                verify(deliveryRepository, times(1))
                                .save(order.getDelivery());
                assertThat(order.getDelivery().isDelivered())
                                .isTrue();
        }

}
