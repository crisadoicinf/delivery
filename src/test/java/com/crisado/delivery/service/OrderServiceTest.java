package com.crisado.delivery.service;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.crisado.delivery.dto.OrderRequest;
import com.crisado.delivery.model.Order;
import com.crisado.delivery.model.OrderDelivery;
import com.crisado.delivery.model.OrderItem;
import com.crisado.delivery.model.OrdersCountByDay;
import com.crisado.delivery.model.Product;
import com.crisado.delivery.model.Rider;
import com.crisado.delivery.repository.MenuItemRepository;
import com.crisado.delivery.repository.OrderItemRepository;
import com.crisado.delivery.repository.OrderRepository;
import com.crisado.delivery.repository.ProductRepository;
import com.crisado.delivery.repository.RiderRepository;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.groups.Tuple.tuple;
import static org.assertj.core.data.MapEntry.entry;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;
    @Mock
    private OrderItemRepository orderItemRepository;
    @Mock
    private MenuItemRepository menuItemRepository;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private RiderRepository riderRepository;
    @InjectMocks
    private OrderService service;
    @Captor
    private ArgumentCaptor<Order> orderCaptor;

    @Test
    void createOrderThrowsExceptionIfRiderNotFound() {
        var request = OrderRequest.builder()
                .deliveryRiderId(1)
                .build();

        when(riderRepository.findById(request.getDeliveryRiderId()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.createOrder(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Rider not found");
    }

    @Test
    void createOrderThrowsExceptionIfProductNotFound() {
        var request = OrderRequest.builder()
                .deliveryRiderId(1)
                .items(List.of(
                        OrderRequest.OrderItem.builder()
                                .productId(1)
                                .build()))
                .build();
        var rider = new Rider();

        when(riderRepository.findById(request.getDeliveryRiderId()))
                .thenReturn(Optional.of(rider));
        when(menuItemRepository.findById(request.getItems().get(0).getProductId()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.createOrder(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Product not found");
    }

    @Test
    void createOrder() {
        var request = OrderRequest.builder()
                .customerName("crisado")
                .customerPhone("12345678")
                .note("my order")
                .deliveryAddress("123 some street")
                .deliveryDate(ZonedDateTime.of(2023, 5, 6, 14, 30, 0, 0, ZoneId.of("UTC")))
                .deliveryDateRange(ZonedDateTime.of(2023, 5, 6, 18, 00, 0, 0, ZoneId.of("UTC")))
                .deliveryRiderId(1)
                .deliveryPrice(1.4)
                .discount(10)
                .items(List.of(
                        OrderRequest.OrderItem.builder()
                                .productId(1)
                                .quantity(2)
                                .note("something")
                                .build(),
                        OrderRequest.OrderItem.builder()
                                .productId(2)
                                .quantity(1)
                                .build()))
                .build();
        var rider = new Rider();
        var product1 = Product.builder().price(10).build();
        var product2 = Product.builder().price(15).build();

        when(riderRepository.findById(request.getDeliveryRiderId()))
                .thenReturn(Optional.of(rider));
        when(menuItemRepository.findById(1))
                .thenReturn(Optional.of(product1));
        when(menuItemRepository.findById(2))
                .thenReturn(Optional.of(product2));

        var order = service.createOrder(request);
        verify(orderRepository, times(1))
                .save(order);
        assertThat(order)
                .extracting(
                        Order::getCustomerName,
                        Order::getCustomerPhone,
                        Order::getNote,
                        Order::getDiscount)
                .containsExactly(
                        "crisado",
                        "12345678",
                        "my order",
                        10D);
        assertThat(order.getDelivery())
                .extracting(
                        OrderDelivery::getAddress,
                        OrderDelivery::getDate,
                        OrderDelivery::getDateRange,
                        OrderDelivery::getRider,
                        OrderDelivery::getPrice)
                .containsExactly(
                        "123 some street",
                        ZonedDateTime.of(2023, 5, 6, 14, 30, 0, 0, ZoneId.of("UTC")),
                        ZonedDateTime.of(2023, 5, 6, 18, 00, 0, 0, ZoneId.of("UTC")),
                        rider,
                        1.4D);
        assertThat(order.getItems())
                .extracting(
                        OrderItem::getPosition,
                        OrderItem::getProduct,
                        OrderItem::getQuantity,
                        OrderItem::getUnitPrice,
                        OrderItem::getTotalPrice,
                        OrderItem::getNote)
                .containsExactlyInAnyOrder(
                        tuple(0,
                                product1,
                                2,
                                10D,
                                20D,
                                "something"),
                        tuple(1,
                                product2,
                                1,
                                15D,
                                15D,
                                null)
                );

    }

    @Test
    void deleteOrderThrowsExceptionIfOrderNotFound() {
        var orderId = 1L;

        when(orderRepository.findById(orderId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.deleteOrder(orderId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Order not found");

    }

    @Test
    void deleteOrder() {
        var orderId = 1L;
        var order = new Order();

        when(orderRepository.findById(orderId))
                .thenReturn(Optional.of(order));

        service.deleteOrder(orderId);
        verify(orderRepository, times(1))
                .delete(order);
    }

    @Test
    void getOrderThrowsExceptionIfOrderNotFound() {
        var orderId = 1L;

        when(orderRepository.findById(orderId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getOrder(orderId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Order not found");
    }

    @Test
    void getOrder() {
        var orderId = 1L;
        var order = new Order();

        when(orderRepository.findById(orderId))
                .thenReturn(Optional.of(order));

        assertThat(service.getOrder(orderId))
                .isSameAs(order);
    }

    @Test
    void getOrdersBetweenDates() {
        var from = ZonedDateTime.of(2023, 5, 6, 0, 0, 0, 0, ZoneId.of("UTC"));
        var to = ZonedDateTime.of(2023, 5, 7, 0, 0, 0, 0, ZoneId.of("UTC"));

        var order1 = new Order();
        var order2 = new Order();
        var orders = List.of(order1, order2);

        when(orderRepository.findAllByDeliveryDateBetween(from, to))
                .thenReturn(orders);

        assertThat(service.getOrdersBetweenDates(from, to))
                .isSameAs(orders)
                .containsExactly(order1, order2);
    }

    @Test
    void getDailyAmountOfOrdersByMonth() {
        int month = 1;
        var d1 = new OrdersCountByDay(1, 10);
        var d3 = new OrdersCountByDay(3, 2);
        var d4 = new OrdersCountByDay(4, 1);
        var ordersCount = List.of(d1 ,d3, d4);

        when(orderRepository.countTotalByDeliveryMonthDay(month))
                .thenReturn(ordersCount);

        assertThat(service.getDailyAmountOfOrdersByMonth(month))
                .contains(
                        entry(1, 10L), 
                        entry(3, 2L), 
                        entry(4, 1L)
                );
    }

    @Test
    void getAllProducts() {
        var product1 = new Product();
        var product2 = new Product();
        var products = List.of(product1, product2);

        when(productRepository.findAll())
                .thenReturn(products);

        assertThat(service.getAllProducts())
                .isSameAs(products)
                .containsExactly(product1, product2);
    }

    @Test
    void updateOrderThrowsExceptionIfOrderNotFound() {
        var orderId = 1L;
        var request = new OrderRequest();

        when(orderRepository.findById(orderId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.updateOrder(orderId, request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Order not found");

    }

    @Test
    void updateOrderThrowsExceptionIfRiderNotFound() {
        var orderId = 1L;
        var request = OrderRequest.builder()
                .deliveryRiderId(1)
                .build();
        var order = new Order();

        when(orderRepository.findById(orderId))
                .thenReturn(Optional.of(order));
        when(riderRepository.findById(request.getDeliveryRiderId()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.updateOrder(orderId, request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Rider not found");

    }

    @Test
    void updateOrderThrowsExceptionIfProductNotFound() {
        var orderId = 1L;
        var request = OrderRequest.builder()
                .deliveryRiderId(1)
                .items(List.of(
                        OrderRequest.OrderItem.builder()
                                .productId(1)
                                .build()))
                .build();
        var order = Order.builder()
                .items(new HashSet<>())
                .build();
        var rider = new Rider();

        when(orderRepository.findById(orderId))
                .thenReturn(Optional.of(order));
        when(riderRepository.findById(request.getDeliveryRiderId()))
                .thenReturn(Optional.of(rider));
        when(menuItemRepository.findById(request.getItems().get(0).getProductId()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.updateOrder(orderId, request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Product not found");
    }

    @Test
    void updateOrder() {
        var orderId = 1L;
        var request = OrderRequest.builder()
                .customerName("crisado")
                .customerPhone("12345678")
                .note("my order")
                .deliveryAddress("123 some street")
                .deliveryDate(ZonedDateTime.of(2023, 5, 6, 14, 30, 0, 0, ZoneId.of("UTC")))
                .deliveryDateRange(ZonedDateTime.of(2023, 5, 6, 18, 00, 0, 0, ZoneId.of("UTC")))
                .deliveryRiderId(1)
                .deliveryPrice(1.4)
                .discount(10)
                .items(List.of(
                        OrderRequest.OrderItem.builder()
                                .productId(2)
                                .quantity(1)
                                .note("new product")
                                .build(),
                        OrderRequest.OrderItem.builder()
                                .id(1L)
                                .productId(1)
                                .quantity(2)
                                .note("something")
                                .build()
                ))
                .build();
        var rider = new Rider();
        var product1 = Product.builder().price(10).build();
        var product2 = Product.builder().price(15).build();
        var order = Order.builder()
                .id(orderId)
                .customerName("cristian")
                .customerPhone("87654321")
                .note("first order")
                .delivery(OrderDelivery.builder()
                        .id(1L)
                        .address("321 other street")
                        .date(ZonedDateTime.of(2023, 5, 6, 13, 00, 0, 0, ZoneId.of("UTC")))
                        .dateRange(ZonedDateTime.of(2023, 5, 6, 17, 30, 0, 0, ZoneId.of("UTC")))
                        .delivered(false)
                        .price(2)
                        .build()
                )
                .discount(5)
                .items(new HashSet<>(List.of(
                        OrderItem.builder()
                                .id(1L)
                                .product(product2)
                                .quantity(1)
                                .note("init")
                                .position(0)
                                .unitPrice(product2.getPrice())
                                .totalPrice(1 * product2.getPrice())
                                .build(),
                        OrderItem.builder()
                                .id(2L)
                                .product(product1)
                                .quantity(2)
                                .note("init")
                                .position(1)
                                .unitPrice(product1.getPrice())
                                .totalPrice(1 * product1.getPrice())
                                .build()
                )))
                .build();

        when(orderRepository.findById(orderId))
                .thenReturn(Optional.of(order));
        when(riderRepository.findById(request.getDeliveryRiderId()))
                .thenReturn(Optional.of(rider));
        when(menuItemRepository.findById(1))
                .thenReturn(Optional.of(product1));
        when(menuItemRepository.findById(2))
                .thenReturn(Optional.of(product2));

        
        assertThat(service.updateOrder(orderId, request))
                .isSameAs(order)
                .extracting(
                        Order::getCustomerName,
                        Order::getCustomerPhone,
                        Order::getNote,
                        Order::getDiscount)
                .containsExactly(
                        "crisado",
                        "12345678",
                        "my order",
                        10D);
        assertThat(order.getDelivery())
                .extracting(
                        OrderDelivery::getAddress,
                        OrderDelivery::getDate,
                        OrderDelivery::getDateRange,
                        OrderDelivery::getRider,
                        OrderDelivery::getPrice)
                .containsExactly(
                        "123 some street",
                        ZonedDateTime.of(2023, 5, 6, 14, 30, 0, 0, ZoneId.of("UTC")),
                        ZonedDateTime.of(2023, 5, 6, 18, 00, 0, 0, ZoneId.of("UTC")),
                        rider,
                        1.4D);    
        assertThat(order.getItems())
                .extracting(
                        OrderItem::getId,
                        OrderItem::getPosition,
                        OrderItem::getProduct,
                        OrderItem::getQuantity,
                        OrderItem::getUnitPrice,
                        OrderItem::getTotalPrice,
                        OrderItem::getNote)
                .containsExactlyInAnyOrder(
                        tuple(
                                1L,
                                1,
                                product1,
                                2,
                                10D,
                                20D,
                                "something"),
                        tuple(
                                null,
                                0,
                                product2,
                                1,
                                15D,
                                15D,
                                "new product")
                );

        verify(orderRepository, times(1))
                .save(order);
    }

}
