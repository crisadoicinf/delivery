package com.crisado.delivery.service;

import com.crisado.delivery.dto.CreateOrderDtoRequest;
import com.crisado.delivery.dto.DateRangeDto;
import com.crisado.delivery.dto.DeleteOrderDtoRequest;
import com.crisado.delivery.dto.GetOrderDtoRequest;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.crisado.delivery.dto.ProductDto;
import com.crisado.delivery.dto.OrderDto;
import com.crisado.delivery.dto.OrderDtoRequest;
import com.crisado.delivery.dto.OrderSummaryDto;
import com.crisado.delivery.dto.UpdateOrderDtoRequest;
import com.crisado.delivery.exception.OrderException;
import com.crisado.delivery.exception.OrderNotFoundException;
import com.crisado.delivery.exception.ProductNotFoundException;
import com.crisado.delivery.exception.RiderNotFoundException;
import com.crisado.delivery.model.Order;
import com.crisado.delivery.model.OrderDelivery;
import com.crisado.delivery.model.OrderItem;
import com.crisado.delivery.model.OrdersCountByDay;
import com.crisado.delivery.model.Product;
import com.crisado.delivery.model.Rider;
import com.crisado.delivery.repository.OrderRepository;
import com.crisado.delivery.repository.ProductRepository;
import com.crisado.delivery.repository.RiderRepository;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.groups.Tuple.tuple;
import static org.assertj.core.data.MapEntry.entry;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private RiderRepository riderRepository;
    @Mock
    private Services services;
    @InjectMocks
    private OrderService service;
    @Captor
    private ArgumentCaptor<Order> orderCaptor;

    @Test
    void createOrderThrowsExceptionIfRiderNotFound() {
        var orderRequest = OrderDtoRequest.builder()
                .deliveryRiderId(1)
                .build();
        var createOrderDtoRequest = new CreateOrderDtoRequest(orderRequest);

        when(riderRepository.findById(orderRequest.getDeliveryRiderId()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.createOrder(createOrderDtoRequest))
                .isInstanceOf(OrderException.class)
                .hasMessage("Rider not found")
                .hasCauseInstanceOf(RiderNotFoundException.class);
    }

    @Test
    void createOrderThrowsExceptionIfProductNotFound() {
        var orderRequest = OrderDtoRequest.builder()
                .deliveryRiderId(1)
                .items(List.of(OrderDtoRequest.OrderItem.builder()
                        .productId(1)
                        .build()
                )
                )
                .build();
        var rider = new Rider();
        var createOrderDtoRequest = new CreateOrderDtoRequest(orderRequest);

        when(riderRepository.findById(orderRequest.getDeliveryRiderId()))
                .thenReturn(Optional.of(rider));
        when(productRepository.findById(orderRequest.getItems().get(0).getProductId()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.createOrder(createOrderDtoRequest))
                .isInstanceOf(OrderException.class)
                .hasMessage("Product not found")
                .hasCauseInstanceOf(ProductNotFoundException.class);
    }

    @Test
    void createOrder() {
        var deliveryDate = ZonedDateTime.now();
        var deliveryDateRange = deliveryDate.plusDays(1);
        var orderRequest = OrderDtoRequest.builder()
                .customerName("crisado")
                .customerPhone("12345678")
                .note("my order")
                .deliveryAddress("123 some street")
                .deliveryDate(deliveryDate)
                .deliveryDateRange(deliveryDateRange)
                .deliveryRiderId(1)
                .deliveryPrice(1.4)
                .discount(10)
                .items(List.of(OrderDtoRequest.OrderItem.builder()
                        .productId(1)
                        .quantity(2)
                        .note("something")
                        .build(),
                        OrderDtoRequest.OrderItem.builder()
                                .productId(2)
                                .quantity(1)
                                .build()
                )
                )
                .build();
        var rider = new Rider();
        var product1 = Product.builder().price(10).build();
        var product2 = Product.builder().price(15).build();
        var orderDto = new OrderDto();

        when(riderRepository.findById(orderRequest.getDeliveryRiderId()))
                .thenReturn(Optional.of(rider));
        when(productRepository.findById(1))
                .thenReturn(Optional.of(product1));
        when(productRepository.findById(2))
                .thenReturn(Optional.of(product2));
        when(services.map(any(Order.class), eq(OrderDto.class)))
                .thenReturn(orderDto);

        assertThat(service.createOrder(new CreateOrderDtoRequest(orderRequest)))
                .isSameAs(orderDto);
        verify(orderRepository)
                .save(orderCaptor.capture());
        var order = orderCaptor.getValue();
        assertThat(order)
                .extracting(
                        Order::getCustomerName,
                        Order::getCustomerPhone,
                        Order::getNote,
                        Order::getDiscount
                )
                .containsExactly(
                        "crisado",
                        "12345678",
                        "my order",
                        10D
                );
        assertThat(order.getDelivery())
                .extracting(
                        OrderDelivery::getAddress,
                        OrderDelivery::getDate,
                        OrderDelivery::getDateRange,
                        OrderDelivery::getRider,
                        OrderDelivery::getPrice
                )
                .containsExactly(
                        "123 some street",
                        deliveryDate,
                        deliveryDateRange,
                        rider,
                        1.4D
                );
        assertThat(order.getItems())
                .extracting(
                        OrderItem::getPosition,
                        OrderItem::getProduct,
                        OrderItem::getQuantity,
                        OrderItem::getUnitPrice,
                        OrderItem::getTotalPrice,
                        OrderItem::getNote
                )
                .containsExactlyInAnyOrder(
                        tuple(
                                0,
                                product1,
                                2,
                                10D,
                                20D,
                                "something"
                        ),
                        tuple(
                                1,
                                product2,
                                1,
                                15D,
                                15D,
                                null
                        )
                );

    }

    @Test
    void deleteOrder() {
        var orderId = 1L;
        var order = new Order();

        service.deleteOrder(new DeleteOrderDtoRequest(orderId));
        verify(orderRepository)
                .deleteById(orderId);
    }

    @Test
    void getOrderThrowsExceptionIfOrderNotFound() {
        var orderId = 1L;
        var orderDtoRequest = new GetOrderDtoRequest(orderId);

        when(orderRepository.findById(orderId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getOrder(orderDtoRequest))
                .isInstanceOf(OrderNotFoundException.class)
                .hasMessage("Order not found");
    }

    @Test
    void getOrder() {
        var orderId = 1L;
        var orderDto = new OrderDto();

        when(orderRepository.findById(orderId))
                .thenReturn(Optional.of(new Order()));
        when(services.map(any(Order.class), eq(OrderDto.class)))
                .thenReturn(orderDto);

        assertThat(service.getOrder(new GetOrderDtoRequest(orderId)))
                .isSameAs(orderDto);
    }

    @Test
    void getOrdersBetweenDates() {
        var from = ZonedDateTime.now();
        var to = ZonedDateTime.now().minusDays(1);

        var orderDto1 = new OrderSummaryDto();
        var orderDto2 = new OrderSummaryDto();
        var orders = List.of(new Order(), new Order());

        when(orderRepository.findAllByDeliveryDateBetween(from, to))
                .thenReturn(orders);
        when(services.map(any(Order.class), eq(OrderSummaryDto.class)))
                .thenReturn(orderDto1, orderDto2);

        assertThat(service.getOrderSummaries(new DateRangeDto(from, to)))
                .containsExactly(orderDto1, orderDto2);
    }

    @Test
    void getDailyAmountOfOrdersByMonth() {
        int month = 1;
        var d1 = new OrdersCountByDay(1, 10);
        var d3 = new OrdersCountByDay(3, 2);
        var d4 = new OrdersCountByDay(4, 1);
        var ordersCount = List.of(d1, d3, d4);

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
    void getProducts() {
        var productDto1 = new ProductDto();
        var productDto2 = new ProductDto();
        var products = List.of(new Product(), new Product());

        when(productRepository.findAll())
                .thenReturn(products);
        when(services.map(any(Product.class), eq(ProductDto.class)))
                .thenReturn(productDto1, productDto2);

        assertThat(service.getProducts())
                .containsExactly(productDto1, productDto2);
    }

    @Test
    void updateOrderThrowsExceptionIfOrderNotFound() {
        var orderId = 1L;
        var orderRequest = new OrderDtoRequest();
        var updateOrderDtoRequest = new UpdateOrderDtoRequest(orderId, orderRequest);

        when(orderRepository.findById(orderId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.updateOrder(updateOrderDtoRequest))
                .isInstanceOf(OrderNotFoundException.class)
                .hasMessage("Order not found");

    }

    @Test
    void updateOrderThrowsExceptionIfRiderNotFound() {
        var orderId = 1L;
        var orderRequest = OrderDtoRequest.builder()
                .deliveryRiderId(1)
                .build();
        var order = new Order();
        var updateOrderDtoRequest = new UpdateOrderDtoRequest(orderId, orderRequest);

        when(orderRepository.findById(orderId))
                .thenReturn(Optional.of(order));
        when(riderRepository.findById(orderRequest.getDeliveryRiderId()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.updateOrder(updateOrderDtoRequest))
                .isInstanceOf(OrderException.class)
                .hasMessage("Rider not found")
                .hasCauseInstanceOf(RiderNotFoundException.class);

    }

    @Test
    void updateOrderThrowsExceptionIfProductNotFound() {
        var orderId = 1L;
        var orderRequest = OrderDtoRequest.builder()
                .deliveryRiderId(1)
                .items(List.of(OrderDtoRequest.OrderItem.builder()
                        .productId(1)
                        .build()
                )
                )
                .build();
        var order = Order.builder()
                .items(new HashSet<>())
                .build();
        var rider = new Rider();
        var updateOrderDtoRequest = new UpdateOrderDtoRequest(orderId, orderRequest);

        when(orderRepository.findById(orderId))
                .thenReturn(Optional.of(order));
        when(riderRepository.findById(orderRequest.getDeliveryRiderId()))
                .thenReturn(Optional.of(rider));
        when(productRepository.findById(orderRequest.getItems().get(0).getProductId()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.updateOrder(updateOrderDtoRequest))
                .isInstanceOf(OrderException.class)
                .hasMessage("Product not found")
                .hasCauseInstanceOf(ProductNotFoundException.class);
    }

    @Test
    void updateOrder() {
        var deliveryDate = ZonedDateTime.now();
        var deliveryDateRange = deliveryDate.plusDays(1);
        var orderId = 1L;
        var orderRequest = OrderDtoRequest.builder()
                .customerName("crisado")
                .customerPhone("12345678")
                .note("my order")
                .deliveryAddress("123 some street")
                .deliveryDate(deliveryDate)
                .deliveryDateRange(deliveryDateRange)
                .deliveryRiderId(1)
                .deliveryPrice(1.4)
                .discount(10)
                .items(List.of(OrderDtoRequest.OrderItem.builder()
                        .productId(2)
                        .quantity(1)
                        .note("new product")
                        .build(),
                        OrderDtoRequest.OrderItem.builder()
                                .id(1L)
                                .productId(1)
                                .quantity(2)
                                .note("something")
                                .build()
                )
                )
                .build();
        var rider = new Rider();
        var product1 = Product.builder().price(10).build();
        var product2 = Product.builder().price(15).build();
        var order = Order.builder()
                .id(orderId)
                .customerName("cristian")
                .customerPhone("87654321")
                .note("first order")
                .delivery(
                        OrderDelivery.builder()
                                .id(1L)
                                .address("321 other street")
                                .date(ZonedDateTime.now().minusDays(10))
                                .dateRange(ZonedDateTime.now().minusDays(9))
                                .delivered(false)
                                .price(2)
                                .build()
                )
                .discount(5)
                .items(
                        new HashSet<>(
                                List.of(
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
                                )
                        )
                )
                .build();
        var orderDto = new OrderDto();

        when(orderRepository.findById(orderId))
                .thenReturn(Optional.of(order));
        when(riderRepository.findById(orderRequest.getDeliveryRiderId()))
                .thenReturn(Optional.of(rider));
        when(productRepository.findById(1))
                .thenReturn(Optional.of(product1));
        when(productRepository.findById(2))
                .thenReturn(Optional.of(product2));
        when(services.map(order, OrderDto.class))
                .thenReturn(orderDto);

        assertThat(service.updateOrder(new UpdateOrderDtoRequest(orderId, orderRequest)))
                .isSameAs(orderDto);
        verify(orderRepository)
                .save(order);
        assertThat(order)
                .extracting(
                        Order::getCustomerName,
                        Order::getCustomerPhone,
                        Order::getNote,
                        Order::getDiscount
                )
                .containsExactly(
                        "crisado",
                        "12345678",
                        "my order",
                        10D
                );
        assertThat(order.getDelivery())
                .extracting(
                        OrderDelivery::getAddress,
                        OrderDelivery::getDate,
                        OrderDelivery::getDateRange,
                        OrderDelivery::getRider,
                        OrderDelivery::getPrice
                )
                .containsExactly(
                        "123 some street",
                        deliveryDate,
                        deliveryDateRange,
                        rider,
                        1.4D
                );
        assertThat(order.getItems())
                .extracting(
                        OrderItem::getId,
                        OrderItem::getPosition,
                        OrderItem::getProduct,
                        OrderItem::getQuantity,
                        OrderItem::getUnitPrice,
                        OrderItem::getTotalPrice,
                        OrderItem::getNote
                )
                .containsExactlyInAnyOrder(
                        tuple(
                                1L,
                                1,
                                product1,
                                2,
                                10D,
                                20D,
                                "something"
                        ),
                        tuple(
                                null,
                                0,
                                product2,
                                1,
                                15D,
                                15D,
                                "new product"
                        )
                );

        verify(orderRepository)
                .save(order);
    }

}
